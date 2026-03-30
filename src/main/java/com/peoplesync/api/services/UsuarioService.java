package com.peoplesync.api.services;

import com.peoplesync.api.dtos.UsuarioRequest;
import com.peoplesync.api.dtos.UsuarioUpdateRequest;
import com.peoplesync.api.models.Delegacion;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.DelegacionRepository;
import com.peoplesync.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.peoplesync.api.dtos.CambiarPasswordRequest;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DelegacionRepository delegacionRepository;
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

    @Transactional
    public Usuario crearUsuario(UsuarioRequest request) {

        // 1. Comprobamos si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.getEmail());
        }

        // 2. Comprobamos si el DNI ya existe
        if (usuarioRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario con el DNI: " + request.getDni());
        }

        // 3. Buscamos la delegación
        Delegacion delegacion = delegacionRepository.findById(request.getDelegacionId())
                .orElseThrow(() -> new IllegalArgumentException("La delegación especificada no existe"));

        // 4. Creamos el usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDni(request.getDni());
        nuevoUsuario.setNombreCompleto(request.getNombreCompleto());
        nuevoUsuario.setEmail(request.getEmail());

        // Ciframos la contraseña
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        nuevoUsuario.setRol(request.getRol());
        nuevoUsuario.setDiasVacacionesAnuales(request.getDiasVacacionesAnuales() != null ? request.getDiasVacacionesAnuales() : 22);
        nuevoUsuario.setDelegacion(delegacion);

        // Compruebo si se le asigna un manager
        if (request.getManagerId() != null) {
            Usuario manager = usuarioRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("El manager especificado no existe"));
            nuevoUsuario.setManager(manager);
        }

        return usuarioRepository.save(nuevoUsuario);
    }

    @Transactional
    public Usuario actualizarUsuario(UUID id, UsuarioUpdateRequest request) {
        Usuario usuarioActual = obtenerUsuarioPorId(id);

        // compruebo si el dni existe en bbdd
        if (!usuarioActual.getDni().equals(request.getDni()) && usuarioRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("Ya existe otro usuario con el DNI: " + request.getDni());
        }

        // compruebo si el email existe en bbdd
        if (!usuarioActual.getEmail().equals(request.getEmail()) && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe otro usuario con el email: " + request.getEmail());
        }

        Delegacion delegacion = delegacionRepository.findById(request.getDelegacionId())
                .orElseThrow(() -> new IllegalArgumentException("La delegación especificada no existe"));

        // actualizo los datos
        usuarioActual.setDni(request.getDni());
        usuarioActual.setNombreCompleto(request.getNombreCompleto());
        usuarioActual.setEmail(request.getEmail());
        usuarioActual.setRol(request.getRol());
        usuarioActual.setDelegacion(delegacion);

        if (request.getDiasVacacionesAnuales() != null) {
            usuarioActual.setDiasVacacionesAnuales(request.getDiasVacacionesAnuales());
        }

        // si envia una nueva contraseña, debo cifrarla antes de guardarla
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuarioActual.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        // si envia nuevo manager, lo actualizo en bbdd
        if (request.getManagerId() != null) {
            Usuario manager = usuarioRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("El manager especificado no existe"));
            usuarioActual.setManager(manager);
        } else {
            usuarioActual.setManager(null);
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
        usuarioRepository.save(usuario);
    }
}