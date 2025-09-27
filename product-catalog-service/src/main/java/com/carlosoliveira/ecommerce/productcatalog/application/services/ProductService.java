package com.carlosoliveira.ecommerce.productcatalog.application.services;

import com.carlosoliveira.ecommerce.common.valueObjects.Money;

import com.carlosoliveira.ecommerce.productcatalog.application.dtos.CreateProductRequest;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ProductDetailsResponse;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ProductResponse;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.StockCheckResponse;
import com.carlosoliveira.ecommerce.productcatalog.application.errors.ProductNotFoundException;
import com.carlosoliveira.ecommerce.productcatalog.application.errors.StockReservationException;
import com.carlosoliveira.ecommerce.productcatalog.domain.Product;
import com.carlosoliveira.ecommerce.productcatalog.domain.Stock;
import com.carlosoliveira.ecommerce.productcatalog.infrastructure.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY_MS = 50;
    private static final long MAX_DELAY_MS = 500;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Money price = new Money(request.amount(), Currency.getInstance("USD"));
        Stock initialStock = new Stock(request.initialStock());

        Product product = new Product(request.name(), price, initialStock);
        Product savedProduct = productRepository.save(product);

        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getPrice().getAmount(),
                savedProduct.getStock().getQuantity(),
                savedProduct.getVersion()
        );
    }

    @Transactional(readOnly = true)
    public ProductDetailsResponse getProduct(UUID id) throws ProductNotFoundException {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return new ProductDetailsResponse(
                product.getId(),
                product.getName(),
                product.getPrice().getAmount(),
                product.getStock().getQuantity(),
                product.getVersion()
        );
    }

    @Transactional(readOnly = true)
    public StockCheckResponse checkStock(UUID productId, int quantity) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        boolean hasStock =  product.getStock().isAvailable(quantity);
        return new StockCheckResponse(hasStock, product.getStock().getQuantity());
    }

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 500)
    )
    @Transactional
    public void reserveStock(UUID productId, int quantity) {
        var product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.decrementStock(quantity);
        productRepository.save(product);
    }

    @Recover
    public void recover(ObjectOptimisticLockingFailureException ex, UUID productId, int quantity) {
        throw new StockReservationException(productId, MAX_RETRIES);
    }
}
