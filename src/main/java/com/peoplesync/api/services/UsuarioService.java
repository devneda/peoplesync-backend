package com.peoplesync.api.services;

import com.peoplesync.api.dtos.UsuarioRequest;
import com.peoplesync.api.dtos.UsuarioUpdateRequest;
import com.peoplesync.api.models.Delegacion;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.CalendarioRepository;
import com.peoplesync.api.repositories.DelegacionRepository;
import com.peoplesync.api.repositories.HorarioRepository;
import com.peoplesync.api.repositories.PatronRotacionRepository;
import com.peoplesync.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.peoplesync.api.dtos.CambiarPasswordRequest;
import org.springframework.security.authentication.BadCredentialsException;

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

    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerMisEmpleados(UUID managerId) {
        return usuarioRepository.findByManagerId(managerId);
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerManagers() {
        return usuarioRepository.findByRol(com.peoplesync.api.enums.Rol.MANAGER);
    }

    @Transactional
    public Usuario crearUsuario(UsuarioRequest request) {

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.getEmail());
        }

        if (usuarioRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario con el DNI: " + request.getDni());
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

        // Asignar manager
        if (request.getManagerId() != null) {
            Usuario manager = usuarioRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("El manager especificado no existe"));
            nuevoUsuario.setManager(manager);
        }

        if (request.getCalendarioId() != null) {
            calendarioRepository.findById(request.getCalendarioId()).ifPresent(nuevoUsuario::setCalendario);
        }

        if (request.getHorarioId() != null) {
            horarioRepository.findById(request.getHorarioId()).ifPresent(nuevoUsuario::setHorarioFijo);
        }

        if (request.getPatronId() != null) {
            patronRotacionRepository.findById(request.getPatronId()).ifPresent(nuevoUsuario::setPatronRotacion);
            nuevoUsuario.setFechaInicioPatron(LocalDate.now()); // El ciclo empieza hoy
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