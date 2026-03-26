package com.peoplesync.api.models;

import com.peoplesync.api.enums.TipoFichaje;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fichajes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Fichaje {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_hora_entrada", nullable = false)
    private LocalDateTime fechaHoraEntrada;

    @Column(name = "fecha_hora_salida")
    private LocalDateTime fechaHoraSalida;

    @Column(name = "ip_registro", length = 45)
    private String ipRegistro;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoFichaje tipo;
}