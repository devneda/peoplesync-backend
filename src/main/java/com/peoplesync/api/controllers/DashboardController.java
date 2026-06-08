package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.DashboardStatsResponse;
import com.peoplesync.api.dtos.UsuarioResponse;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ModelMapper modelMapper;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DashboardStatsResponse> obtenerEstadisticas() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticasGlobales());
    }

    @GetMapping("/activos-hoy")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> obtenerActivosHoy() {
        List<Usuario> usuarios = dashboardService.obtenerUsuariosActivosHoy();
        List<UsuarioResponse> response = usuarios.stream()
                .map(u -> modelMapper.map(u, UsuarioResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}