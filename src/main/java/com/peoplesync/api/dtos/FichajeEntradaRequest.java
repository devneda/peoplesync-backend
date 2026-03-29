package com.peoplesync.api.dtos;

import com.peoplesync.api.enums.TipoFichaje;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FichajeEntradaRequest {

    @NotNull(message = "El tipo de fichaje es obligatorio")
    private TipoFichaje tipo;

    private String ipRegistro;
}