package com.carlosoliveira.ecommerce.shoppingcart.application.events;

import com.carlosoliveira.ecommerce.shoppingcart.config.RabbitMQConfig;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.CartItemQuantityUpdatedEvent;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.ItemAddedToCartEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void handleItemAddedToCartEvent(ItemAddedToCartEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CART_EVENTS_EXCHANGE,
                    "cart.added",
                    event
            );
            log.info("Published message to RabbitMQ for product ID: {}", event.productId());
        } catch (Exception e) {
            log.error("Failed to publish message to RabbitMQ: {}", e.getMessage());
        }
    }

    public void handleCartItemQuantityUpdatedEvent(CartItemQuantityUpdatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CART_EVENTS_EXCHANGE,
                    "cart.quantity.updated",
                    event
            );
            log.info("Published CartItemQuantityUpdatedEvent to RabbitMQ. Product: {}, Difference: {}",
                    event.productId(), event.quantityDifference());
        } catch (Exception e) {
            log.error("Failed to publish CartItemQuantityUpdatedEvent to RabbitMQ: {}", e.getMessage());
        }
    }
}
