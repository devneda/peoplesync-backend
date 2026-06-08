package com.peoplesync.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnuncioResponse {
    private UUID id;
    private String titulo;
    private String contenido;
    private LocalDateTime fechaPublicacion;
    private String autorNombre;
    private String categoria;
}
