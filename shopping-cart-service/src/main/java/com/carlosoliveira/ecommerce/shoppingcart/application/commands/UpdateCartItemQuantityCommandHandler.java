package com.carlosoliveira.ecommerce.shoppingcart.application.commands;

import com.carlosoliveira.ecommerce.shoppingcart.application.CartRepository;
import com.carlosoliveira.ecommerce.shoppingcart.domain.Cart;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UpdateCartItemQuantityCommandHandler implements CommandHandler<UpdateCartItemQuantityCommand> {

    private final CartRepository cartRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateCartItemQuantityCommandHandler(CartRepository cartRepository, ApplicationEventPublisher eventPublisher) {
        this.cartRepository = cartRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(UpdateCartItemQuantityCommand command) {
        Cart cart = cartRepository.findByUserIdWithItems(command.userId())
                .orElseThrow(() -> new IllegalStateException("Cart not found"));
        cart.updateItemQuantity(command.productId(), command.newQuantity());

        cartRepository.save(cart);

        cart.domainEvents().forEach(eventPublisher::publishEvent);
        cart.clearDomainEvents();
    }
}
