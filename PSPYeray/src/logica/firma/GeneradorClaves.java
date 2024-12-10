package logica.firma;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class GeneradorClaves {
    public static KeyPair generarParDeClaves() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Longitud de clave segura
        return keyGen.generateKeyPair();
    }
}
