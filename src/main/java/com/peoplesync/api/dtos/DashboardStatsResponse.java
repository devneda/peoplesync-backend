package com.peoplesync.api.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalEmpleados;
    private long totalDelegaciones;
    private long empleadosActivosHoy;
    private long ausenciasHoy;
}