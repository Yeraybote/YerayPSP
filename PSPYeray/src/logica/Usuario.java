package logica;

public class Usuario {
    private String nombre;
    private String apellido;
    private int edad;
    private String email;
    private String usuario;
    private String passwordCifrada;

    public Usuario(String nombre, String apellido, int edad, String email, String usuario, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.usuario = usuario;
        try {
            this.passwordCifrada = UtilidadCifrado.cifrar(password); // Cifrar la contraseña
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar la contraseña", e);
        }
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return passwordCifrada;
    }

    public String toString() {
        return "Usuario: " + usuario + ", Nombre: " + nombre + " " + apellido + ", Edad: " + edad + ", Email: " + email;
    }
}
