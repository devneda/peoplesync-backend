package com.peoplesync.api.repositories;

import com.peoplesync.api.models.PatronTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatronTurnoRepository extends JpaRepository<PatronTurno, UUID> {
    List<PatronTurno> findByPatronIdOrderBySemanaOrdenAsc(UUID patronId);
}