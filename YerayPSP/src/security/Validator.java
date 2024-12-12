package security;


import model.User;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.regex.Pattern;

public class Validator {

    // Métodos de validación
    public boolean validarEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    public boolean validarPassword(String password) {
        return password.length() >= 8;
    }

    public String hashPassword(String password) {
        // Implementa una función de hashing para mayor seguridad (ejemplo: bcrypt)
        return Integer.toHexString(password.hashCode()); // Ejemplo básico (NO usar en producción)
    }

    public boolean existeUsuario(String nombre, List<User> usuariosValidos) {
        return usuariosValidos.stream().anyMatch(u -> u.getNombre().equals(nombre));
    }

    public User validarUsuario(String nombre, String password, List<User> usuariosValidos) {
        Validator validador = new Validator();
        for (User usuario : usuariosValidos) {
            if (usuario.getNombre().equals(nombre) &&
                    usuario.getPassword().equals(validador.hashPassword(password))) {
                return usuario;
            }
        }
        return null;
    }

    public static PublicKey reconstruirClavePublica(byte[] clavePublicaBytes) throws Exception {
        // Crear un objeto X509EncodedKeySpec con los bytes descifrados
        X509EncodedKeySpec spec = new X509EncodedKeySpec(clavePublicaBytes);

        // Usar KeyFactory para reconstruir la clave pública
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Cambia "RSA" si usas otro algoritmo
        return keyFactory.generatePublic(spec);
    }
}
