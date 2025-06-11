package com.carlosoliveira.ecommerce.productcatalog;

import com.carlosoliveira.ecommerce.productcatalog.application.dtos.CreateProductRequest;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ProductResponse;
import com.carlosoliveira.ecommerce.productcatalog.domain.Currency;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Product Management Integration Tests")
public class ProductManagementTests {

    private final Faker faker = new Faker();

    @Autowired
    private TestRestTemplate restTemplate;

    private final String PRODUCTS_API_URL = "/products";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Nested
    @DisplayName("POST /products")
    class CreateProductTests {
        @Test
        @DisplayName("Should create a product successfully and persist it")
        void shouldCreateProductSuccessfullyAndPersistIt() {
            // Arrange
            var createProductRequest = new CreateProductRequest(
                    faker.commerce().productName(),
                    BigDecimal.valueOf(faker.number().randomDouble(2, 50, 1000)),
                    Currency.USD,
                    10
            );

            // Act
            ResponseEntity<ProductResponse> response = restTemplate.postForEntity(PRODUCTS_API_URL, createProductRequest, ProductResponse.class);

            // Assert
            ProductResponse responseBody = response.getBody();
            assertThat(responseBody.id()).isNotNull();
            assertThat(responseBody.name()).isEqualTo(createProductRequest.name());
            assertThat(responseBody.priceAmount()).isEqualByComparingTo(createProductRequest.amount());
            assertThat(responseBody.stockQuantity()).isEqualTo(createProductRequest.initialStock());
            assertThat(responseBody.version()).isNotNull();
        }
    }
}
