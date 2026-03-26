package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Fichaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, UUID> {
    // Para sacar el historial de fichajes de un usuario concreto
    List<Fichaje> findByUsuarioIdOrderByFechaHoraEntradaDesc(UUID usuarioId);
}