package com.carlosoliveira.ecommerce.shoppingcart.application.ports;

public interface CommandHandler<T> {
    void handle(T command);
}
