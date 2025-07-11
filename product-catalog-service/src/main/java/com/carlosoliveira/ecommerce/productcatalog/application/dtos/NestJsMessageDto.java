package com.carlosoliveira.ecommerce.productcatalog.application.dtos;

import java.util.Map;

public record NestJsMessageDto(
        String pattern,
        Map<String, Object>data,
        String id
) {
}
