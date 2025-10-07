package com.hospital.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JFrame {
    
    public MenuPrincipal() {
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Sistema de Gestión de Citas Hospitalarias");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("Sistema de Gestión de Citas Hospitalarias");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con botones
        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 20, 20));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JButton btnPacientes = crearBoton("Gestión de Pacientes", e -> abrirGestionPacientes());
        JButton btnMedicos = crearBoton("Gestión de Médicos", e -> abrirGestionMedicos());
        JButton btnAgendarCita = crearBoton("Agendar Cita", e -> abrirAgendarCita());
        JButton btnConsultarCitas = crearBoton("Consultar Citas", e -> abrirConsultarCitas());
        JButton btnModificarCitas = crearBoton("Modificar/Cancelar Citas", e -> abrirModificarCitas());
        JButton btnSalir = crearBoton("Salir", e -> System.exit(0));
        
        panelBotones.add(btnPacientes);
        panelBotones.add(btnMedicos);
        panelBotones.add(btnAgendarCita);
        panelBotones.add(btnConsultarCitas);
        panelBotones.add(btnModificarCitas);
        panelBotones.add(btnSalir);
        
        add(panelBotones, BorderLayout.CENTER);
    }
    
    private JButton crearBoton(String texto, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.PLAIN, 16));
        boton.addActionListener(listener);
        return boton;
    }
    
    private void abrirGestionPacientes() {
        GestionPacientes ventanaPacientes = new GestionPacientes();
        ventanaPacientes.setVisible(true);
    }
    
    private void abrirGestionMedicos() {
        GestionMedicos ventanaMedicos = new GestionMedicos();
        ventanaMedicos.setVisible(true);
    }
    
    private void abrirAgendarCita() {
        AgendarCita ventanaAgendarCita = new AgendarCita();
        ventanaAgendarCita.setVisible(true);
    }
    
    private void abrirConsultarCitas() {
        ConsultarCitas ventanaConsultarCitas = new ConsultarCitas();
        ventanaConsultarCitas.setVisible(true);
    }
    
    private void abrirModificarCitas() {
        ModificarCitas ventanaModificarCitas = new ModificarCitas();
        ventanaModificarCitas.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MenuPrincipal().setVisible(true);
        });
    }
} 