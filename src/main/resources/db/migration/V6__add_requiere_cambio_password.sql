ALTER TABLE usuarios ADD COLUMN requiere_cambio_password BOOLEAN NOT NULL DEFAULT FALSE;
UPDATE usuarios SET requiere_cambio_password = FALSE;
