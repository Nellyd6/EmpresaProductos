package sga.estructuras;

import sga.excepciones.EstructuraVaciaException;

import java.util.Iterator;

/**
 * Pila LIFO genérica. Se usa para deshacer operaciones del folio
 * y para navegación "atrás" entre pantallas.
 */
public class Pila<T> implements Iterable<T> {
    private final ListaEnlazada<T> elementos = new ListaEnlazada<>();

    public void apilar(T dato) {
        elementos.agregarInicio(dato);
    }

    public T desapilar() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("No se puede desapilar: la pila está vacía");
        }
        return elementos.eliminar(0);
    }

    public T cima() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La pila está vacía");
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
