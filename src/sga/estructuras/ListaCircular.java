package sga.estructuras;

import sga.excepciones.EstructuraVaciaException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista circular genérica. Se usa para rotar los turnos de recepción.
 */
public class ListaCircular<T> implements Iterable<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> actual;
    private int tamano;

    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (actual == null) {
            actual = nuevo;
            actual.siguiente = actual;
        } else {
            Nodo<T> recorredor = actual;
            while (recorredor.siguiente != actual) {
                recorredor = recorredor.siguiente;
            }
            recorredor.siguiente = nuevo;
            nuevo.siguiente = actual;
        }
        tamano++;
    }

    public T turnoActual() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("No hay recepcionistas en rotación");
        }
        return actual.dato;
    }

    public T avanzarTurno() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("No hay recepcionistas en rotación");
        }
        actual = actual.siguiente;
        return actual.dato;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public int tamano() {
        return tamano;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> recorredor = actual;
            private int vistos = 0;

            @Override
            public boolean hasNext() {
                return actual != null && vistos < tamano;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T dato = recorredor.dato;
                recorredor = recorredor.siguiente;
                vistos++;
                return dato;
            }
        };
    }
}
