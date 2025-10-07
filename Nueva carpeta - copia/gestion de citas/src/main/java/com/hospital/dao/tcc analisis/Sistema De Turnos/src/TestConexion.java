import java.sql.Connection;
import java.sql.SQLException;

public class TestConexion {
    public static void main(String[] args) {
        try {
            System.out.println("Intentando conectar a la base de datos...");
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("¡Conexión exitosa!");
            conn.close();
            System.out.println("Conexión cerrada.");
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos:");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 