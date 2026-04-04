package com.peoplesync.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteHorasResponse {
    private String nombreEmpleado;
    private long totalHoras;
    private long totalMinutos;
    private String tiempoFormateado;
}