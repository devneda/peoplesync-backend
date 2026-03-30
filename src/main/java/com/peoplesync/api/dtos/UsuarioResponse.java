package com.peoplesync.api.dtos;

import com.peoplesync.api.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private UUID id;
    private String dni;
    private String nombreCompleto;
    private String email;
    private Rol rol;
    private Integer diasVacacionesAnuales;
    private UUID managerId;
    private LocalDateTime createdAt;
}