package com.redis.redis.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.redis.redis.models.Product;
import com.redis.redis.services.ProductCacheService;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
class ProductCacheIntegrationTest {

    @Container
    @ServiceConnection  // 🔥 Spring Boot cuida automaticamente de host/porta/config
    static GenericContainer<?> redis = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379);

    @Autowired
    private ProductCacheService productCacheService;

    @Test
    void testCache() {
        List<Product> first = productCacheService.getAllProducts();
        List<Product> second = productCacheService.getAllProducts();
        assertEquals(first, second);
    }
}
