package com.carlosoliveira.ecommerce.productcatalog.infrastructure.persistence;

import com.carlosoliveira.ecommerce.productcatalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
}
