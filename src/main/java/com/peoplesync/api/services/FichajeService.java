package com.peoplesync.api.services;

import com.peoplesync.api.enums.TipoFichaje;
import com.peoplesync.api.models.Fichaje;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.FichajeRepository;
import com.peoplesync.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FichajeService {

    private final FichajeRepository fichajeRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Fichaje registrarEntrada(UUID usuarioId, String ipRegistro, TipoFichaje tipo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Fichaje nuevoFichaje = Fichaje.builder()
                .usuario(usuario)
                .fechaHoraEntrada(LocalDateTime.now())
                .ipRegistro(ipRegistro)
                .tipo(tipo)
                .build();

        return fichajeRepository.save(nuevoFichaje);
    }

    @Transactional
    public Fichaje registrarSalida(UUID usuarioId) {
        // Buscamos si el usuario tiene un fichaje a medias (sin salida)
        Fichaje fichajeAbierto = fichajeRepository.findFirstByUsuarioIdAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(usuarioId)
                .orElseThrow(() -> new IllegalStateException("No tienes ningún fichaje abierto en este momento"));

        fichajeAbierto.setFechaHoraSalida(LocalDateTime.now());
        return fichajeRepository.save(fichajeAbierto);
    }
}