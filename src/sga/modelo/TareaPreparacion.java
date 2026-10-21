package sga.modelo;

import java.time.LocalTime;

public class TareaPreparacion {
    private final Apartamento apartamento;
    private final boolean entradaHoy;
    private final LocalTime horaLlegadaEstimada;

    public TareaPreparacion(Apartamento apartamento, boolean entradaHoy, LocalTime horaLlegadaEstimada) {
        this.apartamento = apartamento;
        this.entradaHoy = entradaHoy;
        this.horaLlegadaEstimada = horaLlegadaEstimada;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public boolean isEntradaHoy() {
        return entradaHoy;
    }

    public LocalTime getHoraLlegadaEstimada() {
        return horaLlegadaEstimada;
    }

    @Override
    public String toString() {
        String prioridad = entradaHoy ? "P1 llegada " + horaLlegadaEstimada : "P2 sin entrada hoy";
        return apartamento.getCodigo() + " - " + prioridad;
    }
}
