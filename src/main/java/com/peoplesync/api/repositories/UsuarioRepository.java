package com.peoplesync.api.repositories;

import com.peoplesync.api.enums.Rol;
import com.peoplesync.api.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByManager(Usuario manager);
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = 'MANAGER' OR u.rol = 'ADMIN'")
    List<Usuario> findManagers();

    List<Usuario> findByRol(Rol rol);

    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
}
