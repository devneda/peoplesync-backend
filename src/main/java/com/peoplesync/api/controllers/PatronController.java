package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.PatronRotacionRequest;
import com.peoplesync.api.models.PatronRotacion;
import com.peoplesync.api.services.EstructuraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patrones")
@RequiredArgsConstructor
public class PatronController {

    private final EstructuraService estructuraService;

    @GetMapping
    public ResponseEntity<List<PatronRotacion>> listarPatrones() {
        return ResponseEntity.ok(estructuraService.obtenerTodosLosPatrones());
    }

    @PostMapping
    public ResponseEntity<PatronRotacion> crearPatron(@Valid @RequestBody PatronRotacionRequest request) {
        PatronRotacion nuevo = estructuraService.crearPatron(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
}