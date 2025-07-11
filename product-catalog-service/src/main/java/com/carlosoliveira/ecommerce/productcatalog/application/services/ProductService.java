package com.carlosoliveira.ecommerce.productcatalog.application.services;

import com.carlosoliveira.ecommerce.productcatalog.application.dtos.CreateProductRequest;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ProductDetailsResponse;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ProductResponse;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.StockCheckResponse;
import com.carlosoliveira.ecommerce.productcatalog.domain.Currency;
import com.carlosoliveira.ecommerce.productcatalog.domain.Money;
import com.carlosoliveira.ecommerce.productcatalog.domain.Product;
import com.carlosoliveira.ecommerce.productcatalog.domain.Stock;
import com.carlosoliveira.ecommerce.productcatalog.infrastructure.persistence.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Money price = new Money(request.amount(), Currency.USD);
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

    public Optional<ProductDetailsResponse> getProduct(UUID id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        return Optional.of(
                new ProductDetailsResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice().getAmount(),
                        product.getStock().getQuantity(),
                        product.getVersion()
                )
        );
    }

    public StockCheckResponse checkStock(UUID productId, int quantity) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        boolean hasStock =  product.getStock().isAvailable(quantity);

        return new StockCheckResponse(
                hasStock,
                product.getStock().getQuantity()
        );
    }

    public void reserveStock(UUID productId, int quantity) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        product.decrementStock(quantity);
        productRepository.save(product);
    }
}
