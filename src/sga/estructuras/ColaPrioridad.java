package sga.estructuras;

import sga.excepciones.EstructuraVaciaException;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cola de prioridad genérica implementada con heap binario.
 * Sirve para preparación de apartamentos y para vencimiento de reservas pendientes.
 */
@SuppressWarnings("unchecked")
public class ColaPrioridad<T> implements Iterable<T> {
    private Object[] heap = new Object[16];
    private int tamano;
    private final Comparator<T> comparador;

    public ColaPrioridad(Comparator<T> comparador) {
        this.comparador = comparador;
    }

    public void encolar(T dato) {
        if (tamano == heap.length) {
            crecer();
        }
        heap[tamano] = dato;
        subir(tamano);
        tamano++;
    }

    public T desencolar() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("No se puede desencolar: la cola de prioridad está vacía");
        }
        T minimo = (T) heap[0];
        tamano--;
        heap[0] = heap[tamano];
        heap[tamano] = null;
        if (tamano > 0) {
            bajar(0);
        }
        return minimo;
    }

    public T frente() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola de prioridad está vacía");
        }
        return (T) heap[0];
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public int tamano() {
        return tamano;
    }

    private void subir(int indice) {
        while (indice > 0) {
            int padre = (indice - 1) / 2;
            if (comparador.compare((T) heap[indice], (T) heap[padre]) >= 0) {
                break;
            }
            intercambiar(indice, padre);
            indice = padre;
        }
    }

    private void bajar(int indice) {
        while (true) {
            int izquierdo = 2 * indice + 1;
            int derecho = 2 * indice + 2;
            int menor = indice;
            if (izquierdo < tamano && comparador.compare((T) heap[izquierdo], (T) heap[menor]) < 0) {
                menor = izquierdo;
            }
            if (derecho < tamano && comparador.compare((T) heap[derecho], (T) heap[menor]) < 0) {
                menor = derecho;
            }
            if (menor == indice) {
                break;
            }
            intercambiar(indice, menor);
            indice = menor;
        }
    }

    private void intercambiar(int i, int j) {
        Object tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private void crecer() {
        Object[] nuevo = new Object[heap.length * 2];
        System.arraycopy(heap, 0, nuevo, 0, heap.length);
        heap = nuevo;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int indice = 0;

            @Override
            public boolean hasNext() {
                return indice < tamano;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (T) heap[indice++];
            }
        };
    }
}
