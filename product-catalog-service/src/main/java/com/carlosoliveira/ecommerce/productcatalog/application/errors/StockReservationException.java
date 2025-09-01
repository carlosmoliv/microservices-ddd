package com.carlosoliveira.ecommerce.productcatalog.application.errors;

import java.util.UUID;

public class StockReservationException extends RuntimeException {
    public StockReservationException(UUID productId, int retries) {
        super("Failed to reserve stock for product " + productId + " after " + retries + " retries");
    }
}
