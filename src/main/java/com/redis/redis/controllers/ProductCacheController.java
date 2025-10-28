package com.redis.redis.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redis.redis.models.Product;
import com.redis.redis.services.ProductCacheService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/product")
public class ProductCacheController{

    @Autowired
    private ProductCacheService productCacheService;

    @GetMapping
    @Cacheable(value = "products", key="'all'")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productCacheService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping("/clear")
    @CacheEvict(value = "products", key = "'all'")
    public ResponseEntity clear() {
        productCacheService.clearCache();
        return ResponseEntity.ok("Cache limpo com sucesso!");
    }
    
    
}