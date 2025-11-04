package com.redis.redis.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import com.redis.redis.dto.PedidoRequestDTO;
import com.redis.redis.dto.PedidoResponseDTO;
import com.redis.redis.exceptions.ApiException;
import com.redis.redis.models.Pedido;
import com.redis.redis.repositories.PedidoRepository;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository repository;

    @InjectMocks
    private PedidoService service;

    @Test
    void shouldCreatePedido(){
        Pedido pedido = Pedido.builder()
            .id("pedido1")
            .productId(List.of("produto1","produto2"))
            .total(7.40)
            .dateCreatedAt(LocalDate.now())
            .build();

        when(repository.save(any(Pedido.class))).thenAnswer(i -> {
            Pedido p = i.getArgument(0);
            p.setId("pedido1");
            return p;
        });

        PedidoResponseDTO response = service.savePedido(
            new PedidoRequestDTO(pedido.getTotal(), 
            pedido.getProductId(), pedido.getUsuarioId()));

            assertAll(
            () -> assertEquals(pedido.getId(), response.id()),
            () -> assertEquals(pedido.getProductId(), response.productId()),
            () -> assertEquals(pedido.getTotal(), response.total()),
            () -> assertEquals(pedido.getUsuarioId(), response.usuarioId()),
            () -> assertEquals(pedido.getDateCreatedAt(), response.dateCreatedAt())
            );
    }

    @Test
    void shouldReturnErrorOnCreate(){
        PedidoRequestDTO request = new PedidoRequestDTO(
            7.40
            , List.of("produto1","produto2"),
             "usuario1");

        // simula falha repositorio
        when(repository.save(any(Pedido.class))).thenThrow(
            new RuntimeException("db falhou"));

    ApiException exception = assertThrows(ApiException.class,() ->{
        service.savePedido(request);
    });
     assertAll(
            () -> assertEquals("erro ao salvar pedido", exception.getMessage()),
            () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus())
        );
    }

    @Test
    void shouldFindPedidoById(){
        Pedido pedido = Pedido.builder()
            .id("pedido1")
            .total(8.40)
            .usuarioId("usuario1")
            .productId(List.of("product1","product2"))
            .dateCreatedAt(LocalDate.now())
            .build();
    
        when(repository.findById(pedido.getId())).thenReturn(Optional.of(pedido));

        PedidoResponseDTO response = service.findByOrderId("pedido1");
        assertAll(
            () -> assertEquals(pedido.getId(), response.id()),
            () -> assertEquals(pedido.getProductId(), response.productId()),
            () -> assertEquals(pedido.getTotal(), response.total()),
            () -> assertEquals(pedido.getUsuarioId(), response.usuarioId()),
            () -> assertEquals(pedido.getDateCreatedAt(), response.dateCreatedAt())
            );
    }

    @Test
    void shouldReturnErrorOnGetPedidoById(){
        when(repository.findById("pedidoX")).thenReturn(Optional.empty());
        
        ApiException exception = assertThrows(ApiException.class, () ->{
            service.findByOrderId("pedidoX");
        });
        assertAll(
            () -> assertEquals("Pedido não encontrado", exception.getMessage()),
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void shouldFindByUserId(){
         Pedido pedido = Pedido.builder()
            .id("pedido1")
            .total(8.40)
            .usuarioId("usuario1")
            .productId(List.of("product1","product2"))
            .dateCreatedAt(LocalDate.now())
            .build();
        
        Pedido pedido1 = Pedido.builder()
            .id("pedido2")
            .total(8.40)
            .usuarioId(pedido.getUsuarioId())
            .productId(List.of("product3","product4"))
            .dateCreatedAt(LocalDate.now())
            .build();
        when(repository.findByUsuarioId(pedido.getUsuarioId()))
            .thenReturn(List.of(pedido,pedido1));

        List<PedidoResponseDTO> response = service.findPedidoByUserId(pedido.getUsuarioId());
        assertAll(
            () -> assertEquals(pedido.getId(), response.get(0).id()),
            () -> assertEquals(pedido.getProductId(), response.get(0).productId()),
            () -> assertEquals(pedido.getTotal(), response.get(0).total()),
            () -> assertEquals(pedido.getUsuarioId(), response.get(0).usuarioId()),
            () -> assertEquals(pedido.getDateCreatedAt(), response.get(0).dateCreatedAt())
            );
    }

    @Test
    void shouldNotFindOrderByUserId(){
        when(repository.findByUsuarioId("usuarioX")).thenReturn(List.of());
        ApiException exception = assertThrows(ApiException.class, () ->{
            service.findPedidoByUserId("usuarioX");
        });
        assertAll(
            () -> assertEquals("nenhum pedido registrado por esse usuario", exception.getMessage()),
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void shouldDeletePedido(){
        Pedido pedido = Pedido.builder()
            .id("pedido1")
            .total(8.40)
            .usuarioId("usuario1")
            .productId(List.of("product1","product2"))
            .dateCreatedAt(LocalDate.now())
            .build();
        
        when(repository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        doNothing().when(repository).deleteById("pedido1");
        PedidoResponseDTO response = service.deletePedido(pedido.getId());
        assertAll(
            () -> assertEquals(pedido.getId(), response.id()),
            () -> assertEquals(pedido.getProductId(), response.productId()),
            () -> assertEquals(pedido.getTotal(), response.total()),
            () -> assertEquals(pedido.getUsuarioId(), response.usuarioId()),
            () -> assertEquals(pedido.getDateCreatedAt(), response.dateCreatedAt()));
    }

    @Test
    void shoulReturnErrorOnDeletePedido(){
        when(repository.findById("pedidoX")).thenReturn(Optional.empty());
        ApiException exception = assertThrows(ApiException.class, () ->{
            service.deletePedido("pedidoX");
        });
        assertAll(
            () -> assertEquals("Pedido não encontrado", exception.getMessage()),
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void shouldGetAllPedidos(){
        Pedido pedido = Pedido.builder()
            .id("pedido1")
            .total(8.40)
            .usuarioId("usuario1")
            .productId(List.of("product1","product2"))
            .dateCreatedAt(LocalDate.now())
            .build();
        
        Pedido pedido1 = Pedido.builder()
            .id("pedido2")
            .total(8.40)
            .usuarioId(pedido.getUsuarioId())
            .productId(List.of("product3","product4"))
            .dateCreatedAt(LocalDate.now())
            .build();

        when(repository.findAll()).thenReturn(List.of(pedido,pedido1));
        List<PedidoResponseDTO> response = service.getAllOrders();
         assertAll(
            () -> assertEquals(pedido.getId(), response.get(0).id()),
            () -> assertEquals(pedido.getProductId(), response.get(0).productId()),
            () -> assertEquals(pedido.getTotal(), response.get(0).total()),
            () -> assertEquals(pedido.getUsuarioId(), response.get(0).usuarioId())
        );
    }

    @Test
    void shouldReturnErrorOnGetAllPedidos(){
        when(repository.findAll()).thenReturn(List.of());
        ApiException exception = assertThrows(ApiException.class, () ->{
            service.getAllOrders();
        });
         assertAll(
            () -> assertEquals("Não existe nenhum pedido no sistema", exception.getMessage()),
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void shouldGetOrderByDate(){
        Pedido pedido = Pedido.builder()
            .id("pedido1")
            .total(8.40)
            .usuarioId("usuario1")
            .productId(List.of("product1","product2"))
            .dateCreatedAt(LocalDate.now())
            .build();
        
        when(repository.findByDateCreatedAt(pedido.getDateCreatedAt())).thenReturn(Optional.of(pedido));
        PedidoResponseDTO response = service.getOrderByDate(pedido.getDateCreatedAt());

        assertAll(
            () -> assertEquals(pedido.getId(), response.id()),
            () -> assertEquals(pedido.getProductId(), response.productId()),
            () -> assertEquals(pedido.getTotal(), response.total()),
            () -> assertEquals(pedido.getUsuarioId(), response.usuarioId()));
    }

    @Test
    void shouldReturnErrorOnGetPedidoByDate(){
        when(repository.findByDateCreatedAt(LocalDate.now())).thenReturn(Optional.empty());
        ApiException exception = assertThrows(ApiException.class, () -> {
            service.getOrderByDate(LocalDate.now());
        });
        assertAll(
            () -> assertEquals("Não foi encontrado nenhum pedido criado na data informada", exception.getMessage()),
            () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }
}
