package com.carlosoliveira.ecommerce.productcatalog.domain;

import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Money {

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private Currency currency;
}
