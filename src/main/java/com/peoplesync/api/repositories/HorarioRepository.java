package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, UUID> {
}