package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.AusenciaResponse;
import com.peoplesync.api.dtos.EstadoAusenciaRequest;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
import com.peoplesync.api.enums.TipoAusencia;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ausencias")
@RequiredArgsConstructor
public class AusenciaController {

    private final AusenciaService ausenciaService;
    private final AusenciaRepository ausenciaRepository;
    private final ModelMapper modelMapper;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<AusenciaResponse> solicitarAusencia(
            @RequestParam("tipo") TipoAusencia tipo,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(value = "comentarios", required = false) String comentarios,
            @RequestParam(value = "documento", required = false) MultipartFile documento,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {

        Ausencia nuevaAusencia = ausenciaService.solicitarAusencia(
                usuarioAutenticado.getId(),
                tipo,
                fechaInicio,
                fechaFin,
                comentarios,
                documento
        );

        AusenciaResponse response = modelMapper.map(nuevaAusencia, AusenciaResponse.class);
        response.setUsuarioId(nuevaAusencia.getUsuario().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mis-ausencias")
    public ResponseEntity<List<AusenciaResponse>> obtenerMisAusencias(
            @AuthenticationPrincipal Usuario usuarioAutenticado) {

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

    @GetMapping("/{id}/documento")
    public ResponseEntity<Resource> descargarDocumento(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario usuarioAutenticado) throws IOException {

        Resource recurso = ausenciaService.cargarJustificante(id, usuarioAutenticado);

        String contentType = "application/octet-stream";
        try {
            contentType = java.nio.file.Files.probeContentType(Paths.get(recurso.getURI()));
        } catch (IOException ex) {
            // Silently ignore and use default
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<AusenciaResponse>> obtenerPendientes(@AuthenticationPrincipal Usuario usuarioAutenticado) {

        List<Ausencia> pendientes = ausenciaService.obtenerAusenciasPendientes(usuarioAutenticado);

        List<AusenciaResponse> response = pendientes.stream()
                .map(ausencia -> {
                    AusenciaResponse dto = modelMapper.map(ausencia, AusenciaResponse.class);
                    dto.setUsuarioId(ausencia.getUsuario().getId());
                    dto.setUsuarioNombre(ausencia.getUsuario().getNombreCompleto());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<AusenciaResponse> cambiarEstado(
            @PathVariable UUID id,
            @Valid @RequestBody EstadoAusenciaRequest request) {

        if (request.getEstado() == com.peoplesync.api.enums.EstadoAusencia.PENDIENTE) {
            throw new IllegalArgumentException("El estado debe ser APROBADA o RECHAZADA");
        }

        Ausencia ausenciaActualizada = ausenciaService.cambiarEstadoAusencia(id, request.getEstado());

        AusenciaResponse response = modelMapper.map(ausenciaActualizada, AusenciaResponse.class);
        response.setUsuarioId(ausenciaActualizada.getUsuario().getId());

        return ResponseEntity.ok(response);
    }
}
