package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

import java.time.Instant;
import java.util.UUID;

public record ItemAddedToCartEvent(
        UUID cartId,
        UUID productId,
        int quantity,
        Instant timestamp
) {}
