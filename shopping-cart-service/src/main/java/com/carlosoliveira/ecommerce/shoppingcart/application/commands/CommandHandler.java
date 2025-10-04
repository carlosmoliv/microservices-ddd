package com.carlosoliveira.ecommerce.shoppingcart.application.commands;

public interface CommandHandler<T> {
    void handle(T command);
}
