-- Crear la base de datos
CREATE DATABASE SistemaTurnos;
GO

USE SistemaTurnos;
GO

-- Crear la tabla de Pacientes
CREATE TABLE Pacientes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    prioridad VARCHAR(20) NOT NULL,
    sintomas TEXT NOT NULL,
    enfermedades TEXT,
    problemas TEXT,
    area_hospitalaria VARCHAR(50) NOT NULL,
    numero_turno INT NOT NULL UNIQUE,
    edad INT NOT NULL,
    es_embarazada BIT NOT NULL DEFAULT 0,
    tiene_discapacidad BIT NOT NULL DEFAULT 0,
    nivel_dolor INT NOT NULL,
    es_cronico BIT NOT NULL DEFAULT 0,
    fecha_ingreso DATETIME NOT NULL DEFAULT GETDATE(),
    fecha_atencion DATETIME NULL,
    atendido BIT NOT NULL DEFAULT 0
);
GO

-- Crear índices para mejorar el rendimiento
CREATE INDEX IX_Pacientes_NumeroTurno ON Pacientes(numero_turno);
CREATE INDEX IX_Pacientes_Atendido ON Pacientes(atendido);
CREATE INDEX IX_Pacientes_FechaIngreso ON Pacientes(fecha_ingreso);
GO 