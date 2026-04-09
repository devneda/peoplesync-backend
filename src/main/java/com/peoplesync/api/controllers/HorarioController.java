package com.peoplesync.api.controllers;

import com.peoplesync.api.models.Horario;
import com.peoplesync.api.services.EstructuraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final EstructuraService estructuraService;

    @GetMapping
    public ResponseEntity<List<Horario>> listarHorarios() {
        return ResponseEntity.ok(estructuraService.obtenerTodosLosHorarios());
    }

    @PostMapping
    public ResponseEntity<Horario> crearHorario(@RequestBody Horario horario) {
        Horario nuevoHorario = estructuraService.crearHorario(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }
}