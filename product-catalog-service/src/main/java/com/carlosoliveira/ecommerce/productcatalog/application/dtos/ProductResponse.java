package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        BigDecimal priceAmount,
        int stockQuantity,
        Long version
) {
}
