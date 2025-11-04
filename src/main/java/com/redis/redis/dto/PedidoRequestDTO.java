package com.redis.redis.dto;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public record PedidoRequestDTO(
    @NotNull(message = "o valor do produto é obrigatório")
    Double total,
    @NotNull(message = "a lista não pode ser vazia")
    List<String> productId,
    @NotNull(message = "não pode ser vazio")
    String usuarioId
    ) {
    
}
