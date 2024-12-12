package logic;

import security.Cipher;
import model.Incidence;
import model.User;
import security.Descipher;
import security.Validator;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final List<Incidence> incidentList = new ArrayList<>();
    private final List<User> userList;

    public ClientHandler(Socket clientSocket, List<User> userList) {
        this.clientSocket = clientSocket;
        this.userList = userList;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            SecretKey symmetricKey = new SecretKeySpec("1234567890123456".getBytes(), "AES");
            outputStream.writeObject(symmetricKey);

            boolean isActive = true;
            while (isActive) {
                String receivedOption = decryptIncomingMessage(inputStream, symmetricKey);
                handleClientOption(receivedOption, inputStream, outputStream, symmetricKey);
                if ("SALIR".equals(receivedOption)) {
                    isActive = false;
                }
            }
        } catch (EOFException e) {
            System.out.println("Connection closed for: " + clientSocket);
        } catch (SocketException e) {
            System.out.println("Data stream unexpectedly closed. Client may have disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Error closing socket: " + ex.getMessage());
            }
        }
    }

    /**
     * Este método gestiona la opción recibida del cliente.
     * Recibe la opción, el flujo de entrada, el flujo de salida y la clave simétrica.
     * Procesa la opción y llama al método correspondiente.
     */
    private void handleClientOption(String option, ObjectInputStream inputStream, ObjectOutputStream outputStream, SecretKey symmetricKey) throws Exception {
        switch (option) {
            case "LOGIN":
                loginUser(inputStream, outputStream, symmetricKey);
                break;
            case "REGISTER":
                createUser(inputStream, outputStream, symmetricKey);
                break;
            case "END":
                System.out.println("Cliente desconectado.");
                break;
            default:
                outputStream.writeUTF("Opción invalida.");
                outputStream.flush();
        }
    }

    /**
     * Este método crea un nuevo usuario y lo añade a la lista de usuarios.
     * Recibe los datos del usuario cifrados, los descifra y los añade a la lista.
     * Si el usuario ya existe, responde con un mensaje de error.
     */
    private void createUser(ObjectInputStream inputStream, ObjectOutputStream outputStream, SecretKey symmetricKey) throws Exception {
        User newUser = decryptUser(inputStream, symmetricKey);
        Validator validator = new Validator();

        if (!validator.existeUsuario(newUser.getNombre(), userList)) {
            userList.add(newUser);
            System.out.println("User created successfully.");
            outputStream.writeUTF("User created successfully.");
        } else {
            outputStream.writeUTF("User already exists.");
        }
        outputStream.flush();
    }

    /**
     * Este método verifica las credenciales del usuario y gestiona el inicio de sesión si son válidas.
     * Recibe las credenciales cifradas del cliente, las descifra y las compara con la lista de usuarios.
     * Si las credenciales coinciden, inicia la sesión del usuario; en caso contrario, responde con un error.
     */
    private void loginUser(ObjectInputStream inputStream, ObjectOutputStream outputStream, SecretKey symmetricKey) throws Exception {
        String username = decryptIncomingMessage(inputStream, symmetricKey);
        String password = decryptIncomingMessage(inputStream, symmetricKey);

        User user = new Validator().validarUsuario(username, password, userList);
        if (user != null) {
            outputStream.writeUTF("VALIDO");
            outputStream.flush();
            System.out.println("User logged in: " + user.getNombre());
            handleUserSession(inputStream, outputStream, symmetricKey);
        } else {
            outputStream.writeUTF("INVALID USER");
            outputStream.flush();
        }
    }

    /**
     * Este método gestiona la sesión del usuario una vez que ha iniciado sesión correctamente.
     * Recibe las incidencias del cliente, las descifra y las procesa.
     * Si el cliente solicita salir, finaliza la sesión.
     */
    private void handleUserSession(ObjectInputStream inputStream, ObjectOutputStream outputStream, SecretKey symmetricKey) throws Exception {
        boolean continueSession = true;
        while (continueSession) {
            String option = decryptIncomingMessage(inputStream, symmetricKey);
            if ("INCIDENCIA".equals(option)) {
                processIncident(inputStream, outputStream, symmetricKey);
            } else if ("SALIR".equals(option)) {
                continueSession = false;
            } else {
                System.out.println("Invalid option received.");
            }
        }
    }

    /**
     * Este método procesa la incidencia recibida del cliente.
     * Recibe la clave pública del cliente, la incidencia cifrada y la firma digital.
     * Descifra la incidencia y la clave pública, verifica la firma digital y la categoriza.
     * Responde al cliente con la categoría y el tiempo estimado de resolución.
     */
    private void processIncident(ObjectInputStream inputStream, ObjectOutputStream outputStream, SecretKey symmetricKey) throws Exception {
        PublicKey clientPublicKey = decryptPublicKey(inputStream, symmetricKey);
        Incidence incident = decryptIncident(inputStream, symmetricKey);
        byte[] digitalSignature = decryptIncomingBytes(inputStream, symmetricKey);

        boolean isValid = verifySignature(incident, clientPublicKey, digitalSignature);
        if (isValid) {
            incident.setLevel(incident.getDescription());
            incident.setHours();
            incidentList.add(incident);

            String response = "Incident created. Categorized as: " + incident.getLevel() + " Estimated resolution time: " + incident.getHours();
            sendEncryptedMessage(response, outputStream, symmetricKey);
        } else {
            sendEncryptedMessage("INCIDENT NOT VERIFIED", outputStream, symmetricKey);
        }
    }

    /**
     * Este método descifra un mensaje recibido del cliente.
     * Recibe la longitud del mensaje cifrado y el mensaje cifrado.
     * Descifra el mensaje y lo devuelve como una cadena de texto.
     */
    private String decryptIncomingMessage(ObjectInputStream inputStream, SecretKey symmetricKey) throws Exception {
        int length = inputStream.readInt();
        byte[] encryptedData = new byte[length];
        inputStream.readFully(encryptedData);
        return Descipher.stringDescipher(encryptedData, symmetricKey);
    }

    /**
     * Este método descifra un array de bytes recibido del cliente.
     * Recibe la longitud del array cifrado y el array cifrado.
     * Descifra el array y lo devuelve como un array de bytes.
     */
    private byte[] decryptIncomingBytes(ObjectInputStream inputStream, SecretKey symmetricKey) throws Exception {
        int length = inputStream.readInt();
        byte[] encryptedData = new byte[length];
        inputStream.readFully(encryptedData);
        return Descipher.dataDescipher(encryptedData, symmetricKey);
    }

    /**
     * Este método descifra un usuario recibido del cliente.
     * Recibe los datos del usuario cifrados y los descifra.
     * Convierte los datos descifrados en un objeto de tipo User y lo devuelve.
     */
    private User decryptUser(ObjectInputStream inputStream, SecretKey symmetricKey) throws Exception {
        byte[] userData = decryptIncomingBytes(inputStream, symmetricKey);
        return User.convertirBytesAUsuario(userData);
    }

    /**
     * Este método descifra la clave pública recibida del cliente.
     * Recibe la clave pública cifrada y la descifra.
     * Convierte la clave pública descifrada en un objeto de tipo PublicKey y la devuelve.
     */
    private PublicKey decryptPublicKey(ObjectInputStream inputStream, SecretKey symmetricKey) throws Exception {
        byte[] publicKeyData = decryptIncomingBytes(inputStream, symmetricKey);
        return Validator.reconstruirClavePublica(publicKeyData);
    }


    /**
     * Este método descifra la incidencia recibida del cliente.
     * Recibe la incidencia cifrada y la descifra.
     * Convierte la incidencia descifrada en un objeto de tipo Incidence y la devuelve.
     */
    private Incidence decryptIncident(ObjectInputStream inputStream, SecretKey symmetricKey) throws Exception {
        byte[] incidentData = decryptIncomingBytes(inputStream, symmetricKey);
        return Incidence.convertirBytesAIncidencia(incidentData);
    }

    /**
     * Este método verifica la firma digital de una incidencia.
     * Recibe la incidencia, la clave pública y la firma digital.
     * Verifica la firma digital y devuelve un valor booleano.
     */
    private boolean verifySignature(Incidence incident, PublicKey publicKey, byte[] signature) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(Incidence.convertirIncidenciaABytes(incident));
        return verifier.verify(signature);
    }

    /**
     * Este método envía un mensaje cifrado al cliente.
     * Recibe el mensaje, el flujo de salida y la clave simétrica.
     * Cifra el mensaje y lo envía al cliente.
     */
    private void sendEncryptedMessage(String message, ObjectOutputStream outputStream, SecretKey symmetricKey) throws Exception {
        byte[] encryptedMessage = Cipher.stringCipher(message, symmetricKey);
        outputStream.writeInt(encryptedMessage.length);
        outputStream.write(encryptedMessage);
        outputStream.flush();
    }

}
