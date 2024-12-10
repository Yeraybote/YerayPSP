package logica;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    private String usuario;
    private String password;

    public LoginRequest(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }
}
