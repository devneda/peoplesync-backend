package com.peoplesync.api.services;

import com.peoplesync.api.dtos.CambiarPasswordRequest;
import com.peoplesync.api.dtos.UsuarioRequest;
import com.peoplesync.api.dtos.UsuarioUpdateRequest;
import com.peoplesync.api.models.Delegacion;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DelegacionRepository delegacionRepository;
    private final CalendarioRepository calendarioRepository;
    private final HorarioRepository horarioRepository;
    private final PatronRotacionRepository patronRotacionRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> obtenerMisEmpleados() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario manager) {
            return usuarioRepository.findByManager(manager);
        }
        throw new IllegalStateException("No hay un usuario autenticado");
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    public List<Usuario> obtenerManagers() {
        return usuarioRepository.findManagers();
    }

    @Transactional
    public Usuario crearUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario con el DNI: " + request.getDni());
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.getEmail());
        }

        Delegacion delegacion = delegacionRepository.findById(request.getDelegacionId())
                .orElseThrow(() -> new IllegalArgumentException("La delegación especificada no existe"));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDni(request.getDni());
        nuevoUsuario.setNombreCompleto(request.getNombreCompleto());
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRol(request.getRol());
        nuevoUsuario.setDiasVacacionesAnuales(request.getDiasVacacionesAnuales() != null ? request.getDiasVacacionesAnuales() : 22);
        nuevoUsuario.setDelegacion(delegacion);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setRequiereCambioPassword(true);
        nuevoUsuario.setFotoUrl(request.getFotoUrl());

        if (request.getManagerId() != null) {
            Usuario manager = usuarioRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("El manager especificado no existe"));
            nuevoUsuario.setManager(manager);
        }

        if (request.getCalendarioId() != null) {
            calendarioRepository.findById(request.getCalendarioId()).ifPresent(nuevoUsuario::setCalendario);
        }

        if (request.getHorarioId() != null) {
            horarioRepository.findById(request.getHorarioId()).ifPresent(nuevoUsuario::setHorario);
        }

        if (request.getPatronId() != null) {
            patronRotacionRepository.findById(request.getPatronId()).ifPresent(nuevoUsuario::setPatron);
            nuevoUsuario.setFechaInicioPatron(LocalDate.now());
        }

        return usuarioRepository.save(nuevoUsuario);
    }

    @Transactional
    public Usuario actualizarUsuario(UUID id, UsuarioUpdateRequest request) {
        Usuario usuarioActual = obtenerUsuarioPorId(id);

        if (!usuarioActual.getDni().equals(request.getDni()) && usuarioRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("Ya existe otro usuario con el DNI: " + request.getDni());
        }

        if (!usuarioActual.getEmail().equals(request.getEmail()) && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe otro usuario con el email: " + request.getEmail());
        }

        Delegacion delegacion = delegacionRepository.findById(request.getDelegacionId())
                .orElseThrow(() -> new IllegalArgumentException("La delegación especificada no existe"));

        usuarioActual.setDni(request.getDni());
        usuarioActual.setNombreCompleto(request.getNombreCompleto());
        usuarioActual.setEmail(request.getEmail());
        usuarioActual.setRol(request.getRol());
        usuarioActual.setDelegacion(delegacion);

        if (request.getDiasVacacionesAnuales() != null) {
            usuarioActual.setDiasVacacionesAnuales(request.getDiasVacacionesAnuales());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuarioActual.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getManagerId() != null) {
            Usuario manager = usuarioRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("El manager especificado no existe"));
            usuarioActual.setManager(manager);
        } else {
            usuarioActual.setManager(null);
        }

        if (request.getRequiereCambioPassword() != null) {
            usuarioActual.setRequiereCambioPassword(request.getRequiereCambioPassword());
        }

        if (request.getFotoUrl() != null) {
            usuarioActual.setFotoUrl(request.getFotoUrl());
        }

        return usuarioRepository.save(usuarioActual);
    }

    @Transactional
    public void cambiarMiPassword(UUID usuarioId, CambiarPasswordRequest request) {
        Usuario usuario = obtenerUsuarioPorId(usuarioId);

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNueva()));
        usuario.setRequiereCambioPassword(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desactivarUsuario(UUID id) {
        Usuario usuario = obtenerUsuarioPorId(id);

        if (!usuario.getActivo()) {
            throw new IllegalStateException("El usuario ya está desactivado");
        }

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}
