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
}
