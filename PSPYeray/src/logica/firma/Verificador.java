package logica.firma;

import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class Verificador {
    public static boolean verificarFirma(String mensaje, String firmaBase64, PublicKey clavePublica) throws Exception {
        Signature firma = Signature.getInstance("SHA256withRSA");
        firma.initVerify(clavePublica);
        firma.update(mensaje.getBytes());
        byte[] firmaDigital = Base64.getDecoder().decode(firmaBase64);
        return firma.verify(firmaDigital);
    }
}
