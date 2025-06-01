package com.carlosoliveira.ecommerce.productcatalog.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column()
    private UUID id;

    @Column(length = 100)
    private String name;

    @Embedded
    private Stock stock;
}
