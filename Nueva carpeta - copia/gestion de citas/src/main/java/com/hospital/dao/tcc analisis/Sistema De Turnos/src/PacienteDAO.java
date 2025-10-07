import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {
    private static final String URL = "jdbc:sqlserver://lisandroherrera\\SQLEXPRESS;databaseName=Sistemadeturnos;integratedSecurity=true;trustServerCertificate=true";

    public PacienteDAO() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void guardarPaciente(SistemaDeTurnos.Paciente paciente) throws SQLException {
        String sql = "INSERT INTO pacientes (nombre, prioridad, sintomas, enfermedades, " +
                    "problemas, area_hospitalaria, numero_turno, edad, es_embarazada, " +
                    "tiene_discapacidad, nivel_dolor, es_cronico, atendido) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, false)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paciente.nombre);
            pstmt.setString(2, paciente.prioridad.toString());
            pstmt.setString(3, paciente.sintomas);
            pstmt.setString(4, paciente.enfermedades);
            pstmt.setString(5, paciente.problemas);
            pstmt.setString(6, paciente.areaHospitalaria);
            pstmt.setInt(7, paciente.numeroTurno);
            pstmt.setInt(8, paciente.edad);
            pstmt.setBoolean(9, paciente.esEmbarazada);
            pstmt.setBoolean(10, paciente.tieneDiscapacidad);
            pstmt.setInt(11, paciente.nivelDolor);
            pstmt.setBoolean(12, paciente.esCronico);

            pstmt.executeUpdate();
        }
    }

    public List<SistemaDeTurnos.Paciente> obtenerPacientesEnEspera() throws SQLException {
        List<SistemaDeTurnos.Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE atendido = false";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pacientes.add(crearPacienteDesdeResultSet(rs));
            }
        }
        return pacientes;
    }

    public List<SistemaDeTurnos.Paciente> obtenerHistorialPacientes() throws SQLException {
        List<SistemaDeTurnos.Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE atendido = true ORDER BY numero_turno DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pacientes.add(crearPacienteDesdeResultSet(rs));
            }
        }
        return pacientes;
    }

    public void marcarComoAtendido(int numeroTurno) throws SQLException {
        String sql = "UPDATE pacientes SET atendido = true WHERE numero_turno = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, numeroTurno);
            pstmt.executeUpdate();
        }
    }

    public void eliminarPaciente(int numeroTurno) throws SQLException {
        String sql = "DELETE FROM pacientes WHERE numero_turno = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, numeroTurno);
            pstmt.executeUpdate();
        }
    }

    private SistemaDeTurnos.Paciente crearPacienteDesdeResultSet(ResultSet rs) throws SQLException {
        return new SistemaDeTurnos.Paciente(
            rs.getString("nombre"),
            SistemaDeTurnos.Prioridad.valueOf(rs.getString("prioridad")),
            rs.getString("sintomas"),
            rs.getString("enfermedades"),
            rs.getString("problemas"),
            rs.getString("area_hospitalaria"),
            rs.getInt("numero_turno"),
            rs.getInt("edad"),
            rs.getBoolean("es_embarazada"),
            rs.getBoolean("tiene_discapacidad"),
            rs.getInt("nivel_dolor"),
            rs.getBoolean("es_cronico")
        );
    }
} 