package com.carlosoliveira.ecommerce.productcatalog.application.errors;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
