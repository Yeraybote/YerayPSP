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
    private final RepositorioClavesPublicas repositorioClaves;

    // Bloque estático para inicializar usuarios base
    static {
        usuarios.add(new Usuario("Yeray", "Bote", 21, "yeray@gmail.com", "yeray", "Yeray123!"));
        usuarios.add(new Usuario("Ana", "Garcia", 30, "ana@example.com", "ana", "Ana123!"));
        usuarios.add(new Usuario("Luis", "Martinez", 22, "luis@example.com", "luis", "Luis123!"));
    }

    public ManejadorCliente(Socket socket, RepositorioClavesPublicas repositorioClaves) {
        this.socket = socket;
        this.repositorioClaves = repositorioClaves;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())
        ) {
            //while (!socket.isClosed()) {
                try {
                    // Leer un objeto del cliente
                    Object objeto = entrada.readObject();

                    if (objeto instanceof LoginRequest) {
                        // Procesar Login
                        LoginRequest login = (LoginRequest) objeto;
                        System.out.println("Usuario: " + login.getUsuario() + ", Contraseña: " + login.getPassword());

                        if (validarLogin(login.getUsuario(), login.getPassword())) {
                            salida.writeObject("LOGIN_OK");
                        } else {
                            salida.writeObject("Usuario o contraseña incorrectos");
                        }

                    } else if (objeto instanceof RegisterRequest) {
                        // Procesar Registro
                        RegisterRequest registro = (RegisterRequest) objeto;

                        if (registrarUsuario(registro)) {
                            salida.writeObject("REGISTRO_OK");
                        } else {
                            salida.writeObject("El usuario ya existe o los datos no son válidos");
                        }

                    } else if (objeto instanceof Incidencia) {
                        // Procesar Incidencia
                        Incidencia incidencia = (Incidencia) objeto;

                        // Obtener la clave pública del empleado desde el repositorio
                        PublicKey clavePublica = repositorioClaves.obtenerClavePublica(incidencia.getEmpleado());
                        if (clavePublica == null) {
                            salida.writeObject("CLAVE_PUBLICA_NO_ENCONTRADA");
                            return;
                        }

                        System.out.println("Incidencia recibida: " + incidencia);

                        if (verificarFirmaIncidencia(incidencia, incidencia.getFirma(), clavePublica)) {
                            String codigo = generarCodigoUnico();
                            String clasificacion = clasificarIncidencia(incidencia.getDescripcion());
                            int horas = calcularHorasIncidencia(clasificacion);

                            salida.writeObject("INCIDENCIA_OK;" + codigo + ";" + clasificacion + ";" + horas);
                        } else {
                            salida.writeObject("FIRMA_INVALIDA");
                            /* String codigo = generarCodigoUnico();
                            String clasificacion = clasificarIncidencia(incidencia.getDescripcion());
                            int horas = calcularHorasIncidencia(clasificacion);

                            salida.writeObject("INCIDENCIA_OK;" + codigo + ";" + clasificacion + ";" + horas);*/
                        }
                    } else {
                        salida.writeObject("COMANDO_DESCONOCIDO");
                    }

                } catch (ClassNotFoundException e) {
                    System.err.println("Error al procesar objeto: " + e.getMessage());
                    salida.writeObject("ERROR_PROCESANDO_OBJETO");
                }
            //}

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

    private int calcularHorasIncidencia(String clasificacion) {
        switch (clasificacion) {
            case "Urgente":
                return 16;
            case "Moderada":
                return 8;
            case "Leve":
                return 4;
            default:
                return 2;
        }
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
    private boolean registrarUsuario(RegisterRequest registro) {
        // Verificar si el usuario ya existe
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(registro.getUsuario())) {
                return false; // El usuario ya existe
            }
        }
        // Agregar el nuevo usuario
        usuarios.add(new Usuario(registro.getNombre(), registro.getApellido(), registro.getEdad(), registro.getEmail(), registro.getUsuario(), registro.getPassword()));
        return true;
    }

    private boolean verificarFirmaIncidencia(Incidencia incidencia, String firma, PublicKey clavePublica) {
        try {
            // Serializar la incidencia para verificar la firma
            String incidenciaSerializada = incidencia.toJson();

            // Verificar la firma
            return Verificador.verificarFirma(incidenciaSerializada, firma, clavePublica);
        } catch (Exception e) {
            System.err.println("Error al verificar firma de incidencia: " + e.getMessage());
            return false;
        }
    }
}
