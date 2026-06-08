-- V5: Crear tabla de anuncios
CREATE TABLE anuncios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    titulo VARCHAR(255) NOT NULL,
    contenido VARCHAR(1000) NOT NULL,
    fecha_publicacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    autor_id UUID NOT NULL,
    categoria VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_anuncio_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_anuncio_fecha ON anuncios(fecha_publicacion);

-- Añadir soporte para fotos de perfil
ALTER TABLE usuarios ADD COLUMN foto_url VARCHAR(255);
