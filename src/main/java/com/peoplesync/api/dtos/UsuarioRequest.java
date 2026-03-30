package com.peoplesync.api.dtos;

import com.peoplesync.api.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @Email(message = "Debe ser un email válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password; // El admin escribirá una contraseña inicial

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    private Integer diasVacacionesAnuales;

    private UUID managerId;

    @NotNull(message = "La delegación es obligatoria")
    private UUID delegacionId;
}