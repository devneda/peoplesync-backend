package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Fichaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, UUID> {
    // Busca el último fichaje de un usuario que todavía no tenga hora de salida
    Optional<Fichaje> findFirstByUsuarioIdAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(UUID usuarioId);
    List<Fichaje> findByUsuarioIdAndFechaHoraEntradaBetween(UUID usuarioId, java.time.LocalDateTime inicio, java.time.LocalDateTime fin);
    // Cuenta cuántos usuarios DISTINTOS tienen un fichaje de entrada hoy (sin importar si han salido o no)
    @Query("SELECT COUNT(DISTINCT f.usuario.id) FROM Fichaje f WHERE f.fechaHoraEntrada >= :inicioDia AND f.fechaHoraEntrada <= :finDia")
    long countEmpleadosActivosHoy(@Param("inicioDia") LocalDateTime inicioDia, @Param("finDia") LocalDateTime finDia);
}