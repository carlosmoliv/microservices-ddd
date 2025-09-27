package com.carlosoliveira.ecommerce.shoppingcart.application.commands;

import java.math.BigDecimal;
import java.util.UUID;

public record AddItemToCartCommand(
        UUID userId,
        UUID productId,
        String productName,
        BigDecimal price,
        int quantity
) {
}
