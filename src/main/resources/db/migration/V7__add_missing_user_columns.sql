ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS horario_id UUID;
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS patron_id UUID;
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS fecha_inicio_patron DATE;

-- Añadir las foreign keys para integridad
ALTER TABLE usuarios 
ADD CONSTRAINT fk_usuarios_horario FOREIGN KEY (horario_id) REFERENCES horarios(id);

ALTER TABLE usuarios 
ADD CONSTRAINT fk_usuarios_patron FOREIGN KEY (patron_id) REFERENCES patrones_rotacion(id);
