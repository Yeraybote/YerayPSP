package model;


import security.Validator;

import java.io.*;

public class User implements Serializable {
    // Atributos
    private String nombre;
    private String apellido;
    private int edad;
    private String email;
    private String password; // La contraseña se debe almacenar de forma segura (ejemplo: hash)


    public User() {
    }

    // Constructor
    public User(String nombre, String apellido, int edad, String email, String password) throws IllegalArgumentException {
        if (!new Validator().validarEmail(email)) {
            throw new IllegalArgumentException("Email no válido");
        }
        if (!new Validator().validarPassword(password)) {
            throw new IllegalArgumentException("Contraseña no válida. Debe tener al menos 8 caracteres.");
        }
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.password = new Validator().hashPassword(password);
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws IllegalArgumentException{
        if (new Validator().validarEmail(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Email no válido");
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws IllegalArgumentException{
        if (new Validator().validarPassword(password)) {
            this.password = new Validator().hashPassword(password);
        } else {
            throw new IllegalArgumentException("Contraseña no válida. Debe tener al menos 8 caracteres.");
        }
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", edad=" + edad +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    //Funciones privadas

    public static byte[] convertirUsuarioABytes(User usuario) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(usuario);
        oos.close();
        return bos.toByteArray();
    }

    public static User convertirBytesAUsuario(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        User usuario = (User) ois.readObject();
        ois.close();
        return usuario;
    }
}