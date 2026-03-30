package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.CambiarPasswordRequest;
import com.peoplesync.api.dtos.UsuarioRequest;
import com.peoplesync.api.dtos.UsuarioResponse;
import com.peoplesync.api.dtos.UsuarioUpdateRequest;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;

    @GetMapping("/mis-empleados")
    public ResponseEntity<List<UsuarioResponse>> obtenerMisEmpleados(
            @AuthenticationPrincipal Usuario managerAutenticado) {

        List<Usuario> empleados = usuarioService.obtenerMisEmpleados(managerAutenticado.getId());

        List<UsuarioResponse> response = empleados.stream()
                .map(u -> modelMapper.map(u, UsuarioResponse.class))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();

        List<UsuarioResponse> response = usuarios.stream()
                .map(u -> {
                    UsuarioResponse dto = modelMapper.map(u, UsuarioResponse.class);
                    // Si el usuario tiene jefe, sacamos su ID para el DTO
                    if (u.getManager() != null) {
                        dto.setManagerId(u.getManager().getId());
                    }
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(@PathVariable UUID id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        UsuarioResponse response = modelMapper.map(usuario, UsuarioResponse.class);

        if (usuario.getManager() != null) {
            response.setManagerId(usuario.getManager().getId());
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioUpdateRequest request) {

        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, request);
        UsuarioResponse response = modelMapper.map(usuarioActualizado, UsuarioResponse.class);

        if (usuarioActualizado.getManager() != null) {
            response.setManagerId(usuarioActualizado.getManager().getId());
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> cambiarMiPassword(
            @Valid @RequestBody CambiarPasswordRequest request,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {

        usuarioService.cambiarMiPassword(usuarioAutenticado.getId(), request);

        return ResponseEntity.ok("Contraseña actualizada con éxito");
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        Usuario nuevoUsuario = usuarioService.crearUsuario(request);
        UsuarioResponse response = modelMapper.map(nuevoUsuario, UsuarioResponse.class);

        if (nuevoUsuario.getManager() != null) {
            response.setManagerId(nuevoUsuario.getManager().getId());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}