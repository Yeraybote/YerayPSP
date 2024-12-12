package model;


import java.io.*;

public class Incidence implements Serializable {
    private final String id;
    private String description;
    private String site;
    private String employee;
    private String level;

    private int hours;
    private static int incidenceCount = 0;

    // Constructor
    public Incidence(String description, String site, String employee) {
        this.id = generarCodigoUnico();
        this.description = description;
        this.site = site;
        this.employee = employee;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }


    public int getHours() {
        return hours;
    }

    public void setHours() {
        this.hours = calcularHorasIncidencia(this.getLevel());
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = createLevel(level);
    }

    @Override
    public String toString() {
        return "ID: " + id +
                "\nDescription: " + description +
                "\nSite: " + site +
                "\nEmployee: " + employee +
                "\nLevel: " + level;
    }

    public static byte[] convertirIncidenciaABytes(Incidence incidencia) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(incidencia);
        oos.close();
        return bos.toByteArray();
    }

    public static Incidence convertirBytesAIncidencia(byte[] incidenciaBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(incidenciaBytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Incidence incidencia = (Incidence) ois.readObject();
        ois.close();
        return incidencia;
    }

    public static String createLevel(String level) {
        String levellower = level.toLowerCase();
        if (levellower.contains("urgente") || levellower.contains("crítico") || levellower.contains("falla grave")) {
            return "Urgente";
        } else if (levellower.contains("problema") || levellower.contains("error") || levellower.contains("fallo")) {
            return "Moderada";
        } else if (levellower.contains("consulta") || levellower.contains("revisión") || levellower.contains("sugerencia")) {
            return "Leve";
        }

        return "Bloqueada"; // Por defecto, si no se encuentra ninguna palabra clave
    }

    private String generarCodigoUnico() {
        String codigo = "INC-" + incidenceCount;
        incidenceCount++;
        return codigo;
    }

    private int calcularHorasIncidencia(String level) {
        switch (level) {
            case "Urgente":
                return 16;
            case "Moderada":
                return 8;
            case "Leve":
                return 4;
            default:
                return 2;
        }
    }

}