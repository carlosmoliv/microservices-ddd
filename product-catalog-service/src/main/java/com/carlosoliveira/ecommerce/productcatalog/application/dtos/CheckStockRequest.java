package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

import java.util.UUID;

public record CheckStockRequest(
        UUID productId,
        int quantity
) {
}
