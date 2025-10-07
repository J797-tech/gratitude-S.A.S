import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Conexión exitosa");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 