package com.redis.redis.dto;

import java.util.List;

import com.redis.redis.models.Endereco;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
    @NotBlank(message = "o nome do usuario é obrigatório")
    String name,
    @NotBlank(message = "o email é obrigatório")
    String email, 
    List<String> idpedidos,
    List<Endereco> endereco) {   
}
