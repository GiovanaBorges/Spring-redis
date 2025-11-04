package com.redis.redis.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.redis.redis.models.Pedido;

@Repository
public interface PedidoRepository extends MongoRepository<Pedido,String>{
    Optional<Pedido> findByDateCreatedAt(LocalDate data);
    List<Pedido> findByUsuarioId(String usuarioId);
}
