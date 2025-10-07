package com.hospital.dao;

import com.hospital.db.Conexion;
import com.hospital.modelo.Cita;
import com.hospital.modelo.Medico;
import com.hospital.modelo.Paciente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {
    private PacienteDAO pacienteDAO = new PacienteDAO();
    private MedicoDAO medicoDAO = new MedicoDAO();
    
    public boolean crear(Cita cita) {
        String sql = "INSERT INTO citas (paciente_id, medico_id, fecha_hora, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, cita.getPaciente().getId());
            stmt.setInt(2, cita.getMedico().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            stmt.setString(4, cita.getEstado());
            
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    cita.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al crear cita: " + e.getMessage());
            return false;
        }
    }
    
    public Cita buscarPorId(int id) {
        String sql = "SELECT * FROM citas WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Paciente paciente = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                Medico medico = medicoDAO.buscarPorId(rs.getInt("medico_id"));
                
                return new Cita(
                    rs.getInt("id"),
                    paciente,
                    medico,
                    rs.getTimestamp("fecha_hora").toLocalDateTime(),
                    rs.getString("estado")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cita: " + e.getMessage());
        }
        return null;
    }
    
    public List<Cita> listarTodas() {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM citas";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Paciente paciente = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                Medico medico = medicoDAO.buscarPorId(rs.getInt("medico_id"));
                
                citas.add(new Cita(
                    rs.getInt("id"),
                    paciente,
                    medico,
                    rs.getTimestamp("fecha_hora").toLocalDateTime(),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar citas: " + e.getMessage());
        }
        return citas;
    }
    
    public List<Cita> buscarPorFecha(LocalDateTime fecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM citas WHERE CAST(fecha_hora AS DATE) = CAST(? AS DATE)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(fecha));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Paciente paciente = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                Medico medico = medicoDAO.buscarPorId(rs.getInt("medico_id"));
                
                citas.add(new Cita(
                    rs.getInt("id"),
                    paciente,
                    medico,
                    rs.getTimestamp("fecha_hora").toLocalDateTime(),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar citas por fecha: " + e.getMessage());
        }
        return citas;
    }
    
    public List<Cita> buscarPorMedico(int medicoId) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM citas WHERE medico_id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, medicoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Paciente paciente = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                Medico medico = medicoDAO.buscarPorId(rs.getInt("medico_id"));
                
                citas.add(new Cita(
                    rs.getInt("id"),
                    paciente,
                    medico,
                    rs.getTimestamp("fecha_hora").toLocalDateTime(),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar citas por médico: " + e.getMessage());
        }
        return citas;
    }
    
    public boolean actualizar(Cita cita) {
        String sql = "UPDATE citas SET paciente_id = ?, medico_id = ?, fecha_hora = ?, estado = ? WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cita.getPaciente().getId());
            stmt.setInt(2, cita.getMedico().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            stmt.setString(4, cita.getEstado());
            stmt.setInt(5, cita.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cita: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cita: " + e.getMessage());
            return false;
        }
    }
} 