package com.peoplesync.api.controllers;

import com.peoplesync.api.models.Calendario;
import com.peoplesync.api.services.EstructuraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calendarios")
@RequiredArgsConstructor
public class CalendarioController {

    private final EstructuraService estructuraService;

    @GetMapping
    public ResponseEntity<List<Calendario>> listarCalendarios() {
        return ResponseEntity.ok(estructuraService.obtenerTodosLosCalendarios());
    }

    @PostMapping("/delegacion/{delegacionId}")
    public ResponseEntity<Calendario> crearCalendario(
            @PathVariable UUID delegacionId,
            @RequestBody Calendario calendario) {
        Calendario nuevo = estructuraService.crearCalendario(calendario, delegacionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
}