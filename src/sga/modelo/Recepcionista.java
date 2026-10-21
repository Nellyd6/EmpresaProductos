package sga.modelo;

public class Recepcionista {
    private final String id;
    private final String nombre;

    public Recepcionista(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
