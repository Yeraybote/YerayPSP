package logica;

import logica.firma.Verificador;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

class ManejadorCliente implements Runnable {
    private final Socket socket;
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static int contadorIncidencias = 1;

    // Bloque estático para inicializar usuarios base
    static {
        usuarios.add(new Usuario("Juan", "Perez", 25, "juan@example.com", "juan", "Juan123!"));
        usuarios.add(new Usuario("Ana", "Garcia", 30, "ana@example.com", "ana", "Ana123!"));
        usuarios.add(new Usuario("Luis", "Martinez", 22, "luis@example.com", "luis", "Luis123!"));
    }

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {

        try (
                BufferedReader textoEntrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                ObjectInputStream entradaObjetos = new ObjectInputStream(socket.getInputStream())
        ) {
            // Leer el comando inicial del cliente
            String comando = textoEntrada.readLine();

            if (comando.equals("LOGIN")) {
                // Procesar login
                String usuario = textoEntrada.readLine();
                String password = textoEntrada.readLine();

                if (validarLogin(usuario, password)) {
                    salida.println("LOGIN_OK");
                } else {
                    salida.println("Usuario o contraseña incorrectos");
                }

            } else if (comando.equals("REGISTER")) {
                // Procesar registro
                String name = textoEntrada.readLine();
                String lastName = textoEntrada.readLine();
                int age = Integer.parseInt(textoEntrada.readLine());
                String email = textoEntrada.readLine();
                String user = textoEntrada.readLine();
                String password = textoEntrada.readLine();

                if (registrarUsuario(name, lastName, age, email, user, password)) {
                    salida.println("REGISTRO_OK");
                } else {
                    salida.println("El usuario ya existe o los datos no son válidos");
                }

            } else {
                // Procesar incidencia y firma
                try {
                    Incidencia incidencia = (Incidencia) entradaObjetos.readObject(); // Leer incidencia
                    String firma = (String) entradaObjetos.readObject();             // Leer firma digital

                    if (verificarFirmaIncidencia(incidencia, firma, incidencia.getCliente())) {
                        // Generar código único y clasificar la incidencia
                        String codigo = generarCodigoUnico();
                        String clasificacion = clasificarIncidencia(incidencia.getDescripcion());

                        // Registrar o imprimir incidencia (según tus necesidades)
                        System.out.println("Incidencia registrada:");
                        System.out.println("Código: " + codigo);
                        System.out.println("Clasificación: " + clasificacion);
                        System.out.println("Descripción: " + incidencia.getDescripcion());

                        // Enviar respuesta al cliente
                        salida.println("INCIDENCIA_OK;" + codigo + ";" + clasificacion);
                    } else {
                        // Firma inválida, notificar al cliente
                        System.out.println("Firma inválida. Incidencia rechazada.");
                        salida.println("FIRMA_INVALIDA");
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Error al procesar incidencia: " + e.getMessage());
                    salida.println("ERROR_PROCESANDO_INCIDENCIA");
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
    }

    private String clasificarIncidencia(String descripcion) {
        String descripcionLower = descripcion.toLowerCase();

        if (descripcionLower.contains("urgente") || descripcionLower.contains("crítico") || descripcionLower.contains("falla grave")) {
            return "Urgente";
        } else if (descripcionLower.contains("problema") || descripcionLower.contains("error") || descripcionLower.contains("fallo")) {
            return "Moderada";
        } else if (descripcionLower.contains("consulta") || descripcionLower.contains("revisión") || descripcionLower.contains("sugerencia")) {
            return "Leve";
        }

        return "Sin clasificar"; // Por defecto, si no se encuentra ninguna palabra clave
    }

    private String generarCodigoUnico() {
        String codigo = "INC-" + contadorIncidencias;
        contadorIncidencias++;
        return codigo;
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

    private boolean verificarFirmaIncidencia(Incidencia incidencia, String firma, Cliente cliente) {
        try {
            // Obtener clave pública del empleado
            PublicKey clavePublica = cliente.getClavePublica();

            // Serializar la incidencia para verificar la firma
            String incidenciaSerializada = incidencia.toString();

            // Verificar la firma
            return Verificador.verificarFirma(incidenciaSerializada, firma, clavePublica);
        } catch (Exception e) {
            System.err.println("Error al verificar firma de incidencia: " + e.getMessage());
            return false;
        }
    }
}
