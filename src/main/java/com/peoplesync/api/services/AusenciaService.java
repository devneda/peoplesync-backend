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

        // --- LÓGICA DE GUARDADO DEL DOCUMENTO ANONIMIZADO ---
        String nombreSeguroArchivo = null;
        if (documento != null && !documento.isEmpty()) {
            try {
                // Sacamos la extensión (por ejemplo, ".pdf" o ".jpg")
                String extension = StringUtils.getFilenameExtension(documento.getOriginalFilename());
                // Generamos un UUID seguro para el nombre del fichero
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
                .rutaJustificante(nombreSeguroArchivo) // Guardamos el nombre anonimizado
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

    // Metodo para que solo managers directos o el dueño del documento lo pueda descargar
    public Resource cargarJustificante(UUID ausenciaId, Usuario usuarioAutenticado) {
        Ausencia ausencia = ausenciaRepository.findById(ausenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Ausencia no encontrada"));

        // SEGURIDAD RGPD: Solo el dueño, su manager o un ADMIN pueden verlo
        boolean esDueno = ausencia.getUsuario().getId().equals(usuarioAutenticado.getId());
        boolean esSuManager = ausencia.getUsuario().getManager() != null &&
                ausencia.getUsuario().getManager().getId().equals(usuarioAutenticado.getId());
        boolean esAdmin = usuarioAutenticado.getRol() == com.peoplesync.api.enums.Rol.ADMIN;

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