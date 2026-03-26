package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Delegacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DelegacionRepository extends JpaRepository<Delegacion, UUID> {
    Optional<Delegacion> findByNombre(String nombre);
}