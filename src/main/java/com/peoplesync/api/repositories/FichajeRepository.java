package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Fichaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, UUID> {
    // Busca el último fichaje de un usuario que todavía no tenga hora de salida
    Optional<Fichaje> findFirstByUsuarioIdAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(UUID usuarioId);
    List<Fichaje> findByUsuarioIdAndFechaHoraEntradaBetween(UUID usuarioId, java.time.LocalDateTime inicio, java.time.LocalDateTime fin);
}