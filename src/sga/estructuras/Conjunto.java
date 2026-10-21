package sga.estructuras;

import java.util.Iterator;

/**
 * Conjunto genérico sin repetidos. Se usa para los apartamentos favoritos de un huésped.
 */
public class Conjunto<T> implements Iterable<T> {
    private final ListaEnlazada<T> elementos = new ListaEnlazada<>();

    public boolean agregar(T dato) {
        if (elementos.contiene(dato)) {
            return false;
        }
        elementos.agregarFinal(dato);
        return true;
    }

    public boolean eliminar(T dato) {
        return elementos.eliminar(dato);
    }

    public boolean contiene(T dato) {
        return elementos.contiene(dato);
    }

    public int tamano() {
        return elementos.tamano();
    }

    public boolean estaVacio() {
        return elementos.estaVacia();
    }

    @Override
    public Iterator<T> iterator() {
        return elementos.iterator();
    }
}
