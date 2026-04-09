package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Calendario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CalendarioRepository extends JpaRepository<Calendario, UUID> {
    List<Calendario> findByAnio(Integer anio);

    List<Calendario> findByDelegacion_Id(UUID delegacionId);
}