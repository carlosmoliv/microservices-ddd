package com.carlosoliveira.ecommerce.productcatalog.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Stock Value Object Unit Tests")
class StockTest {

    @Test
    @DisplayName("Should create a Stock with a valid positive quantity")
    void shouldCreateStockWithValidPositiveQuantity() {
        Stock stock = new Stock(100);

        assertThat(stock.getQuantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should create a Stock with zero quantity")
    void shouldCreateStockWithZeroQuantity() {
        Stock stock = new Stock(0);

        assertThat(stock.getQuantity()).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    @DisplayName("Should throw IllegalArgumentException when creating Stock with negative quantity")
    void shouldThrowExceptionWhenCreatingStockWithNegativeQuantity(int invalidQuantity) {
        assertThatThrownBy(() -> new Stock(invalidQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock quantity cannot be negative.");
    }

    @Test
    @DisplayName("Should successfully decrement stock by a positive amount")
    void shouldDecrementStockByPositiveAmount() {
        Stock initialStock = new Stock(50);

        Stock newStock = initialStock.decrement(10);

        assertThat(newStock.getQuantity()).isEqualTo(40);
        assertThat(initialStock.getQuantity()).isEqualTo(50);
        assertThat(newStock).isNotSameAs(initialStock);
    }

    @Test
    @DisplayName("Should decrement stock to zero")
    void shouldDecrementStockToZero() {
        Stock initialStock = new Stock(10);

        Stock newStock = initialStock.decrement(10);

        assertThat(newStock.getQuantity()).isEqualTo(0);
        assertThat(initialStock.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when decrementing with insufficient stock")
    void shouldThrowExceptionWhenDecrementingWithInsufficientStock() {
        Stock initialStock = new Stock(5);

        assertThatThrownBy(() -> initialStock.decrement(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock. Current: 5, Requested: 10");
        assertThat(initialStock.getQuantity()).isEqualTo(5);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -50})
    @DisplayName("Should throw IllegalArgumentException when decrementing with negative amount")
    void shouldThrowExceptionWhenDecrementingWithNegativeAmount(int negativeAmount) {
        Stock initialStock = new Stock(100);

        assertThatThrownBy(() -> initialStock.decrement(negativeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Decrement amount cannot be negative.");
        assertThat(initialStock.getQuantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should successfully increment stock by a positive amount")
    void shouldIncrementStockByPositiveAmount() {
        Stock initialStock = new Stock(50);

        Stock newStock = initialStock.increment(20);

        assertThat(newStock.getQuantity()).isEqualTo(70);
        assertThat(initialStock.getQuantity()).isEqualTo(50);
        assertThat(newStock).isNotSameAs(initialStock);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -20})
    @DisplayName("Should throw IllegalArgumentException when incrementing with negative amount")
    void shouldThrowExceptionWhenIncrementingWithNegativeAmount(int negativeAmount) {
        Stock initialStock = new Stock(100);

        assertThatThrownBy(() -> initialStock.increment(negativeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Increment amount cannot be negative.");
        assertThat(initialStock.getQuantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should return true when stock is available for the requested amount")
    void shouldReturnTrueWhenStockIsAvailable() {
        Stock stock = new Stock(50);

        assertThat(stock.isAvailable(40)).isTrue();
    }

    @Test
    @DisplayName("Should return true when stock is exactly equal to the requested amount")
    void shouldReturnTrueWhenStockIsExactlyAvailable() {
        Stock stock = new Stock(50);

        assertThat(stock.isAvailable(50)).isTrue();
    }

    @Test
    @DisplayName("Should return false when stock is not available for the requested amount")
    void shouldReturnFalseWhenStockIsNotAvailable() {
        Stock stock = new Stock(50);

        assertThat(stock.isAvailable(60)).isFalse();
    }
}
