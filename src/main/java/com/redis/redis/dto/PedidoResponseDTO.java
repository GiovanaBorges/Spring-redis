package com.redis.redis.dto;

import java.time.LocalDate;
import java.util.List;

public record PedidoResponseDTO(String id,Double total,List<String> productId,String usuarioId, LocalDate dateCreatedAt) {
    
}

