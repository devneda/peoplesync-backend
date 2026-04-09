package com.peoplesync.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "calendarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Calendario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer anio;

    @Column(name = "incluye_sabados", nullable = false)
    @Builder.Default
    private Boolean incluyeSabados = false;

    @Column(name = "incluye_domingos", nullable = false)
    @Builder.Default
    private Boolean incluyeDomingos = false;

    // BLOQUEAMOS A JACKSON PARA QUE NO INTENTE LEER EL FANTASMA (PROXY)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegacion_id")
    private Delegacion delegacion;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // CREAMOS UN CAMPO VIRTUAL PARA EL FRONTEND
    @JsonProperty("delegacionId")
    public UUID getDelegacionId() {
        return this.delegacion != null ? this.delegacion.getId() : null;
    }
}