package sga.modelo;

public class NodoEdificio {
    public enum Tipo { APARTAMENTO, RECEPCION, ESCALERA, ASEO }

    private final String id;
    private final Tipo tipo;
    private final String etiqueta;
    private final int x;
    private final int y;
    private Apartamento apartamento;

    public NodoEdificio(String id, Tipo tipo, String etiqueta, int x, int y) {
        this.id = id;
        this.tipo = tipo;
        this.etiqueta = etiqueta;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public void setApartamento(Apartamento apartamento) {
        this.apartamento = apartamento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodoEdificio)) {
            return false;
        }
        return id.equals(((NodoEdificio) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
