package com.carlosoliveira.ecommerce.productcatalog.infrastructure.messaging.listeners;

import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ItemAddedToCartEvent;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.NestJsMessageDto;
import com.carlosoliveira.ecommerce.productcatalog.application.services.ProductService;
import com.carlosoliveira.ecommerce.productcatalog.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CartEventListener {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public CartEventListener(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.CART_EVENTS_QUEUE)
    public void handleItemAddedToCartEvent(@Payload NestJsMessageDto event) {
        ItemAddedToCartEvent request = objectMapper.convertValue(event.data(), ItemAddedToCartEvent.class);
        try {
            productService.reserveStock(request.productId(), request.quantity());
        } catch (Exception e) {
            log.error("Failed to reserve stock: {}", e.getMessage());
        }
    }
}
