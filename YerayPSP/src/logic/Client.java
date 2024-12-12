package logic;

import security.Cipher;
import security.Descipher;
import model.Incidence;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.ConnectException;
import java.security.*;
import javax.crypto.SecretKey;

public class Client {
    private static final int PORT = 6969;

    public static void main(String[] args) {
        try (Socket client = new Socket("localhost", PORT);
             ObjectInputStream input = new ObjectInputStream(client.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            // Generar claves asimétricas para firma digital
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            SecretKey secretKey = (SecretKey) input.readObject();
            boolean running = true;

            while (running) {
                mostrarMenu();
                String opcion = reader.readLine();

                switch (opcion) {
                    case "1":
                        loguearse(output, reader, secretKey, input, publicKey, privateKey);

                        break;
                    case "2":
                        crearUsuario(output, reader, secretKey, input);
                        break;
                    case "3":
                        enviarMensajeCifrado(output, "END", secretKey);
                        running = false;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            }
        } catch (ConnectException e) {
            System.out.println("Error de conexión con el servidor.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("1. Login");
        System.out.println("2. Registro");
        System.out.println("3. Acabar");
        System.out.print("Seleccione una opción: ");
    }

    private static void crearUsuario(ObjectOutputStream output, BufferedReader reader, SecretKey secretKey, ObjectInputStream input) throws Exception {
        enviarMensajeCifrado(output, "REGISTER", secretKey);

        User nuevoUsuario;
        while (true) {
            try {
                System.out.print("Email: ");
                String email = reader.readLine();
                System.out.print("Nombre: ");
                String nombre = reader.readLine();
                System.out.print("Apellido: ");
                String apellido = reader.readLine();
                System.out.print("Edad: ");
                int edad = Integer.parseInt(reader.readLine());
                System.out.print("Contraseña: ");
                String password = reader.readLine();

                nuevoUsuario = new User(nombre, apellido, edad, email, password);
                break;
            } catch (Exception e) {
                System.out.println("Datos inválidos. Inténtalo nuevamente.");
            }
        }

        byte[] usuarioEnBytes = Cipher.dataCipher(User.convertirUsuarioABytes(nuevoUsuario), secretKey);
        enviarDatos(output, usuarioEnBytes);

        String respuesta = input.readUTF();
        System.out.println(respuesta);
    }

    private static void loguearse(ObjectOutputStream output, BufferedReader reader, SecretKey secretKey, ObjectInputStream input, PublicKey publicKey, PrivateKey privateKey) throws Exception {
        enviarMensajeCifrado(output, "LOGIN", secretKey);

        System.out.print("Nombre: ");
        String nombre = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();

        enviarDatos(output, Cipher.stringCipher(nombre, secretKey));
        enviarDatos(output, Cipher.stringCipher(password, secretKey));

        String respuestaLogin = input.readUTF();

        if ("VALIDO".equals(respuestaLogin)) {
            System.out.println("Logueado con éxito.");
            manejarSesion(output, reader, secretKey, input, nombre, publicKey, privateKey);
        } else {
            System.out.println("Credenciales inválidas.");
        }
    }

    private static void manejarSesion(ObjectOutputStream output, BufferedReader reader, SecretKey secretKey, ObjectInputStream input, String nombre, PublicKey publicKey, PrivateKey privateKey) throws IOException, ClassNotFoundException {
        boolean activo = true;

        while (activo) {
            System.out.println("1. Crear Incidencia");
            System.out.println("2. Salir");
            try {
                int opcion = Integer.parseInt(reader.readLine());
                switch (opcion) {
                    case 1:
                        crearIncidencia(output, reader, secretKey, input, nombre, publicKey, privateKey);
                        break;
                    case 2:
                        enviarMensajeCifrado(output, "SALIR", secretKey);
                        activo = false;
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Inténtalo de nuevo.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void crearIncidencia(ObjectOutputStream output, BufferedReader reader, SecretKey secretKey, ObjectInputStream input, String nombre, PublicKey publicKey, PrivateKey privateKey) throws Exception {
        enviarMensajeCifrado(output, "INCIDENCIA", secretKey);

        System.out.print("Lugar de la incidencia: ");
        String lugar = reader.readLine();
        System.out.print("Descripción de la incidencia: ");
        String descripcion = reader.readLine();

        Incidence incidencia = new Incidence(descripcion, lugar, nombre);

        enviarDatos(output, Cipher.dataCipher(publicKey.getEncoded(), secretKey));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(Incidence.convertirIncidenciaABytes(incidencia));
        byte[] firmaDigital = signature.sign();

        enviarDatos(output, Cipher.dataCipher(Incidence.convertirIncidenciaABytes(incidencia), secretKey));
        enviarDatos(output, Cipher.dataCipher(firmaDigital, secretKey));

        int longitud = input.readInt();
        byte[] respuesta = new byte[longitud];
        input.readFully(respuesta);

        System.out.println(Descipher.stringDescipher(respuesta, secretKey));
    }

    private static void enviarMensajeCifrado(ObjectOutputStream output, String mensaje, SecretKey secretKey) throws Exception {
        byte[] mensajeCifrado = Cipher.stringCipher(mensaje, secretKey);
        enviarDatos(output, mensajeCifrado);
    }

    private static void enviarDatos(ObjectOutputStream output, byte[] datos) throws IOException {
        output.writeInt(datos.length);
        output.write(datos);
        output.flush();
    }
}
