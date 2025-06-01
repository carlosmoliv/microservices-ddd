package com.carlosoliveira.ecommerce.productcatalog.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Stock Value Object Unit Tests")
class StockTest {

    @Test
    @DisplayName("Should create Stock with valid positive quantity")
    void shouldCreateStockWithValidPositiveQuantity() {
        Stock stock = new Stock(100);
        assertThat(stock.getQuantity()).isEqualTo(100);
    }
}
