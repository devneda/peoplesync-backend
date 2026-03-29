package com.peoplesync.api.dtos;

import com.peoplesync.api.enums.TipoAusencia;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AusenciaRequest {

    @NotNull(message = "El tipo de ausencia es obligatorio")
    private TipoAusencia tipo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @FutureOrPresent(message = "La fecha de fin debe ser hoy o en el futuro")
    private LocalDate fechaFin;

    private String comentarios;
}