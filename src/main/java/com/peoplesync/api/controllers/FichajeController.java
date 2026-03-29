package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.FichajeEntradaRequest;
import com.peoplesync.api.dtos.FichajeResponse;
import com.peoplesync.api.models.Fichaje;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.services.FichajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fichajes")
@RequiredArgsConstructor
public class FichajeController {

    private final FichajeService fichajeService;
    private final ModelMapper modelMapper;

    @PostMapping("/entrada")
    public ResponseEntity<FichajeResponse> registrarEntrada(
            @Valid @RequestBody FichajeEntradaRequest request,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {

        Fichaje nuevoFichaje = fichajeService.registrarEntrada(
                usuarioAutenticado.getId(),
                request.getIpRegistro(),
                request.getTipo()
        );

        FichajeResponse response = modelMapper.map(nuevoFichaje, FichajeResponse.class);
        response.setUsuarioId(nuevoFichaje.getUsuario().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/salida")
    public ResponseEntity<FichajeResponse> registrarSalida(@AuthenticationPrincipal Usuario usuarioAutenticado) {

        Fichaje fichajeCerrado = fichajeService.registrarSalida(usuarioAutenticado.getId());
        FichajeResponse response = modelMapper.map(fichajeCerrado, FichajeResponse.class);

        response.setUsuarioId(fichajeCerrado.getUsuario().getId());

        return ResponseEntity.ok(response);
    }
}