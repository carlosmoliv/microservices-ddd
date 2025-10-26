package com.carlosoliveira.ecommerce.shoppingcart.presentation;

import com.carlosoliveira.ecommerce.shoppingcart.application.commands.handlers.AddItemToCartCommandHandler;
import com.carlosoliveira.ecommerce.shoppingcart.application.commands.AddItemToCartCommand;
import com.carlosoliveira.ecommerce.shoppingcart.application.dtos.AddItemRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@AllArgsConstructor
public class CartController {

    private final AddItemToCartCommandHandler addItemToCart;

    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addItem(@PathVariable UUID userId, @RequestBody AddItemRequest request) {
        AddItemToCartCommand command = new AddItemToCartCommand(
                userId,
                request.productId(),
                request.productName(),
                request.price(),
                request.quantity()
        );
        addItemToCart.handle(command);
        return ResponseEntity.ok().build();
    }
}
