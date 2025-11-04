package com.redis.redis.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.redis.redis.dto.ProductRequestDTO;
import com.redis.redis.dto.ProductResponseDTO;
import com.redis.redis.exceptions.ApiException;
import com.redis.redis.models.Product;
import com.redis.redis.repositories.ProductRepository;

@Service
public class ProductCacheService {

    @Autowired
    private ProductRepository repository;

    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = repository.findAll();

        if(products.isEmpty()){
            throw new ApiException("Nenhum produto encontrado", HttpStatus.NOT_FOUND);
        }
       
        return products.stream()
            .map(p -> new ProductResponseDTO(p.getId(),p.getName(),p.getPrice()))
            .collect(Collectors.toList());
    }

    // search by id product
    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO findById(String id){
        Product product = repository.findById(id)
            .orElseThrow(() -> new ApiException("Produto não encontrado", HttpStatus.NOT_FOUND));

            return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice()
            );
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO deleteProduct(String id){
        Optional<Product> productToBeDeleted = repository.findById(id);
        if(productToBeDeleted.isEmpty()){
            throw new ApiException("Produto não encontrado", HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
        return new ProductResponseDTO(
            productToBeDeleted.get().getId(),
            productToBeDeleted.get().getName(),
            productToBeDeleted.get().getPrice()
        );
    }

    @CacheEvict(value = "products", key = "'all'")
    public void clearCache() {
        // Evict apenas limpa o cache, não precisa retornar nada
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO saveProduct(ProductRequestDTO productRequest){
       try{

        Product product = Product.builder()
            .name(productRequest.name())
            .price(productRequest.price())
            .build();

    Product saved = repository.save(product);
    return new ProductResponseDTO(saved.getId(),saved.getName(),saved.getPrice());
    }catch(Exception e) {
            throw new ApiException("erro ao salvar produto", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }
}
