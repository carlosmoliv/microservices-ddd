package com.carlosoliveira.ecommerce.productcatalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Stock {

    @Column()
    private int quantity;

    public Stock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public Stock decrement(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Decrement amount cannot be negative.");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Insufficient stock. Current: " + this.quantity + ", Requested: " + amount);
        }
        return new Stock(this.quantity - amount);
    }

    public Stock increment(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Increment amount cannot be negative.");
        }
        return new Stock(this.quantity + amount);
    }
}
