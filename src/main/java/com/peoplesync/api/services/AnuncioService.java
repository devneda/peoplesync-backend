package com.peoplesync.api.services;

import com.peoplesync.api.models.Anuncio;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.AnuncioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;

    @Transactional(readOnly = true)
    public List<Anuncio> obtenerAnunciosActivos() {
        return anuncioRepository.findByActivoTrueOrderByFechaPublicacionDesc();
    }

    @Transactional
    public Anuncio crearAnuncio(String titulo, String contenido, String categoria, Usuario autor) {
        Anuncio anuncio = Anuncio.builder()
                .titulo(titulo)
                .contenido(contenido)
                .categoria(categoria != null ? categoria : "GENERAL")
                .fechaPublicacion(LocalDateTime.now())
                .autor(autor)
                .activo(true)
                .build();
        return anuncioRepository.save(anuncio);
    }

    @Transactional
    public void eliminarAnuncio(UUID id) {
        anuncioRepository.deleteById(id);
    }
}
