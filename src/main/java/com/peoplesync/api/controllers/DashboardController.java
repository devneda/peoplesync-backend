package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.DashboardStatsResponse;
import com.peoplesync.api.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DashboardStatsResponse> obtenerEstadisticas() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticasGlobales());
    }
}