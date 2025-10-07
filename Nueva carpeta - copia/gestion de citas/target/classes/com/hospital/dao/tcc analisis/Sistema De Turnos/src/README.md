# Sistema de Turnos Hospitalario

Este sistema permite gestionar los turnos de pacientes en un hospital, clasificándolos por prioridad y área hospitalaria.

## Requisitos

- Java JDK 8 o superior
- SQL Server Express
- Driver JDBC para SQL Server

## Configuración

1. Instalar SQL Server Express
2. Crear la base de datos usando el script `database.sql`
3. Asegurarse de que el driver JDBC esté en el classpath

## Estructura del Proyecto

- `SistemaDeTurnos.java`: Clase principal con la interfaz gráfica
- `PacienteDAO.java`: Clase para el acceso a datos
- `DatabaseConnection.java`: Clase para manejar la conexión a la base de datos
- `TestConexion.java`: Clase para probar la conexión
- `database.sql`: Script para crear la base de datos

## Características

- Clasificación de pacientes por prioridad
- Distribución por áreas hospitalarias
- Gestión de turnos
- Historial de pacientes atendidos
- Interfaz gráfica intuitiva

## Uso

1. Ejecutar la clase `SistemaDeTurnos`
2. Ingresar los datos del paciente
3. El sistema asignará automáticamente el área y prioridad
4. Gestionar los turnos desde la interfaz

## Notas

- La prioridad se determina automáticamente según los síntomas y condiciones
- Se mantiene un historial de pacientes atendidos
- Los turnos se asignan secuencialmente 