package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.AnuncioResponse;
import com.peoplesync.api.models.Anuncio;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.services.AnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/anuncios")
@RequiredArgsConstructor
public class AnuncioController {

    private final AnuncioService anuncioService;

    @GetMapping
    public ResponseEntity<List<AnuncioResponse>> obtenerAnuncios() {
        List<Anuncio> anuncios = anuncioService.obtenerAnunciosActivos();
        List<AnuncioResponse> response = anuncios.stream()
                .map(this::convertToDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AnuncioResponse> crearAnuncio(
            @RequestParam String titulo,
            @RequestParam String contenido,
            @RequestParam(required = false) String categoria,
            @AuthenticationPrincipal Usuario autor) {
        Anuncio nuevo = anuncioService.crearAnuncio(titulo, contenido, categoria, autor);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(nuevo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAnuncio(@PathVariable UUID id) {
        anuncioService.eliminarAnuncio(id);
        return ResponseEntity.noContent().build();
    }

    private AnuncioResponse convertToDto(Anuncio anuncio) {
        return AnuncioResponse.builder()
                .id(anuncio.getId())
                .titulo(anuncio.getTitulo())
                .contenido(anuncio.getContenido())
                .fechaPublicacion(anuncio.getFechaPublicacion())
                .autorNombre(anuncio.getAutor().getNombreCompleto())
                .categoria(anuncio.getCategoria())
                .build();
    }
}
