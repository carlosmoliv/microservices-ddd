package com.carlosoliveira.ecommerce.shoppingcart.application.commandHandlers;

public interface CommandHandler<T> {
    void handle(T command);
}
