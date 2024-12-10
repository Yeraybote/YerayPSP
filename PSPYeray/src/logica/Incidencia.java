package logica;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Incidencia implements Serializable {
    private String descripcion;
    private String lugarFisico;
    private String empleado;
    private String firma;

    @Expose(serialize = false, deserialize = false)
    private transient PublicKey clavePublica;

    public String toJson() {
        return new Gson().toJson(this);
    }

    public Incidencia(String descripcion, String lugarFisico, String empleado) {
        this.descripcion = descripcion;
        this.lugarFisico = lugarFisico;
        this.empleado = empleado;
    }

    public void setClavePublica(PublicKey clavePublica) {
        this.clavePublica = clavePublica;
    }

    public PublicKey getClavePublica() {
        return clavePublica;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getLugarFisico() {
        return lugarFisico;
    }

    public String getEmpleado() {
        return empleado;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "descripcion='" + descripcion + '\'' +
                ", lugarFisico='" + lugarFisico + '\'' +
                ", empleado='" + empleado + '\'' +
                ", firma='" + firma + '\'' +
                '}';
    }

}
