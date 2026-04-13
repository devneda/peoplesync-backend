package com.peoplesync.api.services;

import com.peoplesync.api.dtos.DashboardStatsResponse;
import com.peoplesync.api.repositories.AusenciaRepository;
import com.peoplesync.api.repositories.DelegacionRepository;
import com.peoplesync.api.repositories.FichajeRepository;
import com.peoplesync.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final DelegacionRepository delegacionRepository;
    private final FichajeRepository fichajeRepository;
    private final AusenciaRepository ausenciaRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse obtenerEstadisticasGlobales() {

        LocalDate hoyDate = LocalDate.now();
        LocalDateTime inicioDia = hoyDate.atStartOfDay();
        LocalDateTime finDia = hoyDate.atTime(LocalTime.MAX);

        long totalEmpleados = usuarioRepository.count();
        long totalDelegaciones = delegacionRepository.count();

        long empleadosActivosHoy = fichajeRepository.countEmpleadosActivosHoy(inicioDia, finDia);
        long ausenciasHoy = ausenciaRepository.countAusenciasHoy(hoyDate);

        return DashboardStatsResponse.builder()
                .totalEmpleados(totalEmpleados)
                .totalDelegaciones(totalDelegaciones)
                .empleadosActivosHoy(empleadosActivosHoy)
                .ausenciasHoy(ausenciasHoy)
                .build();
    }
}