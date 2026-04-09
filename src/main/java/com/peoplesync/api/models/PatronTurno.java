package com.peoplesync.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "patron_turnos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PatronTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patron_id", nullable = false)
    private PatronRotacion patron;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;

    @Column(name = "semana_orden", nullable = false)
    private Integer semanaOrden;
}