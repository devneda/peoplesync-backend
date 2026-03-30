package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    // TODO buscar al usuario cuando intente loguearse
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByManagerId(UUID managerId);

    // TODO evitar crear usuarios con el mismo DNI
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
}