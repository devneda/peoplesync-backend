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
import java.util.List;
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

    // Metodo para listar lo pendiente
    @Transactional(readOnly = true)
    public List<Ausencia> obtenerAusenciasPendientes() {
        return ausenciaRepository.findByEstadoOrderByFechaInicioAsc(EstadoAusencia.PENDIENTE);
    }

    // Metodo para que el perfil ADMIN (jefe) apruebe o rechace
    @Transactional
    public Ausencia cambiarEstadoAusencia(UUID ausenciaId, EstadoAusencia nuevoEstado) {
        Ausencia ausencia = ausenciaRepository.findById(ausenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de ausencia no encontrada"));

        if (ausencia.getEstado() != EstadoAusencia.PENDIENTE) {
            throw new IllegalStateException("Esta solicitud ya fue procesada anteriormente (" + ausencia.getEstado() + ")");
        }

        ausencia.setEstado(nuevoEstado);
        return ausenciaRepository.save(ausencia);
    }
}