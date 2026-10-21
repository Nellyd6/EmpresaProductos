package sga.estructuras;

import sga.excepciones.EstructuraVaciaException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista doblemente enlazada genérica. Se usa para historial de estancias,
 * ocupantes de una reserva y desglose de noches liquidadas.
 */
public class ListaEnlazada<T> implements Iterable<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> anterior;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamano;

    public void agregarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
        tamano++;
    }

    public void agregarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = cola = nuevo;
        } else {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza = nuevo;
        }
        tamano++;
    }

    public T obtener(int indice) {
        validarIndice(indice);
        return nodoEn(indice).dato;
    }

    public void set(int indice, T dato) {
        validarIndice(indice);
        nodoEn(indice).dato = dato;
    }

    public T eliminar(int indice) {
        validarIndice(indice);
        Nodo<T> objetivo = nodoEn(indice);
        desenlazar(objetivo);
        return objetivo.dato;
    }

    public boolean eliminar(T dato) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (iguales(actual.dato, dato)) {
                desenlazar(actual);
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public boolean contiene(T dato) {
        for (T elemento : this) {
            if (iguales(elemento, dato)) {
                return true;
            }
        }
        return false;
    }

    public T primerElemento() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La lista está vacía");
        }
        return cabeza.dato;
    }

    public T ultimoElemento() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La lista está vacía");
        }
        return cola.dato;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public int tamano() {
        return tamano;
    }

    public void limpiar() {
        cabeza = cola = null;
        tamano = 0;
    }

    private void desenlazar(Nodo<T> nodo) {
        if (nodo.anterior != null) {
            nodo.anterior.siguiente = nodo.siguiente;
        } else {
            cabeza = nodo.siguiente;
        }
        if (nodo.siguiente != null) {
            nodo.siguiente.anterior = nodo.anterior;
        } else {
            cola = nodo.anterior;
        }
        tamano--;
    }

    private Nodo<T> nodoEn(int indice) {
        Nodo<T> actual;
        if (indice < tamano / 2) {
            actual = cabeza;
            for (int i = 0; i < indice; i++) {
                actual = actual.siguiente;
            }
        } else {
            actual = cola;
            for (int i = tamano - 1; i > indice; i--) {
                actual = actual.anterior;
            }
        }
        return actual;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
    }

    private boolean iguales(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }
}
