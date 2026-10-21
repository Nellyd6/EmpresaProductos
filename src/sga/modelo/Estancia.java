package sga.modelo;

import sga.excepciones.FechasInvalidasException;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Estancia iterable noche por noche. La noche de salida no se cobra ni se ocupa:
 * del 10 al 12 de marzo son dos noches (10 y 11).
 */
public class Estancia implements Iterable<LocalDate> {
    private final LocalDate entrada;
    private final LocalDate salida;

    public Estancia(LocalDate entrada, LocalDate salida) throws FechasInvalidasException {
        if (entrada == null || salida == null || !salida.isAfter(entrada)) {
            throw new FechasInvalidasException("La fecha de salida debe ser posterior a la de entrada");
        }
        this.entrada = entrada;
        this.salida = salida;
    }

    public LocalDate getEntrada() {
        return entrada;
    }

    public LocalDate getSalida() {
        return salida;
    }

    public int noches() {
        return (int) (salida.toEpochDay() - entrada.toEpochDay());
    }

    public boolean solapaCon(Estancia otra) {
        return entrada.isBefore(otra.salida) && otra.entrada.isBefore(salida);
    }

    @Override
    public Iterator<LocalDate> iterator() {
        return new Iterator<LocalDate>() {
            private LocalDate actual = entrada;

            @Override
            public boolean hasNext() {
                return actual.isBefore(salida);
            }

            @Override
            public LocalDate next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                LocalDate noche = actual;
                actual = actual.plusDays(1);
                return noche;
            }
        };
    }

    @Override
    public String toString() {
        return entrada + " → " + salida + " (" + noches() + " noches)";
    }
}
