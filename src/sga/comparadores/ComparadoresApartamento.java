package sga.comparadores;

import sga.modelo.Apartamento;
import sga.modelo.TipoTemporada;

import java.util.Comparator;

/**
 * Órdenes alternativos exigidos por el enunciado (Comparator).
 */
public final class ComparadoresApartamento {
    private ComparadoresApartamento() {
    }

    public static Comparator<Apartamento> porPrecio(TipoTemporada temporada) {
        return Comparator.comparingDouble(a -> a.tarifa(temporada));
    }

    public static Comparator<Apartamento> porCapacidad() {
        return Comparator.comparingInt(Apartamento::getCapacidadMaxima);
    }

    public static Comparator<Apartamento> porDormitorios() {
        return Comparator.comparingInt(Apartamento::getDormitorios);
    }

    public static Comparator<Apartamento> porNumeroReservas() {
        return Comparator.comparingInt(Apartamento::cantidadReservas);
    }
}
