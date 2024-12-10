package logica;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class RepositorioClavesPublicas {
    private final Map<String, PublicKey> clavesPublicas = new HashMap<>();

    // Registrar una clave pública para un empleado
    public void registrarClavePublica(String empleado, PublicKey clavePublica) {
        clavesPublicas.put(empleado, clavePublica);
    }

    // Obtener clave pública asociada a un empleado
    public PublicKey obtenerClavePublica(String empleado) {
        return clavesPublicas.get(empleado);
    }

    // Verificar si existe una clave pública para un empleado
    public boolean existeClavePublica(String empleado) {
        return clavesPublicas.containsKey(empleado);
    }
}
