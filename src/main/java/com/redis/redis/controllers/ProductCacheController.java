package com.redis.redis.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redis.redis.annotations.RateLimited;
import com.redis.redis.dto.ProductRequestDTO;
import com.redis.redis.dto.ProductResponseDTO;
import com.redis.redis.services.LockService;
import com.redis.redis.services.ProductCacheService;
import com.redis.redis.services.PubSubService;
import jakarta.validation.Valid;

import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/product")
public class ProductCacheController{

    @Autowired private ProductCacheService productCacheService;
    @Autowired private PubSubService pubSubService;
    @Autowired private LockService lockService;

    @RateLimited
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productCacheService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findProductById(@PathVariable String id){
        ProductResponseDTO productFound = productCacheService.findById(id);
        return ResponseEntity.ok().body(productFound);
    }

    @PostMapping("/clear")
    public ResponseEntity clear() {
        productCacheService.clearCache();
        return ResponseEntity.ok("Cache limpo com sucesso!");
    }

    @PostMapping("/save")
    public ResponseEntity<ProductResponseDTO> saveProduct(@Valid @RequestBody ProductRequestDTO requestDTO){
        ProductResponseDTO response = productCacheService.saveProduct(requestDTO);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> deleteProduct(@PathVariable String id){
        ProductResponseDTO productDeleted = productCacheService.deleteProduct(id);
        return ResponseEntity.ok().body(productDeleted);
    }

    @RateLimited
    @PostMapping("/lock-task")
    public ResponseEntity<?> doTaskWithLock() {
        if(!lockService.acquireLock("task_lock", Duration.ofSeconds(10))){
        return ResponseEntity.status(423).body("Já existe uma tarefa em execução");
    }

    try{
        Thread.sleep(3000);
        return ResponseEntity.ok("Tarefa concluida");
    }catch (InterruptedException e){
        return ResponseEntity.internalServerError().build();
    } finally{
        lockService.releaseLock("task_lock");
    }
}
   
    
    
    
    
}