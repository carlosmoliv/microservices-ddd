package com.carlosoliveira.ecommerce.shoppingcart.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record AddItemRequest (
        UUID productId,
        String productName,
        BigDecimal price,
        int quantity
) {
}
