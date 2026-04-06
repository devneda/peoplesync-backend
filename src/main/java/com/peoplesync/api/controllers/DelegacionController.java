package com.peoplesync.api.controllers;

import com.peoplesync.api.models.Delegacion;
import com.peoplesync.api.services.DelegacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delegaciones")
@RequiredArgsConstructor
public class DelegacionController {

    private final DelegacionService delegacionService;

    @GetMapping
    public ResponseEntity<List<Delegacion>> listarDelegaciones() {
        return ResponseEntity.ok(delegacionService.obtenerTodas());
    }
}