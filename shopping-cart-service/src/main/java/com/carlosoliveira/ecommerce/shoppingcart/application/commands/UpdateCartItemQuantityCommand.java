package com.carlosoliveira.ecommerce.shoppingcart.application.commands;

import java.util.UUID;

public record UpdateCartItemQuantityCommand(
        UUID userId,
        UUID productId,
        int newQuantity
) {
    public UpdateCartItemQuantityCommand {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }
}
