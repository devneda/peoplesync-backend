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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;

    @GetMapping("/mis-empleados")
    public ResponseEntity<List<UsuarioResponse>> obtenerMisEmpleados() {
        List<Usuario> empleados = usuarioService.obtenerMisEmpleados();
        List<UsuarioResponse> response = empleados.stream()
                .map(u -> modelMapper.map(u, UsuarioResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable UUID id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(modelMapper.map(usuario, UsuarioResponse.class));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        List<UsuarioResponse> response = usuarios.stream()
                .map(u -> modelMapper.map(u, UsuarioResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/managers")
    public ResponseEntity<List<UsuarioResponse>> obtenerManagers() {
        List<Usuario> managers = usuarioService.obtenerManagers();
        List<UsuarioResponse> response = managers.stream()
                .map(u -> modelMapper.map(u, UsuarioResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        Usuario nuevoUsuario = usuarioService.crearUsuario(request);
        return ResponseEntity.ok(modelMapper.map(nuevoUsuario, UsuarioResponse.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(@PathVariable UUID id, @Valid @RequestBody UsuarioUpdateRequest request) {
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(modelMapper.map(usuarioActualizado, UsuarioResponse.class));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obtenerMiPerfil() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario usuario) {
            return ResponseEntity.ok(modelMapper.map(usuario, UsuarioResponse.class));
        }
        throw new IllegalStateException("No hay un usuario autenticado");
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> cambiarMiPassword(@Valid @RequestBody CambiarPasswordRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario usuario) {
            usuarioService.cambiarMiPassword(usuario.getId(), request);
            return ResponseEntity.ok("Contraseña actualizada correctamente.");
        }
        throw new IllegalStateException("No hay un usuario autenticado");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> desactivarUsuario(@PathVariable UUID id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok("Usuario desactivado correctamente. Ya no podrá iniciar sesión.");
    }
}
