package com.carlosoliveira.ecommerce.productcatalog.application.services;

import com.carlosoliveira.ecommerce.productcatalog.application.dtos.CreateProductRequest;
import com.carlosoliveira.ecommerce.productcatalog.application.dtos.ProductResponse;
import com.carlosoliveira.ecommerce.productcatalog.domain.Currency;
import com.carlosoliveira.ecommerce.productcatalog.domain.Money;
import com.carlosoliveira.ecommerce.productcatalog.domain.Product;
import com.carlosoliveira.ecommerce.productcatalog.domain.Stock;
import com.carlosoliveira.ecommerce.productcatalog.infrastructure.persistence.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
