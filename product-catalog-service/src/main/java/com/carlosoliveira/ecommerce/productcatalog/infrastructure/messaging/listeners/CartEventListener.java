package com.carlosoliveira.ecommerce.productcatalog.infrastructure.messaging.listeners;

import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ItemAddedToCartEvent;
import com.carlosoliveira.ecommerce.productcatalog.application.services.ProductService;
import com.carlosoliveira.ecommerce.productcatalog.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CartEventListener {

    private final ProductService productService;

    public CartEventListener(ProductService productService) {
        this.productService = productService;
    }

    @RabbitListener(queues = RabbitMQConfig.CART_EVENTS_QUEUE)
    public void handleItemAddedToCartEvent(@Payload ItemAddedToCartEvent event) {
        log.info("Received ItemAddedToCartEvent for Product ID: {}", event.productId());
        productService.reserveStock(event.productId(), event.quantity());
    }
}
