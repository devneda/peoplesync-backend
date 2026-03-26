package com.peoplesync.api.dtos;

import com.peoplesync.api.enums.TipoFichaje;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record FichajeEntradaRequest(
        @NotNull(message = "El ID del usuario es obligatorio")
        UUID usuarioId,

        @NotNull(message = "El tipo de fichaje es obligatorio")
        TipoFichaje tipo,

        String ipRegistro
) {}