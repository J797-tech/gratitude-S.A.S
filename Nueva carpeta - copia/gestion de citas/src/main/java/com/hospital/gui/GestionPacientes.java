package com.hospital.gui;

import com.hospital.dao.PacienteDAO;
import com.hospital.modelo.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionPacientes extends JFrame {
    private PacienteDAO pacienteDAO;
    private JTextField txtId, txtNombre, txtCedula, txtTelefono, txtDireccion;
    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;
    
    public GestionPacientes() {
        pacienteDAO = new PacienteDAO();
        configurarVentana();
        inicializarComponentes();
        cargarPacientes();
    }
    
    private void configurarVentana() {
        setTitle("Gestión de Pacientes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Paciente"));
        
        txtId = new JTextField();
        txtId.setEditable(false);
        txtNombre = new JTextField();
        txtCedula = new JTextField();
        txtTelefono = new JTextField();
        txtDireccion = new JTextField();
        
        panelFormulario.add(new JLabel("ID:"));
        panelFormulario.add(txtId);
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Cédula:"));
        panelFormulario.add(txtCedula);
        panelFormulario.add(new JLabel("Teléfono:"));
        panelFormulario.add(txtTelefono);
        panelFormulario.add(new JLabel("Dirección:"));
        panelFormulario.add(txtDireccion);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarPaciente());
        btnEliminar.addActionListener(e -> eliminarPaciente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        
        panelFormulario.add(panelBotones);
        
        // Panel superior combinando formulario y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        
        // Tabla de pacientes
        String[] columnas = {"ID", "Nombre", "Cédula", "Teléfono", "Dirección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPacientes = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaPacientes);
        
        tablaPacientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarPacienteSeleccionado();
            }
        });
        
        // Agregar componentes a la ventana
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void cargarPacientes() {
        modeloTabla.setRowCount(0);
        List<Paciente> pacientes = pacienteDAO.listarTodos();
        for (Paciente paciente : pacientes) {
            Object[] fila = {
                paciente.getId(),
                paciente.getNombre(),
                paciente.getCedula(),
                paciente.getTelefono(),
                paciente.getDireccion()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void cargarPacienteSeleccionado() {
        int filaSeleccionada = tablaPacientes.getSelectedRow();
        if (filaSeleccionada >= 0) {
            txtId.setText(tablaPacientes.getValueAt(filaSeleccionada, 0).toString());
            txtNombre.setText(tablaPacientes.getValueAt(filaSeleccionada, 1).toString());
            txtCedula.setText(tablaPacientes.getValueAt(filaSeleccionada, 2).toString());
            txtTelefono.setText(tablaPacientes.getValueAt(filaSeleccionada, 3).toString());
            txtDireccion.setText(tablaPacientes.getValueAt(filaSeleccionada, 4).toString());
        }
    }
    
    private void guardarPaciente() {
        if (!validarCampos()) {
            return;
        }
        
        Paciente paciente = new Paciente();
        if (!txtId.getText().isEmpty()) {
            paciente.setId(Integer.parseInt(txtId.getText()));
        }
        paciente.setNombre(txtNombre.getText());
        paciente.setCedula(txtCedula.getText());
        paciente.setTelefono(txtTelefono.getText());
        paciente.setDireccion(txtDireccion.getText());
        
        boolean exito;
        if (paciente.getId() == 0) {
            exito = pacienteDAO.crear(paciente);
        } else {
            exito = pacienteDAO.actualizar(paciente);
        }
        
        if (exito) {
            JOptionPane.showMessageDialog(this, "Paciente guardado exitosamente");
            limpiarFormulario();
            cargarPacientes();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el paciente", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarPaciente() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para eliminar");
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este paciente?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            if (pacienteDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Paciente eliminado exitosamente");
                limpiarFormulario();
                cargarPacientes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el paciente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
            txtCedula.getText().trim().isEmpty() ||
            txtTelefono.getText().trim().isEmpty() ||
            txtDireccion.getText().trim().isEmpty()) {
            
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
        txtCedula.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        tablaPacientes.clearSelection();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GestionPacientes().setVisible(true);
        });
    }
} 