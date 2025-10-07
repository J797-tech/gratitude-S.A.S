package com.hospital.gui;

import com.hospital.dao.CitaDAO;
import com.hospital.dao.MedicoDAO;
import com.hospital.modelo.Cita;
import com.hospital.modelo.Medico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultarCitas extends JFrame {
    private CitaDAO citaDAO;
    private MedicoDAO medicoDAO;
    
    private JComboBox<Medico> cboMedicos;
    private JSpinner spinnerFecha;
    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    
    public ConsultarCitas() {
        citaDAO = new CitaDAO();
        medicoDAO = new MedicoDAO();
        
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }
    
    private void configurarVentana() {
        setTitle("Consultar Citas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de búsqueda"));
        
        // Combo de médicos
        cboMedicos = new JComboBox<>();
        cboMedicos.addItem(null); // Opción para mostrar todos
        cboMedicos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    value = "Todos los médicos";
                } else if (value instanceof Medico) {
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
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarCitas());
        
        panelFiltros.add(new JLabel("Médico:"));
        panelFiltros.add(cboMedicos);
        panelFiltros.add(new JLabel("Fecha:"));
        panelFiltros.add(spinnerFecha);
        panelFiltros.add(btnBuscar);
        
        // Tabla de citas
        String[] columnas = {"ID", "Paciente", "Médico", "Fecha y Hora", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaCitas = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaCitas);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelBotones.add(btnCerrar);
        
        // Agregar componentes a la ventana
        add(panelFiltros, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        // Cargar médicos
        List<Medico> medicos = medicoDAO.listarTodos();
        for (Medico medico : medicos) {
            cboMedicos.addItem(medico);
        }
        
        // Cargar todas las citas inicialmente
        buscarCitas();
    }
    
    private void buscarCitas() {
        modeloTabla.setRowCount(0);
        List<Cita> citas;
        
        Medico medicoSeleccionado = (Medico) cboMedicos.getSelectedItem();
        java.util.Date fechaSeleccionada = (java.util.Date) spinnerFecha.getValue();
        
        if (medicoSeleccionado != null) {
            citas = citaDAO.buscarPorMedico(medicoSeleccionado.getId());
        } else {
            citas = citaDAO.listarTodas();
        }
        
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
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ConsultarCitas().setVisible(true);
        });
    }
} 