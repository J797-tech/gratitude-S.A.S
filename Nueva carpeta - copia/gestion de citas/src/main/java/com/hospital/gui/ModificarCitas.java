package com.hospital.gui;

import com.hospital.dao.CitaDAO;
import com.hospital.dao.MedicoDAO;
import com.hospital.dao.PacienteDAO;
import com.hospital.modelo.Cita;
import com.hospital.modelo.Medico;
import com.hospital.modelo.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ModificarCitas extends JFrame {
    private CitaDAO citaDAO;
    private MedicoDAO medicoDAO;
    private PacienteDAO pacienteDAO;
    
    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private JComboBox<Medico> cboMedicos;
    private JSpinner spinnerFecha;
    private JSpinner spinnerHora;
    private JComboBox<String> cboEstado;
    
    public ModificarCitas() {
        citaDAO = new CitaDAO();
        medicoDAO = new MedicoDAO();
        pacienteDAO = new PacienteDAO();
        
        configurarVentana();
        inicializarComponentes();
        cargarCitas();
    }
    
    private void configurarVentana() {
        setTitle("Modificar/Cancelar Citas");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel de tabla
        String[] columnas = {"ID", "Paciente", "Médico", "Fecha y Hora", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaCitas = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaCitas);
        
        // Panel de edición
        JPanel panelEdicion = new JPanel(new GridLayout(6, 2, 5, 5));
        panelEdicion.setBorder(BorderFactory.createTitledBorder("Modificar Cita"));
        
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
        
        // Combo de estado
        cboEstado = new JComboBox<>(new String[]{"PROGRAMADA", "CANCELADA", "COMPLETADA"});
        
        panelEdicion.add(new JLabel("Médico:"));
        panelEdicion.add(cboMedicos);
        panelEdicion.add(new JLabel("Fecha:"));
        panelEdicion.add(spinnerFecha);
        panelEdicion.add(new JLabel("Hora:"));
        panelEdicion.add(spinnerHora);
        panelEdicion.add(new JLabel("Estado:"));
        panelEdicion.add(cboEstado);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnModificar = new JButton("Modificar");
        JButton btnCancelar = new JButton("Cancelar Cita");
        JButton btnCerrar = new JButton("Cerrar");
        
        btnModificar.addActionListener(e -> modificarCita());
        btnCancelar.addActionListener(e -> cancelarCita());
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnModificar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnCerrar);
        
        // Agregar listener a la tabla
        tablaCitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarCitaSeleccionada();
            }
        });
        
        // Panel derecho
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(panelEdicion, BorderLayout.CENTER);
        panelDerecho.add(panelBotones, BorderLayout.SOUTH);
        
        // Agregar componentes a la ventana
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, panelDerecho);
        splitPane.setDividerLocation(600);
        add(splitPane);
    }
    
    private void cargarCitas() {
        modeloTabla.setRowCount(0);
        List<Cita> citas = citaDAO.listarTodas();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Cita cita : citas) {
            Object[] fila = {
                cita.getId(),
                cita.getPaciente().getNombre() + " - " + cita.getPaciente().getCedula(),
                cita.getMedico().getNombre() + " - " + cita.getMedico().getEspecialidad(),
                cita.getFechaHora().format(formatter),
                cita.getEstado()
            };
            modeloTabla.addRow(fila);
        }
        
        // Cargar médicos en el combo
        List<Medico> medicos = medicoDAO.listarTodos();
        for (Medico medico : medicos) {
            cboMedicos.addItem(medico);
        }
    }
    
    private void cargarCitaSeleccionada() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada >= 0) {
            int citaId = (int) tablaCitas.getValueAt(filaSeleccionada, 0);
            Cita cita = citaDAO.buscarPorId(citaId);
            
            if (cita != null) {
                // Seleccionar médico
                for (int i = 0; i < cboMedicos.getItemCount(); i++) {
                    Medico medico = cboMedicos.getItemAt(i);
                    if (medico.getId() == cita.getMedico().getId()) {
                        cboMedicos.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Establecer fecha y hora
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(cita.getFechaHora().getYear(),
                       cita.getFechaHora().getMonthValue() - 1,
                       cita.getFechaHora().getDayOfMonth());
                spinnerFecha.setValue(cal.getTime());
                
                cal.set(java.util.Calendar.HOUR_OF_DAY, cita.getFechaHora().getHour());
                cal.set(java.util.Calendar.MINUTE, cita.getFechaHora().getMinute());
                spinnerHora.setValue(cal.getTime());
                
                // Establecer estado
                cboEstado.setSelectedItem(cita.getEstado());
            }
        }
    }
    
    private void modificarCita() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una cita para modificar",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int citaId = (int) tablaCitas.getValueAt(filaSeleccionada, 0);
        Cita cita = citaDAO.buscarPorId(citaId);
        
        if (cita != null) {
            // Actualizar datos de la cita
            cita.setMedico((Medico) cboMedicos.getSelectedItem());
            
            java.util.Date fechaSpinner = (java.util.Date) spinnerFecha.getValue();
            java.util.Date horaSpinner = (java.util.Date) spinnerHora.getValue();
            
            LocalDateTime fechaHora = LocalDateTime.of(
                fechaSpinner.getYear() + 1900,
                fechaSpinner.getMonth() + 1,
                fechaSpinner.getDate(),
                horaSpinner.getHours(),
                horaSpinner.getMinutes()
            );
            
            cita.setFechaHora(fechaHora);
            cita.setEstado((String) cboEstado.getSelectedItem());
            
            if (citaDAO.actualizar(cita)) {
                JOptionPane.showMessageDialog(this, "Cita modificada exitosamente");
                cargarCitas();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al modificar la cita",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelarCita() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una cita para cancelar",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de cancelar esta cita?",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            int citaId = (int) tablaCitas.getValueAt(filaSeleccionada, 0);
            Cita cita = citaDAO.buscarPorId(citaId);
            
            if (cita != null) {
                cita.setEstado("CANCELADA");
                if (citaDAO.actualizar(cita)) {
                    JOptionPane.showMessageDialog(this, "Cita cancelada exitosamente");
                    cargarCitas();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al cancelar la cita",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ModificarCitas().setVisible(true);
        });
    }
} 