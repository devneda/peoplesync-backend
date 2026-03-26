package com.peoplesync.api.services;

import com.peoplesync.api.enums.EstadoAusencia;
import com.peoplesync.api.enums.TipoAusencia;
import com.peoplesync.api.models.Ausencia;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.AusenciaRepository;
import com.peoplesync.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AusenciaService {

    private final AusenciaRepository ausenciaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Ausencia solicitarAusencia(UUID usuarioId, TipoAusencia tipo, LocalDate inicio, LocalDate fin, String comentarios) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Si es VACACIONES, comprobar si tiene días suficientes
        if (tipo == TipoAusencia.VACACIONES) {
            long diasSolicitados = ChronoUnit.DAYS.between(inicio, fin) + 1;
            if (diasSolicitados > usuario.getDiasVacacionesAnuales()) {
                throw new IllegalStateException("No tienes suficientes días de vacaciones disponibles (" + usuario.getDiasVacacionesAnuales() + ")");
            }
        }

        Ausencia nuevaAusencia = Ausencia.builder()
                .usuario(usuario)
                .tipo(tipo)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .comentarios(comentarios)
                .estado(EstadoAusencia.PENDIENTE)
                .build();

        return ausenciaRepository.save(nuevaAusencia);
    }
}