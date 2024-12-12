package security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Descipher {
    // Método para desencriptar datos usando clave simétrica
    public static String stringDescipher(byte[] textoCifrado, SecretKey claveSecreta) throws
            Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, claveSecreta);
        byte[] textoDecifrado = cipher.doFinal(textoCifrado);
        return new String(textoDecifrado);
    }

    // Método para descifrar datos con clave simétrica
    public static byte[] dataDescipher(byte[] datosCifrados, SecretKey claveSecreta) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, claveSecreta);
        return cipher.doFinal(datosCifrados);
    }
}
