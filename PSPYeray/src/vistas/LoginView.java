package vistas;

import logica.Cliente;
import logica.LoginRequest;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginView extends JFrame {

    public LoginView() throws Exception {
        // Configuración de la ventana principal
        setTitle("Inicio de Sesión");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Etiqueta de bienvenida
        JLabel titleLabel = new JLabel("Bienvenid@");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campo de usuario
        JLabel usuarioLabel = new JLabel("Usuario:");
        JTextField usuarioField = new JTextField(15);

        // Campo de contraseña
        JLabel passLabel = new JLabel("Contraseña:");
        JPasswordField passField = new JPasswordField(15);

        // Botón de iniciar sesión
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Etiqueta para redirigir a registro
        JLabel registerLabel = new JLabel("¿No tienes cuenta? Regístrate aquí");
        registerLabel.setForeground(Color.BLUE.darker());
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Acción para el botón de registro
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    new RegisterView().setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al abrir la vista de registro: " + ex.getMessage());
                }
                dispose(); // Cierra la ventana actual
            }
        });

        // Panel de usuario y contraseña
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(usuarioLabel);
        inputPanel.add(usuarioField);
        inputPanel.add(passLabel);
        inputPanel.add(passField);

        // Añadir componentes al panel principal
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20)); // Espaciado
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(15)); // Espaciado
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(15)); // Espaciado
        mainPanel.add(registerLabel);

        // Añadir panel principal a la ventana
        add(mainPanel);

        // Instanciar cliente
        Cliente cliente = new Cliente("localhost", 12345);

        // Acción del botón de iniciar sesión
        loginButton.addActionListener(e -> {
            try {
                String usuario = usuarioField.getText();
                String password = new String(passField.getPassword());

                // Validaciones de los campos
                if (usuario.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
                    return;
                }

                // Conectar al servidor y enviar el objeto
                cliente.conectar();

                String respuesta = cliente.enviarLogin(usuario, password);

                if (respuesta.equals("LOGIN_OK")) {
                    JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso.");
                    new IncidenciasView(usuario, cliente).setVisible(true);
                    dispose(); // Cierra la ventana actual
                } else {
                    JOptionPane.showMessageDialog(this, respuesta);
                }

                cliente.cerrarConexion();

            } catch (IOException ex) {
                //JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginView().setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
