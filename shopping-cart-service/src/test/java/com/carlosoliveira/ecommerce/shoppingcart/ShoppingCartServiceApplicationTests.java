package com.carlosoliveira.ecommerce.shoppingcart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ShoppingCartServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
