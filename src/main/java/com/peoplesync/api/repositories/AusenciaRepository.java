package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Ausencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AusenciaRepository extends JpaRepository<Ausencia, UUID> {
    // Para que el empleado vea todas sus peticiones de vacaciones
    List<Ausencia> findByUsuarioIdOrderByFechaInicioDesc(UUID usuarioId);
    // Metodo para que un perfil ADMIN pueda ver todas las solicitudes
    List<Ausencia> findByEstadoOrderByFechaInicioAsc(com.peoplesync.api.enums.EstadoAusencia estado);
    // Cuenta las ausencias que están APROBADAS y cuya fecha abarca el día de hoy
    @Query("SELECT COUNT(a) FROM Ausencia a WHERE a.estado = 'APROBADA' AND a.fechaInicio <= :hoy AND a.fechaFin >= :hoy")
    long countAusenciasHoy(@Param("hoy") LocalDate hoy);
}