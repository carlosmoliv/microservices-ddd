package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record GetProductDetailsWithStockRequest(
        @NotBlank(message = "Product ID cannot be empty")
        UUID productId,

        @NotBlank(message = "Required quantity cannot be empty")
        int requiredQuantity
) {}
