package com.carlosoliveira.ecommerce.productcatalog.infrastructure.messaging.listeners;

import com.carlosoliveira.ecommerce.common.valueObjects.Money;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ItemAddedToCartEvent;
import com.carlosoliveira.ecommerce.productcatalog.config.RabbitMQConfig;
import com.carlosoliveira.ecommerce.productcatalog.domain.Product;
import com.carlosoliveira.ecommerce.productcatalog.domain.Stock;
import com.carlosoliveira.ecommerce.productcatalog.infrastructure.persistence.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Map;
import java.util.UUID;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Cart Event Listener Integration Tests")
public class CartEventListenerIntegrationTest {

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.11-management")
            .withExposedPorts(5672, 15672);

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID productId;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        try {
            amqpAdmin.purgeQueue(RabbitMQConfig.CART_EVENTS_QUEUE);
        } catch (Exception ignored) {}

        Product product = new Product("Test Product", new Money(new BigDecimal("100.00"), Currency.getInstance("USD")), new Stock(10));
        productRepository.saveAndFlush(product);
        productId = product.getId();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @DisplayName("Should decrement stock when an item added to cart event is received")
    void givenItemAddedToCartEvent_whenReceived_thenStockIsDecremented() {
        // Arrange
        int quantityToAdd = 3;
        int initialStock = 10;
        ItemAddedToCartEvent event = new ItemAddedToCartEvent(productId, UUID.randomUUID(), quantityToAdd, Instant.now());

        Product initialProduct = productRepository.findById(productId).orElseThrow();
        assertThat(initialProduct.getStock().getQuantity()).isEqualTo(initialStock);

        // Act
        System.out.println("Sending message for product ID: " + productId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CART_EVENTS_EXCHANGE, "cart.added", event);

        // Assert
        await().atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    Product updatedProduct = productRepository.findById(productId).orElseThrow();
                    assertThat(updatedProduct.getStock().getQuantity()).isEqualTo(initialStock - quantityToAdd);
                });
    }

    @Test
    @DisplayName("Should handle multiple cart events correctly")
    @Transactional(propagation = Propagation.NEVER)
    void givenMultipleCartEvents_whenReceived_thenStockIsDecrementedCorrectly() {
        // Arrange
        int firstQuantity = 2;
        int secondQuantity = 3;
        int initialStock = 10;

        var firstEvent = new ItemAddedToCartEvent(productId, UUID.randomUUID(), firstQuantity, Instant.now());
        var secondEvent = new ItemAddedToCartEvent(productId, UUID.randomUUID(), secondQuantity, Instant.now().plusSeconds(1));

        // Act
        rabbitTemplate.convertAndSend(RabbitMQConfig.CART_EVENTS_EXCHANGE, "cart.added", firstEvent);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CART_EVENTS_EXCHANGE, "cart.added", secondEvent);

        // Assert
        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    Product updatedProduct = productRepository.findById(productId)
                            .orElseThrow(() -> new AssertionError("Product should exist"));
                    assertThat(updatedProduct.getStock().getQuantity())
                            .isEqualTo(initialStock - firstQuantity - secondQuantity);
                });
    }

    @Test
    @DisplayName("Should not decrement stock when product does not exist")
    @Transactional(propagation = Propagation.NEVER)
    void givenNonExistentProduct_whenCartEventReceived_thenNoStockChange() {
        // Arrange
        UUID nonExistentProductId = UUID.randomUUID();
        ItemAddedToCartEvent event = new ItemAddedToCartEvent(nonExistentProductId, UUID.randomUUID(), 3, Instant.now());

        int initialStock = productRepository.findById(productId).orElseThrow().getStock().getQuantity();

        // Act
        rabbitTemplate.convertAndSend(RabbitMQConfig.CART_EVENTS_EXCHANGE, "cart.added", event);

        // Assert
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Product unchangedProduct = productRepository.findById(productId).orElseThrow();
        assertThat(unchangedProduct.getStock().getQuantity()).isEqualTo(initialStock);
    }

    @Test
    @DisplayName("Should handle optimistic locking by retrying and succeeding")
    @Transactional(propagation = Propagation.NEVER)
    void givenConcurrentEvents_whenReceived_thenRetriesAndSucceeds() throws InterruptedException {
        // Arrange
        int initialStock = 10;
        int firstQuantity = 1;
        int secondQuantity = 1;

        var firstEvent = new ItemAddedToCartEvent(productId, UUID.randomUUID(), firstQuantity, Instant.now());
        var secondEvent = new ItemAddedToCartEvent(productId, UUID.randomUUID(), secondQuantity, Instant.now());

        // Act
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> rabbitTemplate.convertAndSend(RabbitMQConfig.CART_EVENTS_EXCHANGE, "cart.added", firstEvent));
        executor.submit(() -> rabbitTemplate.convertAndSend(RabbitMQConfig.CART_EVENTS_EXCHANGE, "cart.added", secondEvent));

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // Assert
        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    Product updatedProduct = productRepository.findById(productId).orElseThrow();
                    assertThat(updatedProduct.getStock().getQuantity()).isEqualTo(initialStock - firstQuantity - secondQuantity);
                });
    }
}
