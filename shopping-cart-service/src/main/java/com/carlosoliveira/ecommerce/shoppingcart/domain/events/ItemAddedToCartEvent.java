package com.carlosoliveira.ecommerce.shoppingcart.domain.events;

import java.time.Instant;
import java.util.UUID;

public record ItemAddedToCartEvent(
        UUID cartId,
        UUID productId,
        int quantity,
        Instant occurredAt
) {
}
