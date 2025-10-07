package com.hospital.gui;

import com.hospital.dao.MedicoDAO;
import com.hospital.modelo.Medico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionMedicos extends JFrame {
    private MedicoDAO medicoDAO;
    private JTextField txtId, txtNombre, txtEspecialidad, txtHorario;
    private JTable tablaMedicos;
    private DefaultTableModel modeloTabla;
    
    public GestionMedicos() {
        medicoDAO = new MedicoDAO();
        configurarVentana();
        inicializarComponentes();
        cargarMedicos();
    }
    
    private void configurarVentana() {
        setTitle("Gestión de Médicos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Médico"));
        
        txtId = new JTextField();
        txtId.setEditable(false);
        txtNombre = new JTextField();
        txtEspecialidad = new JTextField();
        txtHorario = new JTextField();
        
        panelFormulario.add(new JLabel("ID:"));
        panelFormulario.add(txtId);
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Especialidad:"));
        panelFormulario.add(txtEspecialidad);
        panelFormulario.add(new JLabel("Horario Disponible:"));
        panelFormulario.add(txtHorario);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarMedico());
        btnEliminar.addActionListener(e -> eliminarMedico());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        
        // Panel superior combinando formulario y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        
        // Tabla de médicos
        String[] columnas = {"ID", "Nombre", "Especialidad", "Horario Disponible"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMedicos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaMedicos);
        
        tablaMedicos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarMedicoSeleccionado();
            }
        });
        
        // Agregar componentes a la ventana
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void cargarMedicos() {
        modeloTabla.setRowCount(0);
        List<Medico> medicos = medicoDAO.listarTodos();
        for (Medico medico : medicos) {
            Object[] fila = {
                medico.getId(),
                medico.getNombre(),
                medico.getEspecialidad(),
                medico.getHorarioDisponible()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void cargarMedicoSeleccionado() {
        int filaSeleccionada = tablaMedicos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            txtId.setText(tablaMedicos.getValueAt(filaSeleccionada, 0).toString());
            txtNombre.setText(tablaMedicos.getValueAt(filaSeleccionada, 1).toString());
            txtEspecialidad.setText(tablaMedicos.getValueAt(filaSeleccionada, 2).toString());
            txtHorario.setText(tablaMedicos.getValueAt(filaSeleccionada, 3).toString());
        }
    }
    
    private void guardarMedico() {
        if (!validarCampos()) {
            return;
        }
        
        Medico medico = new Medico();
        if (!txtId.getText().isEmpty()) {
            medico.setId(Integer.parseInt(txtId.getText()));
        }
        medico.setNombre(txtNombre.getText());
        medico.setEspecialidad(txtEspecialidad.getText());
        medico.setHorarioDisponible(txtHorario.getText());
        
        boolean exito;
        if (medico.getId() == 0) {
            exito = medicoDAO.crear(medico);
        } else {
            exito = medicoDAO.actualizar(medico);
        }
        
        if (exito) {
            JOptionPane.showMessageDialog(this, "Médico guardado exitosamente");
            limpiarFormulario();
            cargarMedicos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el médico", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarMedico() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un médico para eliminar");
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este médico?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            if (medicoDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Médico eliminado exitosamente");
                limpiarFormulario();
                cargarMedicos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el médico", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
            txtEspecialidad.getText().trim().isEmpty() ||
            txtHorario.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Todos los campos son obligatorios",
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtEspecialidad.setText("");
        txtHorario.setText("");
        tablaMedicos.clearSelection();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GestionMedicos().setVisible(true);
        });
    }
} 