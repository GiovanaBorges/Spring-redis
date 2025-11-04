package com.redis.redis.dto;

import java.util.List;

import com.redis.redis.models.Endereco;

public record UserResponseDTO(String id, String name,String email, List<String> idpedidos,List<Endereco> endereco) {
    
}
