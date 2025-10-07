import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Modificar estos valores con tus credenciales
    private static final String SERVER = "localhost,1433";
    private static final String DATABASE = "Sistemadeturnos";
    private static final String USER = "sa"; // Usuario de SQL Server
    private static final String PASSWORD = "123456"; // Cambia esto por tu contraseña de SQL Server
    
    private static final String URL = String.format(
        "jdbc:sqlserver://%s;databaseName=%s;user=%s;password=%s;trustServerCertificate=true;encrypt=true;loginTimeout=30;authentication=SqlPassword",
        SERVER, DATABASE, USER, PASSWORD
    );

    public static Connection getConnection() throws SQLException {
        try {
            System.out.println("Intentando conectar a la base de datos...");
            System.out.println("URL de conexión: " + URL);
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("¡Conexión exitosa!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se pudo encontrar el driver JDBC de SQL Server");
            System.err.println("Asegúrate de que el archivo mssql-jdbc-12.8.1.jre8.jar esté en la carpeta lib");
            throw new SQLException("Error al cargar el driver de SQL Server", e);
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos:");
            System.err.println("Código de error: " + e.getErrorCode());
            System.err.println("Estado SQL: " + e.getSQLState());
            System.err.println("Mensaje: " + e.getMessage());
            throw e;
        }
    }

    // Método para probar la conexión
    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            System.out.println("¡Conexión exitosa a la base de datos!");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos:");
            e.printStackTrace();
        }
    }
} 