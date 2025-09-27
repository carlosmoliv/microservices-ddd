package com.carlosoliveira.ecommerce.shoppingcart.domain;

import com.carlosoliveira.ecommerce.common.valueObjects.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private UUID productId;

    @Column
    private String productName;

    @Column
    private int quantity;

    @Embedded
    private Money price;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public CartItem(
            UUID productId,
            String productName,
            Money price,
            int quantity
    ) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public void updateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public void updatePrice(Money price) {
        this.price = price;
    }

    public Money subtotal() {
        return this.price.multiply(this.quantity);
    }
}
