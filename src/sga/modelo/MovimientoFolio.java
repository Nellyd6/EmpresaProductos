package sga.modelo;

import java.time.LocalDateTime;

public class MovimientoFolio {
    public enum Tipo { CARGO, PAGO, AJUSTE }

    private final Tipo tipo;
    private final String descripcion;
    private final double valor;
    private final LocalDateTime fecha;
    private final boolean esReverso;

    public MovimientoFolio(Tipo tipo, String descripcion, double valor, boolean esReverso) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.valor = valor;
        this.fecha = LocalDateTime.now();
        this.esReverso = esReverso;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean isEsReverso() {
        return esReverso;
    }

    public MovimientoFolio contrario() {
        Tipo tipoContrario = tipo == Tipo.CARGO ? Tipo.PAGO : Tipo.CARGO;
        if (tipo == Tipo.AJUSTE) {
            tipoContrario = Tipo.AJUSTE;
        }
        return new MovimientoFolio(tipoContrario, "Reverso: " + descripcion, -valor, true);
    }

    @Override
    public String toString() {
        return fecha + " | " + tipo + " | " + descripcion + " | " + valor;
    }
}
