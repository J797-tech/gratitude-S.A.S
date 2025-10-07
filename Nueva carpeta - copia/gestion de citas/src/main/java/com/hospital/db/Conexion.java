package com.hospital.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    // Configuración de la conexión a la base de datos
    private static final String URL = "jdbc:sqlserver://LISANDROHERRERA\\SQLEXPRESS;databaseName=hospital_db;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";      // Usuario de SQL Server
    private static final String PASSWORD = "123";  // Contraseña del usuario sa

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Intentando conectar a la base de datos...");
                System.out.println("URL de conexión: " + URL);
                
                // Cargar el driver
                try {
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    System.out.println("Driver JDBC cargado correctamente");
                } catch (ClassNotFoundException e) {
                    System.err.println("Error al cargar el driver JDBC: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
                
                // Intentar la conexión
                try {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Conexión establecida exitosamente");
                } catch (SQLException e) {
                    System.err.println("Error al establecer la conexión: " + e.getMessage());
                    System.err.println("Estado SQL: " + e.getSQLState());
                    System.err.println("Código de error: " + e.getErrorCode());
                    e.printStackTrace();
                    return null;
                }
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Error general de conexión: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada exitosamente");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 