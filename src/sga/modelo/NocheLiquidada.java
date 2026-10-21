package sga.modelo;

import java.time.LocalDate;

public class NocheLiquidada {
    private final LocalDate fecha;
    private final TipoTemporada temporada;
    private final int personasQuePagan;
    private final double tarifaPersona;
    private final double subtotal;

    public NocheLiquidada(LocalDate fecha, TipoTemporada temporada, int personasQuePagan, double tarifaPersona) {
        this.fecha = fecha;
        this.temporada = temporada;
        this.personasQuePagan = personasQuePagan;
        this.tarifaPersona = tarifaPersona;
        this.subtotal = personasQuePagan * tarifaPersona;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public TipoTemporada getTemporada() {
        return temporada;
    }

    public int getPersonasQuePagan() {
        return personasQuePagan;
    }

    public double getTarifaPersona() {
        return tarifaPersona;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return fecha + " [" + temporada + "] " + personasQuePagan + " x $" + tarifaPersona + " = $" + subtotal;
    }
}
