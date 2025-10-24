package com.carlosoliveira.ecommerce.shoppingcart.domain.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CartCheckedOutEvent(
        UUID cartId,
        UUID userId,
        List<CheckoutItem> items,
        BigDecimal totalAmount,
        Instant occurredAt
) {
    public record CheckoutItem(
            UUID productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {}
}
