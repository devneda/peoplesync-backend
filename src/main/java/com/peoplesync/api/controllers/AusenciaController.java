package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.AusenciaRequest;
import com.peoplesync.api.dtos.AusenciaResponse;
import com.peoplesync.api.models.Ausencia;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.AusenciaRepository;
import com.peoplesync.api.services.AusenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ausencias")
@RequiredArgsConstructor
public class AusenciaController {

    private final AusenciaService ausenciaService;
    private final AusenciaRepository ausenciaRepository;
    private final ModelMapper modelMapper;

    // Endpoint para solicitar una nueva ausencia (Vacaciones, Baja, etc.)
    @PostMapping
    public ResponseEntity<AusenciaResponse> solicitarAusencia(
            @Valid @RequestBody AusenciaRequest request,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {

        Ausencia nuevaAusencia = ausenciaService.solicitarAusencia(
                usuarioAutenticado.getId(),
                request.getTipo(),
                request.getFechaInicio(),
                request.getFechaFin(),
                request.getComentarios()
        );

        AusenciaResponse response = modelMapper.map(nuevaAusencia, AusenciaResponse.class);
        response.setUsuarioId(nuevaAusencia.getUsuario().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mis-ausencias")
    public ResponseEntity<List<AusenciaResponse>> obtenerMisAusencias(
            @AuthenticationPrincipal Usuario usuarioAutenticado) {

        // Busco las ausencias del usuario en base de datos
        List<Ausencia> misAusencias = ausenciaRepository.findByUsuarioIdOrderByFechaInicioDesc(usuarioAutenticado.getId());

        List<AusenciaResponse> response = misAusencias.stream()
                .map(ausencia -> {
                    AusenciaResponse dto = modelMapper.map(ausencia, AusenciaResponse.class);
                    dto.setUsuarioId(ausencia.getUsuario().getId());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(response);
    }
}