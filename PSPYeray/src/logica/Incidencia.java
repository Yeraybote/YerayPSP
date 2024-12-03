package logica;
import java.io.Serializable;

public class Incidencia implements Serializable {
    private String descripcion;
    private String lugarFisico;
    private String empleado;
    private Cliente cliente;

    public Incidencia(String descripcion, String lugarFisico, String empleado, Cliente cliente) {
        this.descripcion = descripcion;
        this.lugarFisico = lugarFisico;
        this.empleado = empleado;
        this.cliente = cliente;
    }

    public Cliente getCliente() {
        return cliente;
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

    @Override
    public String toString() {
        return "Incidencia{" +
                "descripcion='" + descripcion + '\'' +
                ", lugarFisico='" + lugarFisico + '\'' +
                ", empleado='" + empleado + '\'' +
                '}';
    }

}
