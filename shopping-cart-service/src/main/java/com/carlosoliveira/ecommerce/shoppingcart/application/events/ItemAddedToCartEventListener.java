package com.carlosoliveira.ecommerce.shoppingcart.application.events;

import com.carlosoliveira.ecommerce.shoppingcart.domain.events.ItemAddedToCartEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemAddedToCartEventListener {

    private final CartEventPublisher cartEventPublisher;

    @EventListener
    public void onItemAddedToCart(ItemAddedToCartEvent event) {
        cartEventPublisher.handleItemAddedToCartEvent(event);
    }
}
