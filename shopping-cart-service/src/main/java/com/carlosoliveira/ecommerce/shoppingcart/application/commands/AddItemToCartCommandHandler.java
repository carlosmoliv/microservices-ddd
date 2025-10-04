package com.carlosoliveira.ecommerce.shoppingcart.application.commands;

import com.carlosoliveira.ecommerce.common.valueObjects.Money;

import com.carlosoliveira.ecommerce.shoppingcart.application.CartRepository;
import com.carlosoliveira.ecommerce.shoppingcart.domain.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

@Slf4j
@Component
@Transactional
public class AddItemToCartCommandHandler implements CommandHandler<AddItemToCartCommand> {

    private final CartRepository cartRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AddItemToCartCommandHandler(
            CartRepository cartRepository,
            ApplicationEventPublisher eventPublisher) {
        this.cartRepository = cartRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(AddItemToCartCommand command) {
        Cart cart = cartRepository.findByUserIdWithItems(command.userId())
                .orElse(null);

        if (cart == null) {
            cart = new Cart(command.userId());
            cart = cartRepository.save(cart);
        }

        Money money = new Money(command.price(), Currency.getInstance("USD"));
        cart.addItem(
                command.productId(),
                command.productName(),
                money,
                command.quantity()
        );

        cartRepository.save(cart);

        cart.domainEvents().forEach(eventPublisher::publishEvent);
        cart.clearDomainEvents();
    }
}
