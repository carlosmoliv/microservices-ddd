package com.carlosoliveira.ecommerce.shoppingcart.application.commands;

import com.carlosoliveira.ecommerce.shoppingcart.application.CartRepository;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.ItemAddedToCartEvent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AddItemToCartCommandHandler Integration Tests")
class AddItemToCartCommandHandlerTest {

    @Autowired
    private AddItemToCartCommandHandler sut;

    @Autowired
    private CartRepository cartRepository;

    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();
    private final BigDecimal price = new BigDecimal("100.00");
    private final int quantity = 3;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public EventCaptor eventCaptor() {
            return new EventCaptor();
        }
    }

    @Component
    static class EventCaptor {
        private final List<Object> capturedEvents = new ArrayList<>();

        @EventListener
        public void captureEvent(ItemAddedToCartEvent event) {
            capturedEvents.add(event);
        }

        public List<Object> getCapturedEvents() {
            return capturedEvents;
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
    void shouldCreateNewCartAndPersistItemWhenNoCartExists() {
        // Arrange
        AddItemToCartCommand command = createCommand();

        // Act
        sut.handle(command);

        // Assert
        assertThat(eventCaptor.getCapturedEvents())
                .hasSize(1)
                .first()
                .isInstanceOf(ItemAddedToCartEvent.class);

        var persistedCart = cartRepository.findByUserIdWithItems(userId);

        assertThat(persistedCart).isPresent();
        assertThat(persistedCart.get().getItems()).hasSize(1);
        assertThat(persistedCart.get().getItems().getFirst().getProductId()).isEqualTo(productId);
    }

    private AddItemToCartCommand createCommand() {
        return new AddItemToCartCommand(
                userId,
                productId,
                "Integrated Test Product",
                price,
                quantity
        );
    }
}
