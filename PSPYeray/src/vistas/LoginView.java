package vistas;

import logica.Cliente;
import logica.UtilidadCifrado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginView extends JFrame {

    public LoginView() {
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
        JTextField usuadioField = new JTextField(15);

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
                // Lógica para abrir la vista de registro
                new RegisterView().setVisible(true);
                dispose(); // Cierra la ventana actual
            }
        });

        // Panel de DNI y contraseña
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(usuarioLabel);
        inputPanel.add(usuadioField);
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

        // Parte del código en LoginView
        Cliente cliente = new Cliente("localhost", 12345);

        loginButton.addActionListener(e -> {
            try {
                String usuario = usuadioField.getText();
                String password = new String(passField.getPassword());

                // Validaciones de si los campos están vacíos, ya que las validaciones exausitvas se hacen a la hora de registrar
                if (usuario.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El campo de usuario no puede estar vacío.");
                    return;
                }

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El campo de contraseña no puede estar vacío.");
                    return;
                }

                // Cifrar la contraseña
                cliente.conectar();
                String passwordCifrada = UtilidadCifrado.cifrar(password);

                // Enviar datos al servidor
                cliente.enviarMensaje("LOGIN;" + usuario + ";" + passwordCifrada);

                // Respuesta del servidor
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
            new LoginView().setVisible(true);
        });
    }
}