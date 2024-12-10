package logica;

import java.io.Serializable;

public class RegisterRequest implements Serializable {
    private String nombre;
    private String apellido;
    private int edad;
    private String email;
    private String usuario;
    private String password;

    public RegisterRequest(String nombre, String apellido, int edad, String email, String usuario, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.usuario = usuario;
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public int getEdad() {
        return edad;
    }

    public String getEmail() {
        return email;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }
}
