package com.hospital.gui;

import com.hospital.dao.CitaDAO;
import com.hospital.dao.MedicoDAO;
import com.hospital.dao.PacienteDAO;
import com.hospital.modelo.Cita;
import com.hospital.modelo.Medico;
import com.hospital.modelo.Paciente;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AgendarCita extends JFrame {
    private CitaDAO citaDAO;
    private PacienteDAO pacienteDAO;
    private MedicoDAO medicoDAO;
    
    private JComboBox<Paciente> cboPacientes;
    private JComboBox<Medico> cboMedicos;
    private JSpinner spinnerFecha;
    private JSpinner spinnerHora;
    
    public AgendarCita() {
        citaDAO = new CitaDAO();
        pacienteDAO = new PacienteDAO();
        medicoDAO = new MedicoDAO();
        
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }
    
    private void configurarVentana() {
        setTitle("Agendar Cita");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Combo de pacientes
        cboPacientes = new JComboBox<>();
        cboPacientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Paciente) {
                    Paciente paciente = (Paciente) value;
                    value = paciente.getNombre() + " - " + paciente.getCedula();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        
        // Combo de médicos
        cboMedicos = new JComboBox<>();
        cboMedicos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Medico) {
                    Medico medico = (Medico) value;
                    value = medico.getNombre() + " - " + medico.getEspecialidad();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        
        // Spinner para fecha
        SpinnerDateModel modeloFecha = new SpinnerDateModel();
        spinnerFecha = new JSpinner(modeloFecha);
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(editorFecha);
        
        // Spinner para hora
        SpinnerDateModel modeloHora = new SpinnerDateModel();
        spinnerHora = new JSpinner(modeloHora);
        JSpinner.DateEditor editorHora = new JSpinner.DateEditor(spinnerHora, "HH:mm");
        spinnerHora.setEditor(editorHora);
        
        panelFormulario.add(new JLabel("Paciente:"));
        panelFormulario.add(cboPacientes);
        panelFormulario.add(new JLabel("Médico:"));
        panelFormulario.add(cboMedicos);
        panelFormulario.add(new JLabel("Fecha:"));
        panelFormulario.add(spinnerFecha);
        panelFormulario.add(new JLabel("Hora:"));
        panelFormulario.add(spinnerHora);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAgendar = new JButton("Agendar Cita");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAgendar.addActionListener(e -> agendarCita());
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnAgendar);
        panelBotones.add(btnCancelar);
        
        // Agregar componentes a la ventana
        add(panelFormulario, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        // Cargar pacientes
        List<Paciente> pacientes = pacienteDAO.listarTodos();
        for (Paciente paciente : pacientes) {
            cboPacientes.addItem(paciente);
        }
        
        // Cargar médicos
        List<Medico> medicos = medicoDAO.listarTodos();
        for (Medico medico : medicos) {
            cboMedicos.addItem(medico);
        }
    }
    
    private void agendarCita() {
        if (!validarCampos()) {
            return;
        }
        
        Paciente paciente = (Paciente) cboPacientes.getSelectedItem();
        Medico medico = (Medico) cboMedicos.getSelectedItem();
        
        java.util.Date fechaSpinner = (java.util.Date) spinnerFecha.getValue();
        java.util.Date horaSpinner = (java.util.Date) spinnerHora.getValue();
        
        LocalDate fecha = LocalDate.of(
            fechaSpinner.getYear() + 1900,
            fechaSpinner.getMonth() + 1,
            fechaSpinner.getDate()
        );
        
        LocalTime hora = LocalTime.of(
            horaSpinner.getHours(),
            horaSpinner.getMinutes()
        );
        
        LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);
        
        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setFechaHora(fechaHora);
        cita.setEstado("PROGRAMADA");
        
        if (citaDAO.crear(cita)) {
            JOptionPane.showMessageDialog(this, "Cita agendada exitosamente");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al agendar la cita",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarCampos() {
        if (cboPacientes.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un paciente",
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (cboMedicos.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un médico",
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AgendarCita().setVisible(true);
        });
    }
} 