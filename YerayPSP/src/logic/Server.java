package logic;

import model.User;

import java.util.List;
import java.util.ArrayList;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
    private static final int PORT = 6969;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Servidor escuchando...");
            List<User> users = new ArrayList<>();

            // Añadimos usuarios base
            User uyeray = new User("yeray","bote", 19,"yeray@gmail.es","yeraybote");
            users.add(uyeray);
            User uimanol = new User("imanol","anda", 20,"imanol@gmail.com","imanolanda");
            users.add(uimanol);

            while (true) {
                Socket clientCall = server.accept();
                ClientHandler clientThread = new ClientHandler(clientCall, users);
                clientThread.start();
            }

        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}