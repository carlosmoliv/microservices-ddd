package com.carlosoliveira.ecommerce.productcatalog.application.errors;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super("Product not found: " + productId);
    }
}
