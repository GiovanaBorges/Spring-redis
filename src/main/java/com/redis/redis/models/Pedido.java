package com.redis.redis.models;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pedido {
    @Id
    private String id;
    @NotNull
    private Double total;
    @NotNull
    private List<String> productId;

    @NotNull
    private String usuarioId;

    private LocalDate dateCreatedAt;

}
