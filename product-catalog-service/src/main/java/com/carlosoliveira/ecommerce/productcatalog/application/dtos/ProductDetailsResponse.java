package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDetailsResponse(
        UUID id,
        String name,
        BigDecimal priceAmount,
        int stockQuantity,
        Long version
) {}
