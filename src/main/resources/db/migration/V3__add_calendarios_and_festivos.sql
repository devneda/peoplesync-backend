-- Crear tabla de calendarios
CREATE TABLE calendarios (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    anio INTEGER NOT NULL,
    incluye_sabados BOOLEAN NOT NULL DEFAULT FALSE,
    incluye_domingos BOOLEAN NOT NULL DEFAULT FALSE,
    delegacion_id UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_calendario_delegacion FOREIGN KEY (delegacion_id) REFERENCES delegaciones(id)
);

-- Crear tabla de festivos
CREATE TABLE festivos (
    id UUID PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    fecha DATE NOT NULL,
    calendario_id UUID NOT NULL,
    CONSTRAINT fk_festivo_calendario FOREIGN KEY (calendario_id) REFERENCES calendarios(id)
);

-- Añadir relación en la tabla de usuarios (para asignarles un calendario y horario)
ALTER TABLE usuarios
ADD COLUMN calendario_id UUID,
ADD COLUMN horario_id UUID;

ALTER TABLE usuarios
ADD CONSTRAINT fk_usuario_calendario FOREIGN KEY (calendario_id) REFERENCES calendarios(id),
ADD CONSTRAINT fk_usuario_horario FOREIGN KEY (horario_id) REFERENCES horarios(id);