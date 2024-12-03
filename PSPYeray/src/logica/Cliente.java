package logica;

import logica.firma.GeneradorClaves;

import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Cliente {
    private final String host;
    private final int puerto;
    private Socket socket;
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
        System.out.println("Conectado al servidor");
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

    public static void enviarIncidencia(Incidencia incidencia, String firma, String servidor, int puerto) throws Exception {
        try (Socket socket = new Socket(servidor, puerto);
             ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream())) {
            salida.writeObject(incidencia); // Enviar la incidencia
            salida.writeObject(firma); // Enviar la firma digital
        }
    }

}
