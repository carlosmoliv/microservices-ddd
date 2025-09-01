package com.carlosoliveira.ecommerce.productcatalog.infrastructure.messaging.listeners;

import com.carlosoliveira.ecommerce.productcatalog.application.dtos.*;
import com.carlosoliveira.ecommerce.productcatalog.application.errors.ProductNotFoundException;
import com.carlosoliveira.ecommerce.productcatalog.application.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ProductRpcListener {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public ProductRpcListener(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "product_queue")
    @SendTo
    public Object handleProductRequest(@Payload NestJsMessageDto message) {
        try {
            return switch (message.pattern()) {
                case "product.get_details_with_stock" -> getProductDetails(message);
                default -> throw new UnsupportedOperationException("Unknown pattern: " + message.pattern());
            };
        } catch (Exception e) {
            log.error("Error processing product request for pattern: {}", message.pattern(), e);
            throw e;
        }
    }

    private ProductDetailsWithStockResponse getProductDetails(@Payload NestJsMessageDto message) {
        GetProductDetailsWithStockRequest request = objectMapper.convertValue(
                message.data(),
                GetProductDetailsWithStockRequest.class);

        Optional<ProductDetailsResponse> productDetails = productService.getProduct(request.productId());
        ProductDetailsResponse product = productDetails
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        StockCheckResponse stockCheck = productService.checkStock(
                request.productId(),
                request.requiredQuantity());

        return new ProductDetailsWithStockResponse(
                product,
                stockCheck.hasStock(),
                stockCheck.availableQuantity());
    }
}
