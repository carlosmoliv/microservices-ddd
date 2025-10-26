package com.carlosoliveira.ecommerce.shoppingcart.application.events.listeners;

import com.carlosoliveira.ecommerce.shoppingcart.infrastructure.messaging.CartEventPublisher;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.CartItemQuantityUpdatedEvent;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.ItemAddedToCartEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartEventListener {

    private final CartEventPublisher cartEventPublisher;

    @EventListener
    public void onItemAddedToCart(ItemAddedToCartEvent event) {
        cartEventPublisher.handleItemAddedToCartEvent(event);
    }

    @EventListener
    public void onCartItemQuantityUpdated(CartItemQuantityUpdatedEvent event) {
        cartEventPublisher.handleCartItemQuantityUpdatedEvent(event);
    }
}
