package com.carlosoliveira.ecommerce.common.valueObjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Money Value Object Unit Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create Money with a valid positive amount and currency")
    void shouldCreateMoneyWithValidPositiveAmountAndCurrency() {
        Money money = new Money(new BigDecimal("100.50"), Currency.getInstance("USD"));

        assertThat(money.getAmount()).isEqualByComparingTo("100.50");
        assertThat(money.getCurrency()).isEqualTo(Currency.getInstance("USD"));
    }

    @Test
    @DisplayName("Should create Money with zero amount and currency")
    void shouldCreateMoneyWithZeroAmountAndCurrency() {
        Money money = new Money(BigDecimal.ZERO, Currency.getInstance("EUR"));

        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(money.getCurrency()).isEqualTo(Currency.getInstance("EUR"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when adding Money with different currencies")
    void shouldThrowExceptionWhenAddingMoneyWithDifferentCurrencies() {
        Money m1 = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money m2 = new Money(new BigDecimal("5.50"), Currency.getInstance("BRL"));

        assertThatThrownBy(() -> m1.add(m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot add Money with different currencies.");
    }

    @Test
    @DisplayName("Should successfully subtract Money with the same currency")
    void shouldSubtractMoneyWithSameCurrency() {
        Money m1 = new Money(new BigDecimal("12.50"), Currency.getInstance("USD"));
        Money m2 = new Money(new BigDecimal("3.00"), Currency.getInstance("USD"));

        Money difference = m1.subtract(m2);

        assertThat(difference.getAmount()).isEqualByComparingTo("9.50");
        assertThat(difference.getCurrency()).isEqualTo(Currency.getInstance("USD"));
        assertThat(difference).isNotSameAs(m1);
        assertThat(difference).isNotSameAs(m2);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when subtracting Money with different currencies")
    void shouldThrowExceptionWhenSubtractingMoneyWithDifferentCurrencies() {
        Money m1 = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money m2 = new Money(new BigDecimal("5.50"), Currency.getInstance("BRL"));

        assertThatThrownBy(() -> m1.subtract(m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot subtract Money with different currencies.");
    }

    @Test
    @DisplayName("Should allow subtracting to zero amount")
    void shouldAllowSubtractingToZeroAmount() {
        Money m1 = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money m2 = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));

        Money difference = m1.subtract(m2);

        assertThat(difference.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(difference.getCurrency()).isEqualTo(Currency.getInstance("USD"));
    }

    @Test
    @DisplayName("Should successfully multiply Money by a positive multiplier (int)")
    void shouldMultiplyMoneyByPositiveMultiplier() {
        Money m = new Money(new BigDecimal("10.50"), Currency.getInstance("USD"));
        int multiplier = 2;

        Money result = m.multiply(multiplier);

        assertThat(result.getAmount()).isEqualByComparingTo("21.00");
        assertThat(result.getCurrency()).isEqualTo(Currency.getInstance("USD"));
        assertThat(result).isNotSameAs(m);
    }
}
