package com.carlosoliveira.ecommerce.productcatalog.domain;

import com.carlosoliveira.ecommerce.common.valueObjects.Money;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(length = 100)
    private String name;

    @Embedded
    private Stock stock;

    @Embedded
    private Money price;

    @Version
    private Long version;

    public Product(String name, Money price, Stock initialStock) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        if (price == null) {
            throw new IllegalArgumentException("Product price cannot be null.");
        }
        if (initialStock == null) {
            throw new IllegalArgumentException("Initial stock cannot be null.");
        }
        this.id = this.id == null ? UUID.randomUUID() : this.id;
        this.name = name;
        this.price = price;
        this.stock = initialStock;
    }

    public void decrementStock(int amount) {
        this.stock = this.stock.decrement(amount);
    }

    public void incrementStock(int amount) {
        this.stock = this.stock.increment(amount);
    }

    public void updatePrice(Money newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("Product price cannot be null.");
        }
        this.price = newPrice;
    }

    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        this.name = newName;
    }
}
