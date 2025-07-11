package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

public record StockCheckResponse(
        boolean hasStock,
        int availableQuantity
) {}
