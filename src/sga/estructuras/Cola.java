package sga.estructuras;

import sga.excepciones.EstructuraVaciaException;

import java.util.Iterator;

/**
 * Cola FIFO genérica. Se usa para las solicitudes del canal externo.
 */
public class Cola<T> implements Iterable<T> {
    private final ListaEnlazada<T> elementos = new ListaEnlazada<>();

    public void encolar(T dato) {
        elementos.agregarFinal(dato);
    }

    public T desencolar() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("No se puede desencolar: la cola está vacía");
        }
        return elementos.eliminar(0);
    }

    public T frente() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola está vacía");
        }
        return elementos.primerElemento();
    }

    public boolean estaVacia() {
        return elementos.estaVacia();
    }

    public int tamano() {
        return elementos.tamano();
    }

    @Override
    public Iterator<T> iterator() {
        return elementos.iterator();
    }
}
