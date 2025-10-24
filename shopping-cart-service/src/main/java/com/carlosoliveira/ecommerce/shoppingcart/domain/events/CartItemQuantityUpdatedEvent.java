package com.carlosoliveira.ecommerce.shoppingcart.domain.events;

import java.time.Instant;
import java.util.UUID;

public record CartItemQuantityUpdatedEvent(
        UUID cartId,
        UUID userId,
        UUID productId,
        int previousQuantity,
        int newQuantity,
        int quantityDifference,
        Instant occurredAt
) {
    public CartItemQuantityUpdatedEvent {
        quantityDifference = newQuantity - previousQuantity;
    }
}
