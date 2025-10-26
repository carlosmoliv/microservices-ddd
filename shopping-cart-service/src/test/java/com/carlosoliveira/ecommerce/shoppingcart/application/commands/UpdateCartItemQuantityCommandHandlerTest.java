package com.carlosoliveira.ecommerce.shoppingcart.application.commands;

import com.carlosoliveira.ecommerce.shoppingcart.application.ports.CartRepository;
import com.carlosoliveira.ecommerce.shoppingcart.application.commands.handlers.AddItemToCartCommandHandler;
import com.carlosoliveira.ecommerce.shoppingcart.application.commands.handlers.UpdateCartItemQuantityCommandHandler;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.CartItemQuantityUpdatedEvent;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.ItemAddedToCartEvent;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("UpdateCartItemQuantityCommandHandler Integration Tests")
class UpdateCartItemQuantityCommandHandlerTest {

    @Autowired
    private UpdateCartItemQuantityCommandHandler sut;

    @Autowired
    private AddItemToCartCommandHandler addItemHandler;

    @Autowired
    private CartRepository cartRepository;

    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();
    private final BigDecimal price = new BigDecimal("100.00");

    @TestConfiguration
    static class TestConfig {
        @Bean
        public EventCaptor eventCaptor() {
            return new EventCaptor();
        }
    }

    @Getter
    @Component
    static class EventCaptor {
        private final List<Object> capturedEvents = new ArrayList<>();

        @EventListener
        public void captureAddedEvent(ItemAddedToCartEvent event) {
            capturedEvents.add(event);
        }

        @EventListener
        public void captureUpdatedEvent(CartItemQuantityUpdatedEvent event) {
            capturedEvents.add(event);
        }

        public void clear() {
            capturedEvents.clear();
        }
    }

    @Autowired
    private EventCaptor eventCaptor;

    @BeforeEach
    void setup() {
        cartRepository.deleteAll();
        eventCaptor.clear();
    }

    @Test
    void shouldUpdateItemQuantityAndPublishEvent() {
        // Arrange
        addItemToCart(3);
        eventCaptor.clear();

        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(
                userId,
                productId,
                5
        );

        // Act
        sut.handle(command);

        // Assert
        assertThat(eventCaptor.getCapturedEvents())
                .hasSize(1)
                .first()
                .isInstanceOf(CartItemQuantityUpdatedEvent.class)
                .satisfies(event -> {
                    CartItemQuantityUpdatedEvent e = (CartItemQuantityUpdatedEvent) event;
                    assertThat(e.userId()).isEqualTo(userId);
                    assertThat(e.productId()).isEqualTo(productId);
                    assertThat(e.previousQuantity()).isEqualTo(3);
                    assertThat(e.newQuantity()).isEqualTo(5);
                    assertThat(e.quantityDifference()).isEqualTo(2);
                });

        // Assert
        var persistedCart = cartRepository.findByUserIdWithItems(userId);
        assertThat(persistedCart).isPresent();
        assertThat(persistedCart.get().getItems()).hasSize(1);
        assertThat(persistedCart.get().getItems().getFirst().getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldIncreaseQuantityWhenNewQuantityIsHigher() {
        // Arrange
        addItemToCart(2);
        eventCaptor.clear();

        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(
                userId,
                productId,
                7
        );

        // Act
        sut.handle(command);

        // Assert
        assertThat(eventCaptor.getCapturedEvents())
                .hasSize(1)
                .first()
                .satisfies(event -> {
                    CartItemQuantityUpdatedEvent e = (CartItemQuantityUpdatedEvent) event;
                    assertThat(e.quantityDifference()).isEqualTo(5);
                    assertThat(e.quantityDifference()).isPositive();
                });

        var persistedCart = cartRepository.findByUserIdWithItems(userId);
        assertThat(persistedCart.get().getItems().getFirst().getQuantity()).isEqualTo(7);
    }

    @Test
    void shouldDecreaseQuantityWhenNewQuantityIsLower() {
        // Arrange
        addItemToCart(10);
        eventCaptor.clear();

        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(
                userId,
                productId,
                3
        );

        // Act
        sut.handle(command);

        // Assert
        assertThat(eventCaptor.getCapturedEvents())
                .hasSize(1)
                .first()
                .satisfies(event -> {
                    CartItemQuantityUpdatedEvent e = (CartItemQuantityUpdatedEvent) event;
                    assertThat(e.quantityDifference()).isEqualTo(-7); // 3 - 10 = -7
                    assertThat(e.quantityDifference()).isNegative();
                });

        var persistedCart = cartRepository.findByUserIdWithItems(userId);
        assertThat(persistedCart.get().getItems().getFirst().getQuantity()).isEqualTo(3);
    }

    @Test
    void shouldNotPublishEventWhenQuantityRemainsTheSame() {
        // Arrange
        addItemToCart(5);
        eventCaptor.clear();

        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(
                userId,
                productId,
                5
        );

        // Act
        sut.handle(command);

        // Assert
        assertThat(eventCaptor.getCapturedEvents()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenCartDoesNotExist() {
        UUID nonExistentUserId = UUID.randomUUID();
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(
                nonExistentUserId,
                productId,
                5
        );

        assertThatThrownBy(() -> sut.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cart not found for user");
    }

    @Test
    void shouldThrowExceptionWhenItemDoesNotExistInCart() {
        // Arrange
        addItemToCart(3);
        eventCaptor.clear();

        UUID differentProductId = UUID.randomUUID();
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(
                userId,
                differentProductId,
                5
        );

        // Act & Assert
        assertThatThrownBy(() -> sut.handle(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item not found in cart");
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        addItemToCart(3);

        assertThatThrownBy(() -> new UpdateCartItemQuantityCommand(userId, productId, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Use removeItem to delete items");
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        assertThatThrownBy(() -> new UpdateCartItemQuantityCommand(userId, productId, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity cannot be negative");
    }

    @Test
    void shouldHandleMultipleUpdatesToSameItem() {
        // Arrange
        addItemToCart(2);
        eventCaptor.clear();

        // Act
        sut.handle(new UpdateCartItemQuantityCommand(userId, productId, 5));
        sut.handle(new UpdateCartItemQuantityCommand(userId, productId, 8));
        sut.handle(new UpdateCartItemQuantityCommand(userId, productId, 3));

        // Assert
        assertThat(eventCaptor.getCapturedEvents()).hasSize(3);

        var persistedCart = cartRepository.findByUserIdWithItems(userId);
        assertThat(persistedCart.get().getItems().getFirst().getQuantity()).isEqualTo(3);
    }

    private void addItemToCart(int quantity) {
        AddItemToCartCommand command = new AddItemToCartCommand(
                userId,
                productId,
                "Test Product",
                price,
                quantity
        );
        addItemHandler.handle(command);
    }
}
