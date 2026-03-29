package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Ausencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AusenciaRepository extends JpaRepository<Ausencia, UUID> {
    // Para que el empleado vea todas sus peticiones de vacaciones
    List<Ausencia> findByUsuarioIdOrderByFechaInicioDesc(UUID usuarioId);
    // Metodo para que un perfil ADMIN pueda ver todas las solicitudes
    List<Ausencia> findByEstadoOrderByFechaInicioAsc(com.peoplesync.api.enums.EstadoAusencia estado);
}