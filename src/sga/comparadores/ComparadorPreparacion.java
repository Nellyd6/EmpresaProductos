package sga.comparadores;

import sga.modelo.TareaPreparacion;

import java.util.Comparator;

/**
 * Prioridad 1: entrada hoy, ordenados por hora de llegada.
 * Prioridad 2: el resto. Combina comparadores con thenComparing.
 */
public final class ComparadorPreparacion {
    private ComparadorPreparacion() {
    }

    public static Comparator<TareaPreparacion> crear() {
        return Comparator.comparing((TareaPreparacion t) -> !t.isEntradaHoy())
                .thenComparing(t -> t.getHoraLlegadaEstimada() == null
                        ? java.time.LocalTime.MAX
                        : t.getHoraLlegadaEstimada());
    }
}
