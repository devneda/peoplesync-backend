-- 1. Modificar tabla de horarios para incluir descansos
ALTER TABLE horarios
ADD COLUMN minutos_descanso INTEGER NOT NULL DEFAULT 0;

-- 2. Crear tabla de Patrones de Rotación
CREATE TABLE patrones_rotacion (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    semanas_ciclo INTEGER NOT NULL, -- Cuántas semanas dura el ciclo completo
    created_at TIMESTAMP WITHOUT TIME ZONE
);

-- 3. Crear tabla intermedia para vincular Patrones con Horarios
CREATE TABLE patron_turnos (
    id UUID PRIMARY KEY,
    patron_id UUID NOT NULL,
    horario_id UUID NOT NULL,
    semana_orden INTEGER NOT NULL, -- Semana 1, Semana 2, etc.
    CONSTRAINT fk_patron_turnos_patron FOREIGN KEY (patron_id) REFERENCES patrones_rotacion(id),
    CONSTRAINT fk_patron_turnos_horario FOREIGN KEY (horario_id) REFERENCES horarios(id)
);

-- 4. Arreglar relaciones en la tabla de usuarios
-- Como en V3 creamos 'horario_id', lo renombramos a 'horario_fijo_id' para coincidir con Java
ALTER TABLE usuarios RENAME COLUMN horario_id TO horario_fijo_id;

-- Añadimos SOLO las columnas nuevas que faltan
ALTER TABLE usuarios
ADD COLUMN patron_rotacion_id UUID,
ADD COLUMN fecha_inicio_patron DATE;

-- Añadimos SOLO la clave foránea nueva (la del calendario y horario_fijo ya se crearon en V3)
ALTER TABLE usuarios
ADD CONSTRAINT fk_usuario_patron FOREIGN KEY (patron_rotacion_id) REFERENCES patrones_rotacion(id);