package com.peoplesync.api.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class TurnoSemanaRequest {
    @NotNull
    private Integer semanaOrden;

    @NotNull
    private UUID horarioId;
}