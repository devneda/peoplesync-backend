package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Festivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FestivoRepository extends JpaRepository<Festivo, UUID> {
    List<Festivo> findByCalendarioIdOrderByFechaAsc(UUID calendarioId);
}