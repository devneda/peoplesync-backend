package com.peoplesync.api.dtos;

import com.peoplesync.api.enums.EstadoAusencia;
import com.peoplesync.api.enums.TipoAusencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AusenciaResponse {
    private UUID id;
    private UUID usuarioId;
    private TipoAusencia tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoAusencia estado; // PENDIENTE, APROBADA, RECHAZADA
    private String comentarios;
}