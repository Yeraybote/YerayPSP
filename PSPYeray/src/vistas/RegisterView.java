package vistas;

import logica.Cliente;
import logica.RegisterRequest;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RegisterView extends JFrame {

    public RegisterView() throws Exception {
        // Configuración de la ventana principal
        setTitle("Registro de Usuario");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Etiqueta de bienvenida
        JLabel titleLabel = new JLabel("Registro de Usuario");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos de entrada
        JLabel nameLabel = new JLabel("Nombre:");
        JTextField nameField = new JTextField(15);

        JLabel lastNameLabel = new JLabel("Apellido:");
        JTextField lastNameField = new JTextField(15);

        JLabel ageLabel = new JLabel("Edad:");
        JTextField ageField = new JTextField(15);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(15);

        JLabel userLabel = new JLabel("Usuario:");
        JTextField userField = new JTextField(15);

        JLabel passLabel = new JLabel("Contraseña:");
        JPasswordField passField = new JPasswordField(15);

        // Botón de registro
        JButton registerButton = new JButton("Registrar");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Etiqueta para redirigir a inicio de sesión
        JLabel loginLabel = new JLabel("¿Ya tienes cuenta? Inicia sesión aquí");
        loginLabel.setForeground(Color.BLUE.darker());
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Acción para redirigir a la vista de inicio de sesión
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    new LoginView().setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al abrir la vista de inicio de sesión: " + ex.getMessage());
                }
                dispose(); // Cierra la ventana actual
            }
        });

        // Panel de entrada de datos
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(lastNameLabel);
        inputPanel.add(lastNameField);
        inputPanel.add(ageLabel);
        inputPanel.add(ageField);
        inputPanel.add(emailLabel);
        inputPanel.add(emailField);
        inputPanel.add(userLabel);
        inputPanel.add(userField);
        inputPanel.add(passLabel);
        inputPanel.add(passField);

        // Añadir componentes al panel principal
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20)); // Espaciado
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(15)); // Espaciado
        mainPanel.add(registerButton);
        mainPanel.add(Box.createVerticalStrut(15)); // Espaciado
        mainPanel.add(loginLabel);

        // Añadir panel principal a la ventana
        add(mainPanel);

        // Instanciar cliente
        Cliente cliente = new Cliente("localhost", 12345);

        // Acción para el botón de registro
        registerButton.addActionListener(e -> {
            try {
                // Recoger los valores de los campos
                String nombre = nameField.getText().trim();
                String apellido = lastNameField.getText().trim();
                String edadStr = ageField.getText().trim();
                String email = emailField.getText().trim();
                String usuario = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();

                // Validaciones
                if (nombre.isEmpty() || apellido.isEmpty() || edadStr.isEmpty() || email.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                    return;
                }

                if (!edadStr.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "La edad debe ser un número válido.");
                    return;
                }

                int edad = Integer.parseInt(edadStr);
                if (edad < 18 || edad > 100) {
                    JOptionPane.showMessageDialog(this, "La edad debe estar entre 18 y 100 años.");
                    return;
                }

                // Crear objeto RegisterRequest
                RegisterRequest registro = new RegisterRequest(nombre, apellido, edad, email, usuario, password);

                // Conexión con el servidor
                cliente.conectar();
                String respuesta = cliente.enviarRegistro(registro); // Enviar el objeto de registro al servidor

                String respuestaStr = (String) respuesta;
                JOptionPane.showMessageDialog(this, respuestaStr);
                if (respuestaStr.equals("REGISTRO_OK")) {
                    new LoginView().setVisible(true); // Redirigir al login
                    dispose();
                }

                cliente.cerrarConexion();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new RegisterView().setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
