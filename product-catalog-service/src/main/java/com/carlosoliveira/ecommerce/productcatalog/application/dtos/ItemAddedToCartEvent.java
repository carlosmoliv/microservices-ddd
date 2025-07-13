package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

import java.time.Instant;
import java.util.UUID;

public record ItemAddedToCartEvent(
        UUID productId,
        UUID cartId,
        int quantity,
        Instant timestamp
) {}
