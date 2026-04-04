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
        Fichaje fichajeAbierto = fichajeRepository.findFirstByUsuarioIdAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(usuarioId)
                .orElseThrow(() -> new IllegalStateException("No tienes ningún fichaje abierto"));

        fichajeAbierto.setFechaHoraSalida(LocalDateTime.now());
        return fichajeRepository.save(fichajeAbierto);
    }

    @Transactional(readOnly = true)
    public List<Fichaje> obtenerFichajesEntreFechas(Usuario usuario, LocalDate inicio, LocalDate fin) {
        LocalDateTime start = inicio.atStartOfDay();
        LocalDateTime end = fin.atTime(23, 59, 59);
        return fichajeRepository.findByUsuarioIdAndFechaHoraEntradaBetween(usuario.getId(), start, end);
    }

    @Transactional(readOnly = true)
    public boolean tieneFichajeAbierto(UUID usuarioId) {
        return fichajeRepository.findFirstByUsuarioIdAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(usuarioId)
                .isPresent();
    }

    @Transactional(readOnly = true)
    public ReporteHorasResponse calcularHorasTrabajadas(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Fichaje> fichajes = obtenerFichajesEntreFechas(usuario, fechaInicio, fechaFin);
        long totalMinutos = 0;

        for (Fichaje f : fichajes) {
            if (f.getFechaHoraSalida() != null) {
                totalMinutos += Duration.between(f.getFechaHoraEntrada(), f.getFechaHoraSalida()).toMinutes();
            }
        }

        long horas = totalMinutos / 60;
        long minutos = totalMinutos % 60;

        return new ReporteHorasResponse(usuario.getNombreCompleto(), horas, minutos, horas + "h " + minutos + "m");
    }

    @Transactional(readOnly = true)
    public List<Fichaje> obtenerFichajesDeEmpleado(UUID empleadoId, LocalDate inicio, LocalDate fin, Usuario jefe) {
        Usuario empleado = validarYObtenerEmpleado(empleadoId, jefe);
        return obtenerFichajesEntreFechas(empleado, inicio, fin);
    }

    @Transactional(readOnly = true)
    public ReporteHorasResponse obtenerReporteDeEmpleado(UUID empleadoId, LocalDate inicio, LocalDate fin, Usuario jefe) {
        Usuario empleado = validarYObtenerEmpleado(empleadoId, jefe);
        return calcularHorasTrabajadas(empleado, inicio, fin);
    }

    private Usuario validarYObtenerEmpleado(UUID empleadoId, Usuario jefe) {
        Usuario empleado = usuarioRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        boolean isAdmin = jefe.getRol().name().equals("ADMIN");
        boolean esSuManager = empleado.getManager() != null && empleado.getManager().getId().equals(jefe.getId());

        if (!isAdmin && !esSuManager) {
            throw new IllegalStateException("No tienes permisos sobre este empleado");
        }
        return empleado;
    }
}