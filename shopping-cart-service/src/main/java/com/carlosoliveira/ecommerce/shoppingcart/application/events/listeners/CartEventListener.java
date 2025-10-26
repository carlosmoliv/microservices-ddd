package com.carlosoliveira.ecommerce.shoppingcart.application.events.listeners;

import com.carlosoliveira.ecommerce.shoppingcart.application.ports.EventPublisher;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.CartItemQuantityUpdatedEvent;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.ItemAddedToCartEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartEventListener {

    private final EventPublisher eventPublisher;

    @EventListener
    public void onItemAddedToCart(ItemAddedToCartEvent event) {
        eventPublisher.publish(event);
    }

    @EventListener
    public void onCartItemQuantityUpdated(CartItemQuantityUpdatedEvent event) {
        eventPublisher.publish(event);
    }
}
