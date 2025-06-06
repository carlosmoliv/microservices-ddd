 package com.carlosoliveira.ecommerce.productcatalog.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Product Aggregate Root Unit Tests")
class ProductTest {

    private Money samplePrice;
    private Stock sampleStock;
    private String sampleName;

    @BeforeEach
    void setUp() {
        samplePrice = new Money(new BigDecimal("99.99"), Currency.USD);
        sampleStock = new Stock(100);
        sampleName = "Test Product";
    }

    @Test
    @DisplayName("Should successfully create a Product with valid parameters")
    void shouldCreateProductWithValidParameters() {
        Product product = new Product(sampleName, samplePrice, sampleStock);

        assertThat(product).isNotNull();
        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isEqualTo(sampleName);
        assertThat(product.getPrice()).isEqualTo(samplePrice);
        assertThat(product.getStock()).isEqualTo(sampleStock);
        assertThat(product.getVersion()).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("Should throw IllegalArgumentException when creating Product with null or empty name")
    void shouldThrowExceptionWhenCreatingProductWithInvalidName(String invalidName) {
        assertThatThrownBy(() -> new Product(invalidName, samplePrice, sampleStock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product name cannot be null or empty.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating Product with null price")
    void shouldThrowExceptionWhenCreatingProductWithNullPrice() {
        assertThatThrownBy(() -> new Product(sampleName, null, sampleStock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product price cannot be null.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating Product with null initial stock")
    void shouldThrowExceptionWhenCreatingProductWithNullInitialStock() {
        assertThatThrownBy(() -> new Product(sampleName, samplePrice, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Initial stock cannot be null.");
    }


    @Test
    @DisplayName("Should successfully decrement product stock")
    void shouldDecrementProductStock() {
        Product product = new Product(sampleName, samplePrice, new Stock(50));
        Stock initialStock = product.getStock();

        product.decrementStock(10);

        assertThat(product.getStock().getQuantity()).isEqualTo(40);
        assertThat(product.getStock()).isNotSameAs(initialStock);
    }

    @Test
    @DisplayName("Should propagate IllegalArgumentException from Stock when decrementing with insufficient stock")
    void shouldPropagateExceptionOnInsufficientStockDecrement() {
        Product product = new Product(sampleName, samplePrice, new Stock(5));
        Stock initialStock = product.getStock();

        assertThatThrownBy(() -> product.decrementStock(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock.");

        assertThat(product.getStock()).isEqualTo(initialStock);
        assertThat(product.getStock().getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should propagate IllegalArgumentException from Stock when decrementing with negative amount")
    void shouldPropagateExceptionOnNegativeStockDecrementAmount() {
        Product product = new Product(sampleName, samplePrice, new Stock(50));
        Stock initialStock = product.getStock();

        assertThatThrownBy(() -> product.decrementStock(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Decrement amount cannot be negative.");

        assertThat(product.getStock()).isEqualTo(initialStock);
        assertThat(product.getStock().getQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should successfully increment product stock")
    void shouldIncrementProductStock() {
        Product product = new Product(sampleName, samplePrice, new Stock(50));
        Stock initialStock = product.getStock();

        product.incrementStock(20);

        assertThat(product.getStock().getQuantity()).isEqualTo(70);
        assertThat(product.getStock()).isNotSameAs(initialStock);
    }

    @Test
    @DisplayName("Should propagate IllegalArgumentException from Stock when incrementing with negative amount")
    void shouldPropagateExceptionOnNegativeStockIncrementAmount() {
        Product product = new Product(sampleName, samplePrice, new Stock(50));
        Stock initialStock = product.getStock();

        assertThatThrownBy(() -> product.incrementStock(-10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Increment amount cannot be negative.");

        assertThat(product.getStock()).isEqualTo(initialStock);
        assertThat(product.getStock().getQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should successfully update product price")
    void shouldUpdateProductPrice() {
        Product product = new Product(sampleName, samplePrice, sampleStock);
        Money oldPrice = product.getPrice();

        Money newPrice = new Money(new BigDecimal("120.00"), Currency.USD);
        product.updatePrice(newPrice);

        assertThat(product.getPrice()).isEqualTo(newPrice);
        assertThat(product.getPrice()).isNotSameAs(oldPrice);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating price with null")
    void shouldThrowExceptionWhenUpdatingPriceWithNull() {
        Product product = new Product(sampleName, samplePrice, sampleStock);
        Money oldPrice = product.getPrice();

        assertThatThrownBy(() -> product.updatePrice(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product price cannot be null.");

        assertThat(product.getPrice()).isEqualTo(oldPrice);
    }

    @Test
    @DisplayName("Should successfully update product name")
    void shouldUpdateProductName() {
        Product product = new Product(sampleName, samplePrice, sampleStock);
        String newName = "Updated Product Name";

        product.updateName(newName);

        assertThat(product.getName()).isEqualTo(newName);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("Should throw IllegalArgumentException when updating product name with null or empty")
    void shouldThrowExceptionWhenUpdatingProductNameWithNullOrEmpty(String invalidName) {
        Product product = new Product(sampleName, samplePrice, sampleStock);
        String originalName = product.getName();

        assertThatThrownBy(() -> product.updateName(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product name cannot be null or empty.");
        assertThat(product.getName()).isEqualTo(originalName);
    }
}
