package com.peoplesync.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PatronRotacionRequest {
    @NotBlank(message = "El nombre del patrón es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "Debes indicar las semanas del ciclo")
    private Integer semanasCiclo;

    @NotEmpty(message = "Debes asignar al menos un turno al patrón")
    private List<TurnoSemanaRequest> turnos;
}