package com.redis.redis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequestDTO(
    @NotBlank(message = "o nome do produto é obrigatório")
    String name, 
    @NotNull(message = "o valor do produto é obrigatório")
    @Positive(message = "o preço deve ser maior que zero")
    Double price) {   
}
