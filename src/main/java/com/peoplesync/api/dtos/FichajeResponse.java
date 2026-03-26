package com.peoplesync.api.dtos;

import com.peoplesync.api.enums.TipoFichaje;
import java.time.LocalDateTime;
import java.util.UUID;

public record FichajeResponse(
        UUID id,
        UUID usuarioId,
        LocalDateTime fechaHoraEntrada,
        LocalDateTime fechaHoraSalida,
        TipoFichaje tipo
) {}