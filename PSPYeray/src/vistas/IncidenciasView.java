package vistas;

import logica.Cliente;
import logica.Incidencia;
import logica.firma.Firmador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IncidenciasView extends JFrame {
    private JTextField descripcionField;
    private JTextField lugarFisicoField;
    private JTextField empleadoField;
    private String empleado;
    private Cliente cliente;

    public IncidenciasView(String empleado, Cliente cliente) {
        this.empleado = empleado;
        this.cliente = cliente;

        // Configuración de la ventana principal
        setTitle("Crear Incidencia");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campo de descripción
        JLabel descripcionLabel = new JLabel("Descripción:");
        descripcionField = new JTextField(15);

        // Campo de lugar físico
        JLabel lugarFisicoLabel = new JLabel("Lugar Físico:");
        lugarFisicoField = new JTextField(15);

        // Campo de empleado
        JLabel empleadoLabel = new JLabel("Empleado:");
        empleadoField = new JTextField(15);
        empleadoField.setText(empleado);
        empleadoField.setEditable(false);

        // Botón de enviar incidencia
        JButton enviarButton = new JButton("Enviar Incidencia");
        enviarButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel de entrada de datos
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(descripcionLabel);
        inputPanel.add(descripcionField);
        inputPanel.add(lugarFisicoLabel);
        inputPanel.add(lugarFisicoField);
        inputPanel.add(empleadoLabel);
        inputPanel.add(empleadoField);

        // Añadir componentes al panel principal
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(15)); // Espaciado
        mainPanel.add(enviarButton);

        // Añadir panel principal a la ventana
        add(mainPanel);

        // Acción para el botón de enviar incidencia
        enviarButton.addActionListener(e -> {
            String descripcion = descripcionField.getText();
            String lugarFisico = lugarFisicoField.getText();

            if (descripcion.isEmpty() || lugarFisico.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
            } else {

                if (descripcion.length() < 10 || descripcion.length() > 200) {
                    JOptionPane.showMessageDialog(null, "La descripción debe tener entre 10 y 200 caracteres.");
                    return;
                }

                if (!lugarFisico.matches("^[a-zA-Z0-9 ]+$")) {
                    JOptionPane.showMessageDialog(null, "El lugar físico solo puede contener letras, números y espacios.");
                    return;
                }

                try {
                    // Crear incidencia
                    Incidencia incidencia = new Incidencia(descripcion, lugarFisico, empleado);
                    // Serializar y firmar la incidencia
                    String incidenciaSerializada = incidencia.toJson();  // Usando un método para convertir a JSON
                    String firma = Firmador.firmarMensaje(incidenciaSerializada, cliente.getClavePrivada());

                    // Establecer la firma en la incidencia
                    incidencia.setFirma(firma);

                    // Enviar incidencia al servidor
                    System.out.println("Enviando incidencia al servidor...");
                    System.out.println(incidencia);
                    String respuesta = cliente.enviarIncidencia(incidencia);  // Enviar solo los datos necesarios
                    System.out.println("Respuesta del servidor: " + respuesta);

                    // Recibir respuesta del servidor
                    String[] partesRespuesta = respuesta.split(";");

                    if (partesRespuesta[0].equals("INCIDENCIA_OK")) {
                        String codigo = partesRespuesta[1];
                        String clasificacion = partesRespuesta[2];
                        int horas = Integer.parseInt(partesRespuesta[3]);
                        JOptionPane.showMessageDialog(null, "Incidencia registrada:\nCódigo: " + codigo + "\nClasificación: " + clasificacion + "\nHoras estimadas: " + horas);

                        // Limpiar campos
                        descripcionField.setText("");
                        lugarFisicoField.setText("");

                    } else {
                        JOptionPane.showMessageDialog(null, "Error: " + partesRespuesta[0]);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al enviar la incidencia: " + ex.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        Cliente cliente = new Cliente("localhost", 12345);
        SwingUtilities.invokeLater(() -> {
            new IncidenciasView("empleadoEjemplo", cliente).setVisible(true);
        });
    }
}
