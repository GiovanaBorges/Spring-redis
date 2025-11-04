package com.redis.redis.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import com.redis.redis.dto.ProductRequestDTO;
import com.redis.redis.dto.ProductResponseDTO;
import com.redis.redis.exceptions.ApiException;
import com.redis.redis.models.Product;
import com.redis.redis.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductCacheService service;

    @Test
    void shouldCreateProduct(){
        Product product = Product.builder()
            .name("produto limpeza")
            .price(4.54)
            .build();

        when(repository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId("produto1");
            return p;
        });
        
        ProductRequestDTO requestDTO = new ProductRequestDTO(product.getName(),product.getPrice());
        ProductResponseDTO response = service.saveProduct(requestDTO);

        assertAll(
            () ->  assertEquals("produto1", response.id()),
            () -> assertEquals(response.name(), product.getName()),
            () -> assertEquals(response.price(), product.getPrice())
        );
    }

    @Test
    void shouldReturnErrorOnCreateProduct(){
        ProductRequestDTO request = new ProductRequestDTO(
            "pedidox", 7.40);
        when(repository.save(any(Product.class))).thenThrow(
            new RuntimeException("db falhou")
        );

        ApiException exception = assertThrows(ApiException.class, () ->{
            service.saveProduct(request);
        });
        assertAll(
            () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus()),
            () -> assertEquals("erro ao salvar produto", exception.getMessage())
        );
    }

    @Test
    void shouldDeleteProduct(){
        Product productToBeCompared = Product.builder()
            .id("produto1")
            .name("produto limpeza")
            .price(6.40)
            .build();

        when(repository.findById("produto1")).thenReturn(Optional.of(productToBeCompared));
        doNothing().when(repository).deleteById("produto1");

        ProductResponseDTO response = service.deleteProduct("produto1");
          assertAll(
            () -> assertEquals(productToBeCompared.getId(), response.id()),
            () -> assertEquals(productToBeCompared.getName(), response.name()),
            () -> assertEquals(productToBeCompared.getPrice(), response.price())
        );
    }

    @Test
    void shouldReturnErrorOnDeleteProduct(){
        when(repository.findById("produtoX")).thenReturn(Optional.empty());
        ApiException exception = assertThrows(ApiException.class, () ->{
            service.deleteProduct("produtoX");
        });
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
            () -> assertEquals("Produto não encontrado", exception.getMessage())
        );
    }

    @Test
    void shouldReturnAllProduct(){
        Product product1 = Product.builder()
            .id("product1")
            .name("produto 1")
            .price(7.40)
            .build();

        Product product2 = Product.builder()
            .id("product2")
            .name("produto 2")
            .price(7.4)
            .build();
        
        List<Product> products = List.of(
            product1,product2
        );
        
        when(repository.findAll()).thenReturn(products);

        List<ProductResponseDTO> productsList = service.getAllProducts();

        assertAll(
            () -> assertEquals(2, productsList.size()),
            () -> assertEquals("produto 1", productsList.get(0).name())
        );
        verify(repository,times(1)).findAll();
            
    }

    @Test
    void shouldReturnErrorOnGetAllProducts(){
        when(repository.findAll()).thenReturn(List.of());
        ApiException exception = assertThrows(ApiException.class,() ->{
            service.getAllProducts();
        });
        assertAll(
            () -> assertEquals("Nenhum produto encontrado", exception.getMessage()),
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void shouldFindProductById(){
        Product product = Product.builder()
            .id("produto1")
            .name("produto limpeza")
            .price(7.40)
            .build();

        when(repository.findById("produto1")).thenReturn(Optional.of(product));

        ProductResponseDTO response = service.findById("produto1");
        assertAll(
            () -> assertEquals(product.getId(), response.id()),
            () -> assertEquals(product.getName(),response.name()),
            () -> assertEquals(product.getPrice(), response.price())
        );
        verify(repository,times(1)).findById("produto1");
    }

    @Test
    void shouldReturnErrorOnFindProductById(){
        when(repository.findById("produtoX")).thenReturn(Optional.empty());
        ApiException exception = assertThrows(ApiException.class, () ->{
            service.findById("produtoX");
        });
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
            () -> assertEquals("Produto não encontrado", exception.getMessage())
        );
    }
    
}
