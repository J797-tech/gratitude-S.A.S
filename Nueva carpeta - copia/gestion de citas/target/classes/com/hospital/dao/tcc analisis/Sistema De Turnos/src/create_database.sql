-- Crear la base de datos si no existe
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'Sistemadeturnos')
BEGIN
    CREATE DATABASE Sistemadeturnos;
END
GO

USE Sistemadeturnos;
GO

-- Crear la tabla de pacientes si no existe
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'pacientes')
BEGIN
    CREATE TABLE pacientes (
        nombre VARCHAR(100),
        prioridad VARCHAR(10),
        sintomas TEXT,
        enfermedades TEXT,
        problemas TEXT,
        area_hospitalaria VARCHAR(50),
        numero_turno INT PRIMARY KEY,
        edad INT,
        es_embarazada BIT,
        tiene_discapacidad BIT,
        nivel_dolor INT,
        es_cronico BIT,
        atendido BIT
    );
END
GO

-- Crear índices para mejorar el rendimiento
CREATE INDEX IX_Pacientes_NumeroTurno ON pacientes(numero_turno);
CREATE INDEX IX_Pacientes_Atendido ON pacientes(atendido);
CREATE INDEX IX_Pacientes_FechaIngreso ON pacientes(fecha_ingreso);
GO 