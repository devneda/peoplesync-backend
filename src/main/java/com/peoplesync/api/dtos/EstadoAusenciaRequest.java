package com.peoplesync.api.dtos;

import com.peoplesync.api.enums.EstadoAusencia;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoAusenciaRequest {
    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoAusencia estado;
}