package com.peoplesync.api.services;

import com.peoplesync.api.models.Calendario;
import com.peoplesync.api.models.Delegacion;
import com.peoplesync.api.models.Horario;
import com.peoplesync.api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstructuraService {

    private final HorarioRepository horarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final DelegacionRepository delegacionRepository;
    private final PatronRotacionRepository patronRotacionRepository;
    private final PatronTurnoRepository patronTurnoRepository;

    // --- HORARIOS ---

    @Transactional(readOnly = true)
    public List<Horario> obtenerTodosLosHorarios() {
        return horarioRepository.findAll();
    }

    @Transactional
    public Horario crearHorario(Horario horario) {
        return horarioRepository.save(horario);
    }

    // --- CALENDARIOS ---

    @Transactional(readOnly = true)
    public List<Calendario> obtenerTodosLosCalendarios() {
        return calendarioRepository.findAll();
    }

    @Transactional
    public Calendario crearCalendario(Calendario calendarioRequest, UUID delegacionId) {
        Delegacion delegacion = delegacionRepository.findById(delegacionId)
                .orElseThrow(() -> new IllegalArgumentException("Delegación no encontrada"));

        calendarioRequest.setDelegacion(delegacion);
        return calendarioRepository.save(calendarioRequest);
    }

    // --- PATRONES DE ROTACIÓN ---

    @Transactional(readOnly = true)
    public List<com.peoplesync.api.models.PatronRotacion> obtenerTodosLosPatrones() {
        return patronRotacionRepository.findAll();
    }

    @Transactional
    public com.peoplesync.api.models.PatronRotacion crearPatron(com.peoplesync.api.dtos.PatronRotacionRequest request) {
        // 1. Creamos la cabecera del patrón
        com.peoplesync.api.models.PatronRotacion patron = com.peoplesync.api.models.PatronRotacion.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .semanasCiclo(request.getSemanasCiclo())
                .build();

        // Guardamos para generar el ID
        com.peoplesync.api.models.PatronRotacion patronGuardado = patronRotacionRepository.save(patron);

        // 2. Vinculamos cada semana con su horario correspondiente
        for (com.peoplesync.api.dtos.TurnoSemanaRequest turnoReq : request.getTurnos()) {
            Horario horario = horarioRepository.findById(turnoReq.getHorarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado con ID: " + turnoReq.getHorarioId()));

            com.peoplesync.api.models.PatronTurno patronTurno = com.peoplesync.api.models.PatronTurno.builder()
                    .patron(patronGuardado)
                    .horario(horario)
                    .semanaOrden(turnoReq.getSemanaOrden())
                    .build();

            patronTurnoRepository.save(patronTurno);
        }

        return patronGuardado;
    }
}