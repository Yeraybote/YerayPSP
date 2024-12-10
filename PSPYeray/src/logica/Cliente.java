package logica;

import logica.firma.Firmador;
import logica.firma.GeneradorClaves;

import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Cliente {
    private final String host;
    private final int puerto;
    private static Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private KeyPair claves; // Claves del cliente

    public Cliente(String host, int puerto) throws Exception {
        this.host = host;
        this.puerto = puerto;
        // Generar par de claves al iniciar el cliente
        this.claves = GeneradorClaves.generarParDeClaves();
    }

    public PrivateKey getClavePrivada() {
        return claves.getPrivate();
    }

    public PublicKey getClavePublica() {
        return claves.getPublic();
    }

    public void conectar() throws IOException {
        socket = new Socket(host, puerto);
        salida = new PrintWriter(socket.getOutputStream(), true);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void enviarMensaje(String mensaje) {
        salida.println(mensaje);
    }

    public String recibirRespuesta() throws IOException {
        return entrada.readLine();
    }

    public void cerrarConexion() throws IOException {
        if (socket != null) socket.close();
    }

    public String enviarIncidencia(Incidencia incidencia) throws Exception {
        String respuesta = "";
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

            // Enviar la incidencia y la firma al servidor
            salida.writeObject(incidencia);

            // Recibir respuesta del servidor
            respuesta = (String) entrada.readObject();
            //System.out.println("Respuesta del servidor: " + respuesta);
        }
        return respuesta;
    }

    public String enviarRegistro(RegisterRequest registro) throws IOException {
        String respuesta = "";
        try (ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

            // Crear el objeto RegisterRequest y enviarlo
            salida.writeObject(registro);

            // Leer la respuesta del servidor
            respuesta = (String) entrada.readObject();
            System.out.println("Respuesta del servidor: " + respuesta);
        } catch (ClassNotFoundException e) {
            System.err.println("Error al recibir respuesta: " + e.getMessage());
        }
        return respuesta;
    }

    public String enviarLogin(String usuario, String password) throws IOException {
        String respuesta = "";
        try (ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

            // Crear el objeto LoginRequest y enviarlo
            password = UtilidadCifrado.cifrar(password);
            LoginRequest loginRequest = new LoginRequest(usuario, password);
            salida.writeObject(loginRequest);

            // Leer la respuesta del servidor
            respuesta = (String) entrada.readObject();
            System.out.println("Respuesta del servidor: " + respuesta);
            return respuesta;
        } catch (ClassNotFoundException e) {
            System.err.println("Error al recibir respuesta: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return respuesta;
    }

}
