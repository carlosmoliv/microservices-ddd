package com.carlosoliveira.ecommerce.shoppingcart.domain;

import com.carlosoliveira.ecommerce.common.valueObjects.Money;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.CartItemQuantityUpdatedEvent;
import com.carlosoliveira.ecommerce.shoppingcart.domain.events.ItemAddedToCartEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column()
    private UUID userId;

    @Version
    private Long version;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    public Cart(UUID userId) {
        this.userId = userId;
    }

    public void addItem(
            UUID productId,
            String productName,
            Money price,
            int quantity
    ) {
        Optional<CartItem> existingItem = this.findItem(productId);
        if (existingItem.isPresent()) {
            existingItem.get().updateQuantity(quantity);
        } else {
            CartItem newItem = new CartItem(productId, productName, price, quantity);
            newItem.setCart(this);
            this.items.add(newItem);
        }
        this.domainEvents.add(new ItemAddedToCartEvent(this.id, productId, quantity, Instant.now()));
    }

    public void updateItemQuantity(UUID productId, int newQuantity) {
        if (newQuantity == 0) {
            throw new IllegalArgumentException("Quantity cannot be zero");
        }

        CartItem item = findItem(productId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in Cart"));

        int previousQuantity = item.getQuantity();
        if  (previousQuantity == newQuantity) {
            return;
        }
        item.updateQuantity(newQuantity);
        this.domainEvents.add(new CartItemQuantityUpdatedEvent(
                this.id,
                this.userId,
                productId,
                previousQuantity,
                newQuantity,
                newQuantity - previousQuantity,
                Instant.now()
        ));
    }

    public Collection<Object> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    private Optional<CartItem> findItem(UUID productId) {
        return this.items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }
}
