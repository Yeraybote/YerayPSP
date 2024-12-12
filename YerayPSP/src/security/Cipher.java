package security;

import javax.crypto.SecretKey;

public class Cipher {
    // Método para cifrar datos con clave simétrica
    public static byte[] dataCipher(byte[] data, SecretKey secretKey) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    // Método para encriptar Strings con clave simétrica
    public static byte[] stringCipher(String textoOriginal, SecretKey secretKey) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
        byte[] textoCifrado = cipher.doFinal(textoOriginal.getBytes());
        return textoCifrado;
    }

}
