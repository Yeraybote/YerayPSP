package vistas;

import logica.Cliente;
import logica.UtilidadCifrado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
                // Lógica para abrir la vista de inicio de sesión
                try {
                    new LoginView().setVisible(true);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
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

        // Parte del código en RegisterView
        Cliente cliente = new Cliente("localhost", 12345);

        registerButton.addActionListener(e -> {
            try {
                // Recoger los valores de los campos
                String nombre = nameField.getText().trim();
                String apellido = lastNameField.getText().trim();
                String edad = ageField.getText().trim();
                String email = emailField.getText().trim();
                String usuario = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();

                // Validaciones
                if (nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || email.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                    return;
                }

                if (!nombre.matches("^[a-zA-Z ]+$")) {
                    JOptionPane.showMessageDialog(this, "El nombre solo puede contener letras y espacios.");
                    return;
                }

                if (!apellido.matches("^[a-zA-Z ]+$")) {
                    JOptionPane.showMessageDialog(this, "El apellido solo puede contener letras y espacios.");
                    return;
                }

                if (!edad.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "La edad debe ser un número válido.");
                    return;
                }

                int edadInt = Integer.parseInt(edad);
                if (edadInt < 18 || edadInt > 100) {
                    JOptionPane.showMessageDialog(this, "La edad debe estar entre 18 y 100 años.");
                    return;
                }

                if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    JOptionPane.showMessageDialog(this, "El email no tiene un formato válido.");
                    return;
                }

                if (usuario.length() < 4 || usuario.length() > 15) {
                    JOptionPane.showMessageDialog(this, "El usuario debe tener entre 4 y 15 caracteres.");
                    return;
                }

                if (!usuario.matches("^[a-zA-Z0-9_]+$")) {
                    JOptionPane.showMessageDialog(this, "El usuario solo puede contener letras, números y guiones bajos.");
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres.");
                    return;
                }

                if (!password.matches(".*[A-Z].*")) {
                    JOptionPane.showMessageDialog(this, "La contraseña debe contener al menos una letra mayúscula.");
                    return;
                }

                if (!password.matches(".*[a-z].*")) {
                    JOptionPane.showMessageDialog(this, "La contraseña debe contener al menos una letra minúscula.");
                    return;
                }

                if (!password.matches(".*\\d.*")) {
                    JOptionPane.showMessageDialog(this, "La contraseña debe contener al menos un número.");
                    return;
                }

                if (!password.matches(".*[@#$%^&+=!].*")) {
                    JOptionPane.showMessageDialog(this, "La contraseña debe contener al menos un carácter especial (@#$%^&+=!).");
                    return;
                }


                // Conexión con el servidor y envío de datos
                cliente.conectar();
                String passwordCifrada = UtilidadCifrado.cifrar(password); // Cifrar la contraseña

                cliente.enviarMensaje("REGISTER;" + nombre + ";" + apellido + ";" + edad + ";" + email + ";" + usuario + ";" + passwordCifrada);

                String respuesta = cliente.recibirRespuesta();
                JOptionPane.showMessageDialog(this, respuesta);

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
