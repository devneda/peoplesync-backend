CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabla: Delegaciones
CREATE TABLE delegaciones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(255) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla: Horarios
CREATE TABLE horarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(255) NOT NULL,
    hora_entrada_esperada TIME NOT NULL,
    hora_salida_esperada TIME NOT NULL,
    horas_semanales NUMERIC(5,2) NOT NULL, -- Ej: 40.00
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabla: Usuarios
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'USER')),
    dias_vacaciones_anuales INTEGER DEFAULT 22,
    delegacion_id UUID NOT NULL,
    manager_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_delegacion FOREIGN KEY (delegacion_id) REFERENCES delegaciones(id),
    CONSTRAINT fk_usuario_manager FOREIGN KEY (manager_id) REFERENCES usuarios(id)
);

-- 4. Tabla: Fichajes (Control Horario)
CREATE TABLE fichajes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    fecha_hora_entrada TIMESTAMP NOT NULL,
    fecha_hora_salida TIMESTAMP,
    ip_registro VARCHAR(45),
    tipo VARCHAR(20) CHECK (tipo IN ('PRESENCIAL', 'TELETRABAJO')),

    CONSTRAINT fk_fichaje_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 5. Tabla: Ausencias (Vacaciones/Bajas)
CREATE TABLE ausencias (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('VACACIONES', 'BAJA_MEDICA', 'ASUNTOS_PROPIOS')),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA')),
    ruta_justificante VARCHAR(255), -- Ruta al archivo PDF/imagen
    comentarios TEXT,

    CONSTRAINT fk_ausencia_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Crear índices para búsquedas frecuentes y claves foráneas (mejora rendimiento)
CREATE INDEX idx_usuario_email ON usuarios(email);
CREATE INDEX idx_fichaje_usuario_fecha ON fichajes(usuario_id, fecha_hora_entrada);