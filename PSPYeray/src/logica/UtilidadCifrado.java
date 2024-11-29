package logica;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class UtilidadCifrado {
    private static final String ALGORITMO = "AES";
    private static final String CLAVE = "1234567890123456"; // 16 caracteres para AES-128

    // Metodo para cifrar texto
    public static String cifrar(String texto) throws Exception {
        SecretKey clave = new SecretKeySpec(CLAVE.getBytes(), ALGORITMO);
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.ENCRYPT_MODE, clave);
        byte[] textoCifrado = cipher.doFinal(texto.getBytes());
        return Base64.getEncoder().encodeToString(textoCifrado);
    }

    // Metodo para descifrar texto
    public static String descifrar(String textoCifrado) throws Exception {
        SecretKey clave = new SecretKeySpec(CLAVE.getBytes(), ALGORITMO);
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.DECRYPT_MODE, clave);
        byte[] textoDescifrado = cipher.doFinal(Base64.getDecoder().decode(textoCifrado));
        return new String(textoDescifrado);
    }
}
