package com.peoplesync.api.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "horarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "hora_entrada_esperada", nullable = false)
    private LocalTime horaEntradaEsperada;

    @Column(name = "hora_salida_esperada", nullable = false)
    private LocalTime horaSalidaEsperada;

    @Column(name = "horas_semanales", nullable = false, precision = 5, scale = 2)
    private BigDecimal horasSemanales;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}