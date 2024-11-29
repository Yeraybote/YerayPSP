package logica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ManejadorCliente implements Runnable {
    private final Socket socket;
    private static final List<Usuario> usuarios = new ArrayList<>();

    // Bloque estático para inicializar usuarios base
    static {
        usuarios.add(new Usuario("Juan", "Perez", 25, "juan@example.com", "juan123", "password123"));
        usuarios.add(new Usuario("Ana", "Garcia", 30, "ana@example.com", "ana123", "password456"));
        usuarios.add(new Usuario("Luis", "Martinez", 22, "luis@example.com", "luis123", "password789"));
    }

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Mensaje recibido: " + mensaje);
                String[] partes = mensaje.split(";");

                if (partes[0].equals("LOGIN")) {
                    String usuario = partes[1];
                    String password = partes[2];
                    if (validarLogin(usuario, password)) {
                        salida.println("Inicio de sesión exitoso para " + usuario);
                    } else {
                        salida.println("Usuario o contraseña incorrectos");
                    }

                } else if (partes[0].equals("REGISTER")) {
                    String name = partes[1];
                    String lastName = partes[2];
                    int age = Integer.parseInt(partes[3]);
                    String email = partes[4];
                    String user = partes[5];
                    String password = partes[6];

                    if (registrarUsuario(name, lastName, age, email, user, password)) {
                        salida.println("Registro exitoso para " + user);
                    } else {
                        salida.println("El usuario ya existe o los datos no son válidos");
                    }
                } else {
                    salida.println("Comando desconocido");
                }
            }

        } catch (IOException e) {
            System.err.println("Error al manejar cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket: " + e.getMessage());
            }
        }
    }

    // Metodo para validar el login
    private boolean validarLogin(String usuario, String passwordCifrada) {
        try {
            String passwordDescifrada = UtilidadCifrado.descifrar(passwordCifrada);
            for (Usuario u : usuarios) {
                if (u.getUsuario().equals(usuario) && UtilidadCifrado.descifrar(u.getPassword()).equals(passwordDescifrada)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al descifrar contraseña: " + e.getMessage());
        }
        return false;
    }


    // Metodo para registrar un nuevo usuario
    private boolean registrarUsuario(String name, String lastName, int age, String email, String user, String password) {
        // Verificar si el usuario ya existe
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(user)) {
                return false; // El usuario ya existe
            }
        }
        // Agregar el nuevo usuario
        usuarios.add(new Usuario(name, lastName, age, email, user, password));
        return true;
    }
}
