-- Crear la base de datos
CREATE DATABASE hospital_db;
GO

USE hospital_db;
GO

-- Crear tabla de pacientes
CREATE TABLE pacientes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    direccion VARCHAR(200) NOT NULL
);

-- Crear tabla de médicos
CREATE TABLE medicos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    horario_disponible VARCHAR(200) NOT NULL
);

-- Crear tabla de citas
CREATE TABLE citas (
    id INT IDENTITY(1,1) PRIMARY KEY,
    paciente_id INT NOT NULL,
    medico_id INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PROGRAMADA', 'CANCELADA', 'COMPLETADA')),
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (medico_id) REFERENCES medicos(id)
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_pacientes_cedula ON pacientes(cedula);
CREATE INDEX idx_medicos_especialidad ON medicos(especialidad);
CREATE INDEX idx_citas_fecha_hora ON citas(fecha_hora);
CREATE INDEX idx_citas_estado ON citas(estado); 