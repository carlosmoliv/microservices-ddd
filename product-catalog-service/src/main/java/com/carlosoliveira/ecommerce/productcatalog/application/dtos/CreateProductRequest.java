package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

import com.carlosoliveira.ecommerce.productcatalog.domain.Currency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "Product name cannot be empty")
        @Size(max = 255, message = "Product name cannot exceed 255 characters")
        String name,

        @NotNull(message = "Price amount cannot be null")
        @DecimalMin(value = "0.00", inclusive = true, message = "Price amount must be non-negative")
        BigDecimal amount,

        @NotNull(message = "Currency code cannot be empty")
        Currency currency,

        @Min(value = 0, message = "Initial stock cannot be negative")
        int initialStock
) {
}
