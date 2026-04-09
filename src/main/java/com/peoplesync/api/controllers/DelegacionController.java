package com.peoplesync.api.controllers;

import com.peoplesync.api.models.Delegacion;
import com.peoplesync.api.services.DelegacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Delegacion> crearDelegacion(@RequestBody Delegacion request) {
        Delegacion nueva = delegacionService.crearDelegacion(request.getNombre(), request.getDireccion());
        return ResponseEntity.status(201).body(nueva);
    }
}