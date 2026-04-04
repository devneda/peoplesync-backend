package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.FichajeEntradaRequest;
import com.peoplesync.api.dtos.FichajeResponse;
import com.peoplesync.api.dtos.ReporteHorasResponse;
import com.peoplesync.api.models.Fichaje;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.services.FichajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fichajes")
@RequiredArgsConstructor
public class FichajeController {

    private final FichajeService fichajeService;
    private final ModelMapper modelMapper;

    // --- ESCRITURA Y ACCIONES ---

    @PostMapping("/entrada")
    public ResponseEntity<FichajeResponse> registrarEntrada(
            @Valid @RequestBody FichajeEntradaRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        Fichaje nuevo = fichajeService.registrarEntrada(usuario.getId(), request.getIpRegistro(), request.getTipo());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(nuevo));
    }

    @PutMapping("/salida")
    public ResponseEntity<FichajeResponse> registrarSalida(@AuthenticationPrincipal Usuario usuario) {
        Fichaje cerrado = fichajeService.registrarSalida(usuario.getId());
        return ResponseEntity.ok(convertToDto(cerrado));
    }

    // --- CONSULTAS DE ESTADO Y LISTADO (PROPIO) ---

    @GetMapping("/estado")
    public ResponseEntity<Boolean> obtenerEstado(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(fichajeService.tieneFichajeAbierto(usuario.getId()));
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<FichajeResponse>> obtenerFichajesHoy(@AuthenticationPrincipal Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        List<Fichaje> lista = fichajeService.obtenerFichajesEntreFechas(usuario, hoy, hoy);
        return ResponseEntity.ok(lista.stream().map(this::convertToDto).toList());
    }

    @GetMapping("/historial")
    public ResponseEntity<List<FichajeResponse>> obtenerHistorialPropio(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<Fichaje> lista = fichajeService.obtenerFichajesEntreFechas(usuario, inicio, fin);
        return ResponseEntity.ok(lista.stream().map(this::convertToDto).toList());
    }

    // --- CONSULTAS PARA MANAGERS (SOBRE EMPLEADOS) ---

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FichajeResponse>> obtenerFichajesDeEmpleado(
            @PathVariable UUID usuarioId,
            @AuthenticationPrincipal Usuario jefe,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<Fichaje> lista = fichajeService.obtenerFichajesDeEmpleado(usuarioId, inicio, fin, jefe);
        return ResponseEntity.ok(lista.stream().map(this::convertToDto).toList());
    }

    @GetMapping("/reporte")
    public ResponseEntity<ReporteHorasResponse> obtenerReportePropio(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(name = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(fichajeService.calcularHorasTrabajadas(usuario, inicio, fin));
    }

    @GetMapping("/reporte/{usuarioId}")
    public ResponseEntity<ReporteHorasResponse> obtenerReporteDeEmpleado(
            @PathVariable UUID usuarioId,
            @AuthenticationPrincipal Usuario jefe,
            @RequestParam(name = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(fichajeService.obtenerReporteDeEmpleado(usuarioId, inicio, fin, jefe));
    }

    private FichajeResponse convertToDto(Fichaje fichaje) {
        FichajeResponse response = modelMapper.map(fichaje, FichajeResponse.class);
        response.setUsuarioId(fichaje.getUsuario().getId());
        return response;
    }
}