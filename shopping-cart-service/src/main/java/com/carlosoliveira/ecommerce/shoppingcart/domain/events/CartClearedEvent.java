package com.carlosoliveira.ecommerce.shoppingcart.domain.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CartClearedEvent(
        UUID cartId,
        UUID userId,
        List<ClearedItem> clearedItems,
        Instant occurredAt
) {
    public record ClearedItem(
            UUID productId,
            int quantity
    ) {}
}
