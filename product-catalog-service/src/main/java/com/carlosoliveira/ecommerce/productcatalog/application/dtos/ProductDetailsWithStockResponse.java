package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

public record ProductDetailsWithStockResponse(
        ProductDetailsResponse product,
        boolean hasStock,
        int availableQuantity
) {}
