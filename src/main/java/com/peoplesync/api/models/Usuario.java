package com.peoplesync.api.models;

import com.peoplesync.api.enums.Rol;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(name = "dias_vacaciones_anuales")
    @Builder.Default
    private Integer diasVacacionesAnuales = 22;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegacion_id", nullable = false)
    private Delegacion delegacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Usuario manager;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "foto_url")
    private String fotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendario_id")
    private Calendario calendario;

    // Puede tener un horario fijo...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_fijo_id")
    private Horario horarioFijo;

    // ... o puede tener un patrón de rotación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patron_rotacion_id")
    private PatronRotacion patronRotacion;

    @Column(name = "fecha_inicio_patron")
    private java.time.LocalDate fechaInicioPatron;

    // --- MÉTODOS DE SPRING SECURITY (UserDetails) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Le decimos a Spring qué rol tiene este usuario (Ej: "ROLE_ADMIN")
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email; // Nuestro "usuario" para loguearse será el email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // TODO en el futuro se puede añadir la funcion para habilitar un usuario (activo/inactivo)
    @Override
    public boolean isEnabled() {
        return this.activo;
    }
}