# Sistema de Gestión de Turnos Hospitalarios

Este sistema permite gestionar los turnos de pacientes en un hospital, clasificándolos por área hospitalaria y prioridad.

## Requisitos Previos

1. Java JDK 8 o superior
2. MySQL Server 5.7 o superior
3. MySQL Connector/J (el driver JDBC para MySQL)

## Configuración de la Base de Datos

1. Instalar MySQL Server si aún no está instalado
2. Abrir MySQL Workbench o la línea de comandos de MySQL
3. Ejecutar el script `database.sql` para crear la base de datos y las tablas necesarias

## Configuración del Proyecto

1. Asegúrese de tener el conector MySQL en su proyecto. Puede descargarlo de:
   https://dev.mysql.com/downloads/connector/j/

2. Agregue el archivo .jar del conector MySQL al classpath de su proyecto

3. Modifique las credenciales de la base de datos en la clase `PacienteDAO.java` si es necesario:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/Sistemadeturnos";
   private static final String USER = "root";
   private static final String PASSWORD = "";
   ```

## Compilación y Ejecución

1. Compile todos los archivos Java:
   ```bash
   javac *.java
   ```

2. Ejecute la aplicación:
   ```bash
   java SistemaDeTurnos
   ```

## Funcionalidades

- Registro de pacientes con información detallada
- Clasificación automática por área hospitalaria
- Asignación automática de prioridades
- Gestión de turnos por área
- Historial de pacientes atendidos
- Consideración de factores especiales (embarazo, discapacidad, edad, etc.)
- Interfaz gráfica intuitiva

## Notas Importantes

- La prioridad se calcula automáticamente basándose en varios factores como:
  - Edad del paciente
  - Nivel de dolor
  - Condiciones especiales (embarazo, discapacidad)
  - Síntomas y enfermedades reportadas
  
- Las áreas hospitalarias disponibles son:
  - Emergencia
  - Cardiología
  - Neurología
  - General
  - Ambulatorio 