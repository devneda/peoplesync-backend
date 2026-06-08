package com.peoplesync.api.repositories;

import com.peoplesync.api.models.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {
    List<Anuncio> findByActivoTrueOrderByFechaPublicacionDesc();
}
