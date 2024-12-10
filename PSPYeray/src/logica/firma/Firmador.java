package logica.firma;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class Firmador {
    public static String firmarMensaje(String mensaje, PrivateKey clavePrivada) throws Exception {
        Signature firma = Signature.getInstance("SHA256withRSA");
        firma.initSign(clavePrivada);
        firma.update(mensaje.getBytes()); // Hash del mensaje
        byte[] firmaDigital = firma.sign();
        return Base64.getEncoder().encodeToString(firmaDigital); // Retorna la firma en base64
    }
}
