package com.carlosoliveira.ecommerce.productcatalog.infrastructure.messaging.listeners;

import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ItemAddedToCartEvent;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.NestJsMessageDto;
import com.carlosoliveira.ecommerce.productcatalog.application.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = { CartEventListener.class, JacksonAutoConfiguration.class })
@ExtendWith(MockitoExtension.class)
class CartEventListenerTest {

    @Autowired
    private CartEventListener cartEventListener;

    @MockitoBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        reset(productService);
    }

    @Test
    @DisplayName("should reserve stock when item added to cart event is received")
    void givenItemAddedToCartEvent_whenHandle_thenReserveStock() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        int quantity = 3;
        Instant timestamp = Instant.now();

        ItemAddedToCartEvent itemAddedEvent = new ItemAddedToCartEvent(productId, cartId, quantity, timestamp);

        Map<String, Object> eventDataMap = Map.of(
                "productId", itemAddedEvent.productId().toString(),
                "cartId", itemAddedEvent.cartId().toString(),
                "quantity", itemAddedEvent.quantity(),
                "timestamp", itemAddedEvent.timestamp().toString()
        );
        NestJsMessageDto event = new NestJsMessageDto("item.added.to.cart", eventDataMap, "1234567890");

        // Act
        cartEventListener.handleItemAddedToCartEvent(event);

        // Assert
        verify(productService, times(1)).reserveStock(productId, quantity);
        verifyNoMoreInteractions(productService);
    }
}
