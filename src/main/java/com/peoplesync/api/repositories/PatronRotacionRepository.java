package com.peoplesync.api.repositories;

import com.peoplesync.api.models.PatronRotacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatronRotacionRepository extends JpaRepository<PatronRotacion, UUID> {
}