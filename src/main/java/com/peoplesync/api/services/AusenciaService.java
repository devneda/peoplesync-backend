package com.peoplesync.api.services;

import com.peoplesync.api.enums.EstadoAusencia;
import com.peoplesync.api.enums.TipoAusencia;
import com.peoplesync.api.enums.Rol;
import com.peoplesync.api.models.Ausencia;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.AusenciaRepository;
import com.peoplesync.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AusenciaService {

    private final AusenciaRepository ausenciaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Ausencia solicitarAusencia(UUID usuarioId, TipoAusencia tipo, LocalDate inicio, LocalDate fin, String comentarios, MultipartFile documento) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (tipo == TipoAusencia.VACACIONES) {
            long diasSolicitados = ChronoUnit.DAYS.between(inicio, fin) + 1;
            if (diasSolicitados > usuario.getDiasVacacionesAnuales()) {
                throw new IllegalStateException("No tienes suficientes días de vacaciones disponibles (" + usuario.getDiasVacacionesAnuales() + ")");
            }
        }

        String nombreSeguroArchivo = null;
        if (documento != null && !documento.isEmpty()) {
            try {
                String extension = StringUtils.getFilenameExtension(documento.getOriginalFilename());
                nombreSeguroArchivo = UUID.randomUUID().toString() + "." + extension;

                Path directorioUploads = Paths.get("uploads");
                if (!Files.exists(directorioUploads)) {
                    Files.createDirectories(directorioUploads);
                }

                Path rutaFinal = directorioUploads.resolve(nombreSeguroArchivo);
                Files.copy(documento.getInputStream(), rutaFinal, StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception e) {
                throw new RuntimeException("Error al procesar el documento justificativo", e);
            }
        }

        Ausencia nuevaAusencia = Ausencia.builder()
                .usuario(usuario)
                .tipo(tipo)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .comentarios(comentarios)
                .rutaJustificante(nombreSeguroArchivo)
                .estado(EstadoAusencia.PENDIENTE)
                .build();

        return ausenciaRepository.save(nuevaAusencia);
    }

    @Transactional(readOnly = true)
    public List<Ausencia> obtenerAusenciasPendientes(Usuario usuarioAutenticado) {
        List<Ausencia> todasPendientes = ausenciaRepository.findByEstadoOrderByFechaInicioAsc(EstadoAusencia.PENDIENTE);
        
        if (usuarioAutenticado.getRol() == Rol.ADMIN) {
            return todasPendientes;
        }

        return todasPendientes.stream()
                .filter(a -> a.getUsuario().getManager() != null && 
                            a.getUsuario().getManager().getId().equals(usuarioAutenticado.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Ausencia cambiarEstadoAusencia(UUID ausenciaId, EstadoAusencia nuevoEstado) {
        Ausencia ausencia = ausenciaRepository.findById(ausenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de ausencia no encontrada"));

        if (ausencia.getEstado() != EstadoAusencia.PENDIENTE) {
            throw new IllegalStateException("Esta solicitud ya fue procesada anteriormente (" + ausencia.getEstado() + ")");
        }

        ausencia.setEstado(nuevoEstado);
        
        if (nuevoEstado == EstadoAusencia.APROBADA && ausencia.getTipo() == TipoAusencia.VACACIONES) {
            Usuario usuario = ausencia.getUsuario();
            long dias = ChronoUnit.DAYS.between(ausencia.getFechaInicio(), ausencia.getFechaFin()) + 1;
            usuario.setDiasVacacionesAnuales(usuario.getDiasVacacionesAnuales() - (int) dias);
            usuarioRepository.save(usuario);
        }

        return ausenciaRepository.save(ausencia);
    }

    public Resource cargarJustificante(UUID ausenciaId, Usuario usuarioAutenticado) {
        Ausencia ausencia = ausenciaRepository.findById(ausenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Ausencia no encontrada"));

        boolean esDueno = ausencia.getUsuario().getId().equals(usuarioAutenticado.getId());
        boolean esSuManager = ausencia.getUsuario().getManager() != null &&
                ausencia.getUsuario().getManager().getId().equals(usuarioAutenticado.getId());
        boolean esAdmin = usuarioAutenticado.getRol() == Rol.ADMIN;

        if (!esDueno && !esSuManager && !esAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("No tienes permiso para ver este documento");
        }

        if (ausencia.getRutaJustificante() == null) {
            throw new IllegalArgumentException("Esta ausencia no tiene documento adjunto");
        }

        try {
            Path rutaArchivo = Paths.get("uploads").resolve(ausencia.getRutaJustificante());
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (recurso.exists() || recurso.isReadable()) {
                return recurso;
            } else {
                throw new RuntimeException("No se puede leer el archivo");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error al recuperar el archivo", e);
        }
    }
}
