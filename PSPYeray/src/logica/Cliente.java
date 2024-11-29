package logica;

import java.io.*;
import java.net.*;

public class Cliente {
    private final String host;
    private final int puerto;
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;

    public Cliente(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
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
}
