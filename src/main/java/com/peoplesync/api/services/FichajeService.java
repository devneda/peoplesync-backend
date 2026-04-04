package com.peoplesync.api.services;

import com.peoplesync.api.enums.TipoFichaje;
import com.peoplesync.api.models.Fichaje;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.FichajeRepository;
import com.peoplesync.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.peoplesync.api.dtos.ReporteHorasResponse;
import java.time.Duration;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.List;
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

    @Transactional(readOnly = true)
    public ReporteHorasResponse calcularHorasTrabajadas(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin) {

        // 1. Ajustamos las fechas para que cubran el día completo (desde las 00:00:00 hasta las 23:59:59)
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        // 2. Buscamos los fichajes en ese periodo (usando el nombre correcto)
        List<Fichaje> fichajes = fichajeRepository.findByUsuarioIdAndFechaHoraEntradaBetween(usuario.getId(), inicio, fin);

        long totalMinutosTrabajados = 0;

        // 3. Calculamos la duración de cada fichaje cerrado
        for (Fichaje fichaje : fichajes) {
            if (fichaje.getFechaHoraSalida() != null) {
                Duration duracion = Duration.between(fichaje.getFechaHoraEntrada(), fichaje.getFechaHoraSalida());
                totalMinutosTrabajados += duracion.toMinutes();
            }
        }

        // 4. Transformamos los minutos totales a un formato legible (Horas y Minutos)
        long horas = totalMinutosTrabajados / 60;
        long minutosRestantes = totalMinutosTrabajados % 60;
        String tiempoFormateado = horas + "h " + minutosRestantes + "m";

        return new ReporteHorasResponse(
                usuario.getNombreCompleto(),
                horas,
                minutosRestantes,
                tiempoFormateado
        );
    }

    @Transactional(readOnly = true)
    public ReporteHorasResponse obtenerReporteDeEmpleado(UUID empleadoId, LocalDate fechaInicio, LocalDate fechaFin, Usuario jefeAutenticado) {
        Usuario empleado = usuarioRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con ID: " + empleadoId));

        boolean isAdmin = jefeAutenticado.getRol().name().equals("ADMIN");
        boolean isSuManager = empleado.getManager() != null && empleado.getManager().getId().equals(jefeAutenticado.getId());

        if (!isAdmin && !isSuManager) {
            throw new IllegalStateException("No tienes permisos para ver el reporte de horas de este empleado");
        }

        return calcularHorasTrabajadas(empleado, fechaInicio, fechaFin);
    }
}