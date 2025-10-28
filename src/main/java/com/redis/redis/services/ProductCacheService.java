package com.redis.redis.services;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.redis.redis.models.Product;

@Service
public class ProductCacheService {

    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        // Simula consulta lenta (ex: chamada a banco de dados)
        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

        Product product1 = Product.builder()
            .id(1L)
            .name("product 1")
            .price(12.56)
            .build();

        Product product2 = Product.builder()
            .id(2L)
            .name("product 2")
            .price(56.89)
            .build();

        return List.of(product1, product2);
    }

    @CacheEvict(value = "products", key = "'all'")
    public void clearCache() {
        // Evict apenas limpa o cache, não precisa retornar nada
    }
}
