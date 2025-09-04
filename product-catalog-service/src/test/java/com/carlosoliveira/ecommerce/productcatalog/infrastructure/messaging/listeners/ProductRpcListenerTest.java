package com.carlosoliveira.ecommerce.productcatalog.infrastructure.messaging.listeners;

import com.carlosoliveira.ecommerce.productcatalog.application.dtos.NestJsMessageDto;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ProductDetailsResponse;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ProductDetailsWithStockResponse;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.StockCheckResponse;
import com.carlosoliveira.ecommerce.productcatalog.application.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { ProductRpcListener.class, ObjectMapper.class })
class ProductRpcListenerTest {

    @MockitoBean
    ProductService productService;

    @Autowired
    private ProductRpcListener productRpcListener;

    Faker faker = new Faker();

    @Test
    @DisplayName("should handle 'product.get_details_with_stock' and return details with stock")
    void givenProductRequest_whenGetDetailsWithStock_thenReturnProductDetailsWithStock() {
        // Arrange
        UUID productId = UUID.randomUUID();
        int requiredQuantity = 5;
        int availableQuantity = 10;
        String pattern = "product.get_details_with_stock";

        Map<String, Object> messageData = Map.of(
                "productId", productId,
                "requiredQuantity", requiredQuantity
        );
        NestJsMessageDto message = new NestJsMessageDto(pattern, messageData, "1234567890");

        ProductDetailsResponse productDetails = createSampleProductDetails(productId);
        when(productService.getProduct(productId)).thenReturn(productDetails);
        when(productService.checkStock(productId, requiredQuantity))
                .thenReturn(new StockCheckResponse(true, availableQuantity));

        // Act
        Object response = productRpcListener.handleProductRequest(message);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(ProductDetailsWithStockResponse.class);
        ProductDetailsWithStockResponse actualResponse = (ProductDetailsWithStockResponse) response;

        assertThat(productDetails).isEqualTo(actualResponse.product());
        assertThat(actualResponse)
                .hasFieldOrPropertyWithValue("hasStock", true)
                .hasFieldOrPropertyWithValue("availableQuantity", availableQuantity);

        verify(productService).getProduct(productId);
        verify(productService).checkStock(productId, requiredQuantity);
        verifyNoMoreInteractions(productService);
    }

    private ProductDetailsResponse createSampleProductDetails(UUID productId) {
        return new ProductDetailsResponse(
                productId,
                faker.commerce().productName(),
                BigDecimal.valueOf(100.00),
                100,
                1L
        );
    }
}
