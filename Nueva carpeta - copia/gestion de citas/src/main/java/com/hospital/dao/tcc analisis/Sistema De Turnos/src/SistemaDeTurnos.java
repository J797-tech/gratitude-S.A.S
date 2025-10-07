import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SistemaDeTurnos extends JFrame {

    enum Prioridad {
        ALTA, MEDIA, BAJA
    }

    static class Paciente {
        String nombre;
        Prioridad prioridad;
        String sintomas;
        String enfermedades;
        String problemas;
        String areaHospitalaria;
        int numeroTurno;
        int edad;
        boolean esEmbarazada;
        boolean tieneDiscapacidad;
        int nivelDolor;
        boolean esCronico;
        
        Paciente(String nombre, Prioridad prioridad, String sintomas, String enfermedades, 
                String problemas, String areaHospitalaria, int numeroTurno, 
                int edad, boolean esEmbarazada, boolean tieneDiscapacidad, 
                int nivelDolor, boolean esCronico) {
            this.nombre = nombre;
            this.prioridad = prioridad;
            this.sintomas = sintomas;
            this.enfermedades = enfermedades;
            this.problemas = problemas;
            this.areaHospitalaria = areaHospitalaria;
            this.numeroTurno = numeroTurno;
            this.edad = edad;
            this.esEmbarazada = esEmbarazada;
            this.tieneDiscapacidad = tieneDiscapacidad;
            this.nivelDolor = nivelDolor;
            this.esCronico = esCronico;
        }

        @Override
        public String toString() {
            return "Turno #" + numeroTurno + ": " + nombre + 
                   " (Área: " + areaHospitalaria + ", Prioridad: " + prioridad + ")\n" +
                   "  Edad: " + edad + " años\n" +
                   "  Síntomas: " + sintomas + "\n" +
                   "  Enfermedades: " + enfermedades + "\n" +
                   "  Problemas: " + problemas + "\n" +
                   "  Nivel de dolor: " + nivelDolor + "/10" +
                   (esEmbarazada ? "\n  Paciente embarazada" : "") +
                   (tieneDiscapacidad ? "\n  Paciente con discapacidad" : "") +
                   (esCronico ? "\n  Paciente crónico" : "");
        }
    }

    // Mapa areaHospitalaria -> (prioridad -> cola de pacientes)
    private Map<String, Map<Prioridad, Queue<Paciente>>> colasAreas;
    private int siguienteNumeroTurno;

    private JTextField campoNombre;
    private JTextArea areaSintomas;
    private JTextArea areaEnfermedades;
    private JTextArea areaProblemas;
    private JComboBox<String> comboAreaLlamada;
    private Map<String, JTextArea> areasTextoporArea;
    private JButton botonAgregar;
    private JButton botonLlamarSiguiente;
    private JTextField campoEdad;
    private JCheckBox checkEmbarazada;
    private JCheckBox checkDiscapacidad;
    private JComboBox<Integer> comboNivelDolor;
    private JCheckBox checkCronico;

    private final String[] areasHospitalarias = {"Emergencia", "Cardiología", "Neurología", "General", "Ambulatorio"};

    private PacienteDAO pacienteDAO;
    private JButton botonEliminar;
    private JButton botonHistorial;
    private JFrame ventanaHistorial;

    public SistemaDeTurnos() {
        super("Sistema De Turnos - Clasificación Avanzada y Distribución por Área y Prioridad");
        siguienteNumeroTurno = 1;
        pacienteDAO = new PacienteDAO();

        colasAreas = new HashMap<>();
        for (String area : areasHospitalarias) {
            Map<Prioridad, Queue<Paciente>> mapaPrioridades = new EnumMap<>(Prioridad.class);
            for (Prioridad p : Prioridad.values()) {
                mapaPrioridades.put(p, new LinkedList<>());
            }
            colasAreas.put(area, mapaPrioridades);
        }

        cargarPacientesDesdeDB();
        crearInterfaz();
    }

    private void cargarPacientesDesdeDB() {
        try {
            List<Paciente> pacientesEnEspera = pacienteDAO.obtenerPacientesEnEspera();
            for (Paciente paciente : pacientesEnEspera) {
                colasAreas.get(paciente.areaHospitalaria)
                         .get(paciente.prioridad)
                         .add(paciente);
                if (paciente.numeroTurno >= siguienteNumeroTurno) {
                    siguienteNumeroTurno = paciente.numeroTurno + 1;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error detallado de conexión:");
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("Código de error: " + e.getErrorCode());
            System.out.println("Estado SQL: " + e.getSQLState());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(this, 
                "Error al cargar pacientes desde la base de datos:\n" +
                "Mensaje: " + e.getMessage() + "\n" +
                "Código de error: " + e.getErrorCode() + "\n" +
                "Estado SQL: " + e.getSQLState(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearInterfaz() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 800);
        setLocationRelativeTo(null);

        // Panel principal con márgenes
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel contenedor para todo el contenido
        JPanel panelContenedor = new JPanel(new BorderLayout(10, 10));

        // Panel de entrada con título
        JPanel panelEntrada = new JPanel(new GridBagLayout());
        panelEntrada.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Datos del Paciente"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Configuración de fuentes
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font inputFont = new Font("Arial", Font.PLAIN, 12);

        // Componentes de entrada
        JLabel etiquetaNombre = new JLabel("Nombre del paciente:");
        etiquetaNombre.setFont(labelFont);
        campoNombre = new JTextField(30);
        campoNombre.setFont(inputFont);

        JLabel etiquetaSintomas = new JLabel("Describa sus síntomas:");
        etiquetaSintomas.setFont(labelFont);
        areaSintomas = new JTextArea(3, 30);
        areaSintomas.setFont(inputFont);
        areaSintomas.setLineWrap(true);
        areaSintomas.setWrapStyleWord(true);
        JScrollPane scrollSintomas = new JScrollPane(areaSintomas);

        JLabel etiquetaEnfermedades = new JLabel("Enfermedades que presenta:");
        etiquetaEnfermedades.setFont(labelFont);
        areaEnfermedades = new JTextArea(3, 30);
        areaEnfermedades.setFont(inputFont);
        areaEnfermedades.setLineWrap(true);
        areaEnfermedades.setWrapStyleWord(true);
        JScrollPane scrollEnfermedades = new JScrollPane(areaEnfermedades);

        JLabel etiquetaProblemas = new JLabel("Otros problemas relevantes:");
        etiquetaProblemas.setFont(labelFont);
        areaProblemas = new JTextArea(3, 30);
        areaProblemas.setFont(inputFont);
        areaProblemas.setLineWrap(true);
        areaProblemas.setWrapStyleWord(true);
        JScrollPane scrollProblemas = new JScrollPane(areaProblemas);

        JLabel etiquetaEdad = new JLabel("Edad:");
        etiquetaEdad.setFont(labelFont);
        campoEdad = new JTextField(5);
        campoEdad.setFont(inputFont);

        // Panel para checkboxes
        JPanel panelCheckboxes = new JPanel(new GridBagLayout());
        GridBagConstraints gbcCheck = new GridBagConstraints();
        gbcCheck.anchor = GridBagConstraints.WEST;
        gbcCheck.insets = new Insets(2, 2, 2, 2);

        checkEmbarazada = new JCheckBox("Paciente embarazada");
        checkDiscapacidad = new JCheckBox("Paciente con discapacidad");
        checkCronico = new JCheckBox("Paciente crónico");
        
        checkEmbarazada.setFont(inputFont);
        checkDiscapacidad.setFont(inputFont);
        checkCronico.setFont(inputFont);

        JLabel etiquetaDolor = new JLabel("Nivel de dolor (1-10):");
        etiquetaDolor.setFont(labelFont);
        Integer[] nivelesDolor = {1,2,3,4,5,6,7,8,9,10};
        comboNivelDolor = new JComboBox<>(nivelesDolor);
        comboNivelDolor.setFont(inputFont);

        // Botones
        botonAgregar = new JButton("Agregar Paciente");
        botonAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel etiquetaAreaLlamada = new JLabel("Área hospitalaria para llamar:");
        etiquetaAreaLlamada.setFont(labelFont);
        comboAreaLlamada = new JComboBox<>(areasHospitalarias);
        comboAreaLlamada.setFont(inputFont);
        
        botonLlamarSiguiente = new JButton("Llamar Siguiente");
        botonLlamarSiguiente.setFont(new Font("Arial", Font.BOLD, 14));

        // Agregar botones nuevos
        botonEliminar = new JButton("Eliminar Paciente");
        botonEliminar.setFont(new Font("Arial", Font.BOLD, 14));
        botonEliminar.addActionListener(e -> eliminarPaciente());

        botonHistorial = new JButton("Ver Historial");
        botonHistorial.setFont(new Font("Arial", Font.BOLD, 14));
        botonHistorial.addActionListener(e -> mostrarHistorial());

        // Añadir componentes con GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panelEntrada.add(etiquetaNombre, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        panelEntrada.add(campoNombre, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelEntrada.add(etiquetaSintomas, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panelEntrada.add(scrollSintomas, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelEntrada.add(etiquetaEnfermedades, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panelEntrada.add(scrollEnfermedades, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelEntrada.add(etiquetaProblemas, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panelEntrada.add(scrollProblemas, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelEntrada.add(etiquetaEdad, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelEntrada.add(campoEdad, gbc);

        // Panel de checkboxes
        gbcCheck.gridx = 0;
        gbcCheck.gridy = 0;
        panelCheckboxes.add(checkEmbarazada, gbcCheck);

        gbcCheck.gridy = 1;
        panelCheckboxes.add(checkDiscapacidad, gbcCheck);

        gbcCheck.gridy = 2;
        panelCheckboxes.add(checkCronico, gbcCheck);

        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panelEntrada.add(panelCheckboxes, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelEntrada.add(etiquetaDolor, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelEntrada.add(comboNivelDolor, gbc);

        // Panel para botones
        JPanel panelBotones = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.insets = new Insets(10, 10, 10, 10);

        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        gbcBtn.gridwidth = 2;
        panelBotones.add(botonAgregar, gbcBtn);

        gbcBtn.gridy = 1;
        gbcBtn.gridx = 0;
        gbcBtn.gridwidth = 1;
        panelBotones.add(etiquetaAreaLlamada, gbcBtn);

        gbcBtn.gridx = 1;
        panelBotones.add(comboAreaLlamada, gbcBtn);

        gbcBtn.gridy = 2;
        gbcBtn.gridx = 0;
        gbcBtn.gridwidth = 2;
        panelBotones.add(botonLlamarSiguiente, gbcBtn);

        gbcBtn.gridy = 3;
        panelBotones.add(botonEliminar, gbcBtn);

        gbcBtn.gridy = 4;
        panelBotones.add(botonHistorial, gbcBtn);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panelEntrada.add(panelBotones, gbc);

        // Panel de salida con pestañas para cada área
        JPanel panelSalida = new JPanel(new GridBagLayout());
        panelSalida.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Listas de Espera por Área"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbcAreas = new GridBagConstraints();
        gbcAreas.insets = new Insets(5, 5, 5, 5);
        gbcAreas.fill = GridBagConstraints.BOTH;
        gbcAreas.weightx = 1.0;
        gbcAreas.weighty = 1.0;

        areasTextoporArea = new HashMap<>();
        int row = 0;
        int col = 0;

        for (String area : areasHospitalarias) {
            // Panel para cada área
            JPanel panelArea = new JPanel(new BorderLayout(5, 5));
            panelArea.setBorder(BorderFactory.createTitledBorder(area));

            // Área de texto para esta área
            JTextArea areaTexto = new JTextArea(10, 35);
            areaTexto.setEditable(false);
            areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollArea = new JScrollPane(areaTexto);
            
            areasTextoporArea.put(area, areaTexto);
            panelArea.add(scrollArea, BorderLayout.CENTER);

            // Añadir el panel del área a la cuadrícula
            gbcAreas.gridx = col;
            gbcAreas.gridy = row;
            panelSalida.add(panelArea, gbcAreas);

            // Alternar entre columnas (2 columnas)
            if (col == 1) {
                col = 0;
                row++;
            } else {
                col++;
            }
        }

        // Añadir paneles al panel contenedor
        panelContenedor.add(panelEntrada, BorderLayout.NORTH);
        panelContenedor.add(panelSalida, BorderLayout.CENTER);

        // Crear ScrollPane para todo el contenido
        JScrollPane scrollPanePrincipal = new JScrollPane(panelContenedor);
        scrollPanePrincipal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPanePrincipal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPanePrincipal.getVerticalScrollBar().setUnitIncrement(16);
        scrollPanePrincipal.setBorder(null);

        // Añadir el ScrollPane al panel principal
        panelPrincipal.add(scrollPanePrincipal, BorderLayout.CENTER);

        // Añadir panel principal a la ventana
        setContentPane(panelPrincipal);
        pack();

        // Establecer tamaño mínimo de la ventana
        setMinimumSize(getSize());

        botonAgregar.addActionListener(e -> agregarPaciente());
        botonLlamarSiguiente.addActionListener(e -> llamarSiguientePaciente());
    }

    // Asignar área hospitalaria basado en sintomas, enfermedades y problemas
    private String asignarAreaHospitalaria(String sintomas, String enfermedades, String problemas) {
        String texto = (sintomas + " " + enfermedades + " " + problemas).toLowerCase();

        // Mapa de palabras clave por área
        Map<String, String[]> keywordsPorArea = new HashMap<>();
        
        // Emergencia
        keywordsPorArea.put("Emergencia", new String[]{
            "dolor pecho", "infarto", "accidente", "traumatismo", "hemorragia", "sangrado abundante",
            "convulsiones", "desmayo", "inconsciente", "paro", "asfixia", "ahogo", "envenenamiento",
            "quemadura grave", "fractura expuesta", "herida profunda", "trauma craneal",
            "dolor intenso", "presión arterial muy alta", "dificultad respiratoria severa"
        });

        // Cardiología
        keywordsPorArea.put("Cardiología", new String[]{
            "corazón", "arritmia", "taquicardia", "presión arterial", "hipertensión",
            "angina", "dolor pecho leve", "palpitaciones", "soplo", "cardio",
            "marcapasos", "colesterol alto", "triglicéridos", "vena", "arteria"
        });

        // Neurología
        keywordsPorArea.put("Neurología", new String[]{
            "cabeza", "migraña", "convulsión", "epilepsia", "mareo", "vértigo",
            "parálisis", "temblor", "alzheimer", "parkinson", "esclerosis",
            "derrame", "pérdida memoria", "confusión", "desmayo", "neuro",
            "hormigueo", "entumecimiento", "debilidad muscular"
        });

        // General
        keywordsPorArea.put("General", new String[]{
            "fiebre", "gripe", "tos", "dolor estómago", "vómito", "diarrea",
            "infección", "malestar", "fatiga", "dolor muscular", "resfriado",
            "alergia", "erupción", "picazón", "dolor garganta", "dolor oído",
            "dolor espalda", "dolor articulaciones"
        });

        // Ambulatorio (casos menos urgentes o seguimiento)
        keywordsPorArea.put("Ambulatorio", new String[]{
            "control", "chequeo", "rutina", "seguimiento", "revisión",
            "vacuna", "resultado", "análisis", "consulta", "cita programada"
        });

        // Sistema de puntuación por área
        Map<String, Integer> puntajeAreas = new HashMap<>();
        for (String area : areasHospitalarias) {
            puntajeAreas.put(area, 0);
            String[] keywords = keywordsPorArea.get(area);
            if (keywords != null) {
                for (String keyword : keywords) {
                    if (texto.contains(keyword)) {
                        // Dar más peso a palabras clave de emergencia
                        int puntos = area.equals("Emergencia") ? 3 : 1;
                        puntajeAreas.put(area, puntajeAreas.get(area) + puntos);
                    }
                }
            }
        }

        // Encontrar el área con mayor puntaje
        String areaAsignada = "General"; // área por defecto
        int maxPuntaje = -1;
        
        for (Map.Entry<String, Integer> entry : puntajeAreas.entrySet()) {
            if (entry.getValue() > maxPuntaje) {
                maxPuntaje = entry.getValue();
                areaAsignada = entry.getKey();
            }
        }

        return areaAsignada;
    }

    private Prioridad determinarPrioridad(String sintomas, String enfermedades, String problemas,
            int edad, boolean esEmbarazada, boolean tieneDiscapacidad, int nivelDolor, boolean esCronico) {
        int puntosPrioridad = 0;
        String texto = (sintomas + " " + enfermedades + " " + problemas).toLowerCase();

        // Palabras clave de alta prioridad con diferentes pesos
        Map<String, Integer> keywordsAlta = new HashMap<>();
        keywordsAlta.put("inconsciente", 50);
        keywordsAlta.put("paro", 50);
        keywordsAlta.put("infarto", 50);
        keywordsAlta.put("derrame cerebral", 50);
        keywordsAlta.put("hemorragia severa", 45);
        keywordsAlta.put("sangrado abundante", 45);
        keywordsAlta.put("convulsiones", 45);
        keywordsAlta.put("dificultad respiratoria severa", 45);
        keywordsAlta.put("trauma craneal", 45);
        keywordsAlta.put("fractura expuesta", 40);
        keywordsAlta.put("quemadura grave", 40);
        keywordsAlta.put("dolor intenso pecho", 40);
        keywordsAlta.put("envenenamiento", 40);
        keywordsAlta.put("asfixia", 40);

        // Palabras clave de prioridad media
        Map<String, Integer> keywordsMedia = new HashMap<>();
        keywordsMedia.put("dolor fuerte", 25);
        keywordsMedia.put("fiebre alta", 25);
        keywordsMedia.put("presión arterial alta", 25);
        keywordsMedia.put("deshidratación", 25);
        keywordsMedia.put("migraña severa", 20);
        keywordsMedia.put("vómitos frecuentes", 20);
        keywordsMedia.put("diarrea severa", 20);
        keywordsMedia.put("infección", 20);
        keywordsMedia.put("fractura", 20);
        keywordsMedia.put("herida", 15);
        keywordsMedia.put("dolor moderado", 15);
        keywordsMedia.put("mareo", 15);

        // Evaluar palabras clave de alta prioridad
        for (Map.Entry<String, Integer> entry : keywordsAlta.entrySet()) {
            if (texto.contains(entry.getKey())) {
                puntosPrioridad += entry.getValue();
            }
        }

        // Evaluar palabras clave de prioridad media
        for (Map.Entry<String, Integer> entry : keywordsMedia.entrySet()) {
            if (texto.contains(entry.getKey())) {
                puntosPrioridad += entry.getValue();
            }
        }

        // Factores adicionales con pesos ajustados
        if (edad < 5) puntosPrioridad += 30;  // Niños muy pequeños
        else if (edad < 12) puntosPrioridad += 20;  // Niños
        else if (edad > 80) puntosPrioridad += 30;  // Adultos mayores avanzados
        else if (edad > 65) puntosPrioridad += 20;  // Adultos mayores

        if (esEmbarazada) puntosPrioridad += 35;  // Embarazadas tienen alta prioridad
        if (tieneDiscapacidad) puntosPrioridad += 25;  // Personas con discapacidad
        
        // Evaluación del nivel de dolor
        if (nivelDolor >= 8) puntosPrioridad += 35;  // Dolor severo
        else if (nivelDolor >= 6) puntosPrioridad += 25;  // Dolor significativo
        else if (nivelDolor >= 4) puntosPrioridad += 15;  // Dolor moderado

        // Condiciones crónicas pueden complicar otros problemas
        if (esCronico) puntosPrioridad += 15;

        // Determinar prioridad basada en puntos
        if (puntosPrioridad >= 50) return Prioridad.ALTA;
        if (puntosPrioridad >= 25) return Prioridad.MEDIA;
        return Prioridad.BAJA;
    }

    private void actualizarAreaSalida(String area) {
        JTextArea areaTexto = areasTextoporArea.get(area);
        if (areaTexto != null) {
            areaTexto.setText("");
            Map<Prioridad, Queue<Paciente>> colasPrioridad = colasAreas.get(area);
            
            for (Prioridad prioridad : Prioridad.values()) {
                Queue<Paciente> cola = colasPrioridad.get(prioridad);
                if (!cola.isEmpty()) {
                    areaTexto.append("=== Prioridad " + prioridad + " ===\n");
                    for (Paciente p : cola) {
                        areaTexto.append(p.toString() + "\n\n");
                    }
                }
            }
        }
    }

    private void actualizarTodasLasAreas() {
        for (String area : areasHospitalarias) {
            actualizarAreaSalida(area);
        }
    }

    private void agregarPaciente() {
        String nombre = campoNombre.getText().trim();
        String sintomas = areaSintomas.getText().trim();
        String enfermedades = areaEnfermedades.getText().trim();
        String problemas = areaProblemas.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre del paciente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (sintomas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, describa los síntomas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener valores de los nuevos campos
        int edad;
        try {
            edad = Integer.parseInt(campoEdad.getText().trim());
            if (edad < 0 || edad > 120) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese una edad válida (0-120).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una edad válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean esEmbarazada = checkEmbarazada.isSelected();
        boolean tieneDiscapacidad = checkDiscapacidad.isSelected();
        boolean esCronico = checkCronico.isSelected();
        int nivelDolor = (Integer) comboNivelDolor.getSelectedItem();

        String area = asignarAreaHospitalaria(sintomas, enfermedades, problemas);
        Prioridad prioridad = determinarPrioridad(sintomas, enfermedades, problemas, 
                                                edad, esEmbarazada, tieneDiscapacidad, 
                                                nivelDolor, esCronico);

        Paciente paciente = new Paciente(nombre, prioridad, sintomas, enfermedades, 
                                    problemas, area, siguienteNumeroTurno++,
                                    edad, esEmbarazada, tieneDiscapacidad, 
                                    nivelDolor, esCronico);
        
        try {
            pacienteDAO.guardarPaciente(paciente);
            colasAreas.get(area).get(prioridad).add(paciente);
            actualizarAreaSalida(area);
            
            // Limpiar campos
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar el paciente en la base de datos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        campoNombre.setText("");
        areaSintomas.setText("");
        areaEnfermedades.setText("");
        areaProblemas.setText("");
        campoEdad.setText("");
        checkEmbarazada.setSelected(false);
        checkDiscapacidad.setSelected(false);
        checkCronico.setSelected(false);
        comboNivelDolor.setSelectedIndex(0);
    }

    private void llamarSiguientePaciente() {
        String areaSeleccionada = (String) comboAreaLlamada.getSelectedItem();
        if (areaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, seleccione un área hospitalaria para llamar al siguiente paciente.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<Prioridad, Queue<Paciente>> colasPrioridad = colasAreas.get(areaSeleccionada);
        Paciente siguiente = null;
        for (Prioridad p : Prioridad.values()) {
            Queue<Paciente> cola = colasPrioridad.get(p);
            if (!cola.isEmpty()) {
                siguiente = cola.poll();
                break;
            }
        }

        if (siguiente != null) {
            try {
                pacienteDAO.marcarComoAtendido(siguiente.numeroTurno);
                JOptionPane.showMessageDialog(this,
                    "Llamando a siguiente paciente:\n" + siguiente.toString(),
                    "Siguiente Paciente", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar el estado del paciente: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "No hay pacientes en espera en el área '" + areaSeleccionada + "'.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
        }
        
        actualizarAreaSalida(areaSeleccionada);
    }

    private void eliminarPaciente() {
        String input = JOptionPane.showInputDialog(this,
            "Ingrese el número de turno del paciente a eliminar:",
            "Eliminar Paciente", JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int numeroTurno = Integer.parseInt(input.trim());
            
            // Buscar y eliminar el paciente de las colas
            boolean encontrado = false;
            for (String area : areasHospitalarias) {
                Map<Prioridad, Queue<Paciente>> colasPrioridad = colasAreas.get(area);
                for (Queue<Paciente> cola : colasPrioridad.values()) {
                    for (Paciente p : cola) {
                        if (p.numeroTurno == numeroTurno) {
                            cola.remove(p);
                            encontrado = true;
                            break;
                        }
                    }
                    if (encontrado) break;
                }
                if (encontrado) {
                    actualizarAreaSalida(area);
                    break;
                }
            }

            if (encontrado) {
                pacienteDAO.eliminarPaciente(numeroTurno);
                JOptionPane.showMessageDialog(this,
                    "Paciente eliminado exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se encontró ningún paciente con el número de turno especificado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Por favor, ingrese un número válido.",
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al eliminar el paciente de la base de datos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarHistorial() {
        if (ventanaHistorial == null) {
            ventanaHistorial = new JFrame("Historial de Pacientes Atendidos");
            ventanaHistorial.setSize(800, 600);
            ventanaHistorial.setLocationRelativeTo(this);
            
            JTextArea areaHistorial = new JTextArea();
            areaHistorial.setEditable(false);
            areaHistorial.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(areaHistorial);
            
            ventanaHistorial.add(scrollPane);
            
            try {
                List<Paciente> historial = pacienteDAO.obtenerHistorialPacientes();
                StringBuilder sb = new StringBuilder();
                for (Paciente p : historial) {
                    sb.append(p.toString()).append("\n\n");
                }
                areaHistorial.setText(sb.toString());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar el historial: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        ventanaHistorial.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaDeTurnos app = new SistemaDeTurnos();
            app.setVisible(true);
        });
    }
}

