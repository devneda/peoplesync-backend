package com.peoplesync.api.services;

import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario registrarUsuario(Usuario nuevoUsuario) {
        // No pueden existir dos usuarios con el mismo email o DNI
        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado en el sistema");
        }
        if (usuarioRepository.existsByDni(nuevoUsuario.getDni())) {
            throw new IllegalArgumentException("El DNI ya está registrado en el sistema");
        }

        // TODO cifrado de la contraseña con Spring Security
        // nuevoUsuario.setPasswordHash(passwordEncoder.encode(nuevoUsuario.getPasswordHash()));

        return usuarioRepository.save(nuevoUsuario);
    }
}