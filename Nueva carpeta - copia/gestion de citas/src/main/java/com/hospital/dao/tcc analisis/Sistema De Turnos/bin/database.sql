-- Crear la base de datos si no existe
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'Sistemadeturnos')
BEGIN
    CREATE DATABASE Sistemadeturnos;
END
GO

USE Sistemadeturnos;
GO

-- Crear la tabla de pacientes si no existe
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[pacientes]') AND type in (N'U'))
BEGIN
    CREATE TABLE pacientes (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(100) NOT NULL,
        prioridad VARCHAR(20) NOT NULL,
        sintomas TEXT,
        enfermedades TEXT,
        problemas TEXT,
        area_hospitalaria VARCHAR(50) NOT NULL,
        numero_turno INT NOT NULL UNIQUE,
        edad INT NOT NULL,
        es_embarazada BIT DEFAULT 0,
        tiene_discapacidad BIT DEFAULT 0,
        nivel_dolor INT NOT NULL,
        es_cronico BIT DEFAULT 0,
        atendido BIT DEFAULT 0,
        fecha_creacion DATETIME DEFAULT GETDATE(),
        fecha_atencion DATETIME NULL
    );
END 