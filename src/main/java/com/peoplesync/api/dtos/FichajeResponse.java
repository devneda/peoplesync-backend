package com.peoplesync.api.dtos;

import com.peoplesync.api.enums.TipoFichaje;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FichajeResponse {
    private UUID id;
    private UUID usuarioId;
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;
    private TipoFichaje tipo;
}