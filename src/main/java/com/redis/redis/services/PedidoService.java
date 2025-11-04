package com.redis.redis.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.redis.redis.dto.PedidoRequestDTO;
import com.redis.redis.dto.PedidoResponseDTO;
import com.redis.redis.exceptions.ApiException;
import com.redis.redis.models.Pedido;
import com.redis.redis.repositories.PedidoRepository;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository repository;

    @CacheEvict(value = "products", allEntries = true)
    public PedidoResponseDTO savePedido(PedidoRequestDTO pedidodto){
        try{
                   
            Pedido pedidoforSave = Pedido.builder()
                .dateCreatedAt(LocalDate.now())
                .total(pedidodto.total())
                .productId(pedidodto.productId())
                .usuarioId(pedidodto.usuarioId())
                .build();

            Pedido saved = repository.save(pedidoforSave);
            return new PedidoResponseDTO(
            saved.getId(), 
            saved.getTotal(),
            saved.getProductId(),
            saved.getUsuarioId(),
            saved.getDateCreatedAt());
        }catch(Exception e) {
            throw new ApiException("erro ao salvar pedido", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    @Cacheable(value = "pedidos", key = "#id")
    public PedidoResponseDTO findByOrderId(String id){
        Pedido pedidofound = repository.findById(id)
            .orElseThrow(() -> new ApiException("Pedido não encontrado", HttpStatus.NOT_FOUND));
        
            return new PedidoResponseDTO(
                pedidofound.getId(),
                pedidofound.getTotal(),
                pedidofound.getProductId(),
                pedidofound.getUsuarioId(),
                pedidofound.getDateCreatedAt()
            );
    }

    public List<PedidoResponseDTO> findPedidoByUserId(String userId){
        List<Pedido> pedidos = repository.findByUsuarioId(userId);

        if(pedidos.isEmpty()){
            throw new ApiException("nenhum pedido registrado por esse usuario", HttpStatus.NOT_FOUND);
        }

        return pedidos.stream()
            .map(p -> new PedidoResponseDTO(p.getId(),
            p.getTotal(),
            p.getProductId(),
            p.getUsuarioId(), 
            p.getDateCreatedAt()))
            .collect(Collectors.toList());
    }

    @CacheEvict(value = "pedidos", allEntries = true)
    public PedidoResponseDTO deletePedido(String id){
        Pedido pedidoFound = repository.findById(id)
            .orElseThrow(() -> new ApiException("Pedido não encontrado",HttpStatus.NOT_FOUND));  

        repository.deleteById(id);
        return new PedidoResponseDTO(
            pedidoFound.getId(),
            pedidoFound.getTotal(),
            pedidoFound.getProductId(),
            pedidoFound.getUsuarioId(),
            LocalDate.now()
        );
    }

    @Cacheable(value = "pedidos", key = "'all'")
    public List<PedidoResponseDTO> getAllOrders(){
        List<Pedido> orders = repository.findAll();
        if(orders.isEmpty()){
            throw new ApiException("Não existe nenhum pedido no sistema", HttpStatus.NOT_FOUND);
        }
        
        return orders.stream()
            .map(o -> new PedidoResponseDTO(o.getId(),
            o.getTotal(),
            o.getProductId(),
            o.getUsuarioId(),
            o.getDateCreatedAt()))
            .collect(Collectors.toList());
    }

    public PedidoResponseDTO getOrderByDate(LocalDate date){
        Pedido pedidoFound = repository.findByDateCreatedAt(date)
            .orElseThrow(() -> {
                throw new ApiException("Não foi encontrado nenhum pedido criado na data informada", HttpStatus.NOT_FOUND);
            });
       
        return new PedidoResponseDTO(
            pedidoFound.getId(),
            pedidoFound.getTotal(),
            pedidoFound.getProductId(),
            pedidoFound.getUsuarioId(),
            pedidoFound.getDateCreatedAt()
        );
    }

    
}