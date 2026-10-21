package sga.estructuras;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Árbol binario de búsqueda genérico. Inserta, busca y elimina de forma recursiva.
 * Se usa para apartamentos por código, huéspedes por documento y reservas por fecha.
 */
public class ArbolBinarioBusqueda<T extends Comparable<T>> implements Iterable<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> izquierdo;
        Nodo<T> derecho;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> raiz;
    private int tamano;
    private final Comparator<T> comparador;

    public ArbolBinarioBusqueda() {
        this(Comparator.naturalOrder());
    }

    public ArbolBinarioBusqueda(Comparator<T> comparador) {
        this.comparador = comparador;
    }

    public void insertar(T dato) {
        raiz = insertarRec(raiz, dato);
    }

    private Nodo<T> insertarRec(Nodo<T> nodo, T dato) {
        if (nodo == null) {
            tamano++;
            return new Nodo<>(dato);
        }
        int cmp = comparador.compare(dato, nodo.dato);
        if (cmp < 0) {
            nodo.izquierdo = insertarRec(nodo.izquierdo, dato);
        } else if (cmp > 0) {
            nodo.derecho = insertarRec(nodo.derecho, dato);
        } else {
            nodo.dato = dato;
        }
        return nodo;
    }

    public T buscar(T clave) {
        return buscarRec(raiz, clave);
    }

    private T buscarRec(Nodo<T> nodo, T clave) {
        if (nodo == null) {
            return null;
        }
        int cmp = comparador.compare(clave, nodo.dato);
        if (cmp == 0) {
            return nodo.dato;
        }
        return cmp < 0 ? buscarRec(nodo.izquierdo, clave) : buscarRec(nodo.derecho, clave);
    }

    public boolean eliminar(T clave) {
        int antes = tamano;
        raiz = eliminarRec(raiz, clave);
        return tamano < antes;
    }

    private Nodo<T> eliminarRec(Nodo<T> nodo, T clave) {
        if (nodo == null) {
            return null;
        }
        int cmp = comparador.compare(clave, nodo.dato);
        if (cmp < 0) {
            nodo.izquierdo = eliminarRec(nodo.izquierdo, clave);
        } else if (cmp > 0) {
            nodo.derecho = eliminarRec(nodo.derecho, clave);
        } else {
            tamano--;
            if (nodo.izquierdo == null) {
                return nodo.derecho;
            }
            if (nodo.derecho == null) {
                return nodo.izquierdo;
            }
            Nodo<T> sucesor = minimo(nodo.derecho);
            nodo.dato = sucesor.dato;
            nodo.derecho = eliminarMinimo(nodo.derecho);
        }
        return nodo;
    }

    private Nodo<T> minimo(Nodo<T> nodo) {
        return nodo.izquierdo == null ? nodo : minimo(nodo.izquierdo);
    }

    private Nodo<T> eliminarMinimo(Nodo<T> nodo) {
        if (nodo.izquierdo == null) {
            return nodo.derecho;
        }
        nodo.izquierdo = eliminarMinimo(nodo.izquierdo);
        return nodo;
    }

    public boolean algunoCumple(Predicate<T> predicado) {
        return algunoCumpleRec(raiz, predicado);
    }

    private boolean algunoCumpleRec(Nodo<T> nodo, Predicate<T> predicado) {
        if (nodo == null) {
            return false;
        }
        if (predicado.test(nodo.dato)) {
            return true;
        }
        return algunoCumpleRec(nodo.izquierdo, predicado) || algunoCumpleRec(nodo.derecho, predicado);
    }

    public int altura() {
        return alturaRec(raiz);
    }

    private int alturaRec(Nodo<T> nodo) {
        if (nodo == null) {
            return -1;
        }
        int izq = alturaRec(nodo.izquierdo);
        int der = alturaRec(nodo.derecho);
        return 1 + Math.max(izq, der);
    }

    public void inorden(ListaEnlazada<T> destino) {
        inordenRec(raiz, destino);
    }

    private void inordenRec(Nodo<T> nodo, ListaEnlazada<T> destino) {
        if (nodo == null) {
            return;
        }
        inordenRec(nodo.izquierdo, destino);
        destino.agregarFinal(nodo.dato);
        inordenRec(nodo.derecho, destino);
    }

    public void preorden(ListaEnlazada<T> destino) {
        preordenRec(raiz, destino);
    }

    private void preordenRec(Nodo<T> nodo, ListaEnlazada<T> destino) {
        if (nodo == null) {
            return;
        }
        destino.agregarFinal(nodo.dato);
        preordenRec(nodo.izquierdo, destino);
        preordenRec(nodo.derecho, destino);
    }

    public void postorden(ListaEnlazada<T> destino) {
        postordenRec(raiz, destino);
    }

    private void postordenRec(Nodo<T> nodo, ListaEnlazada<T> destino) {
        if (nodo == null) {
            return;
        }
        postordenRec(nodo.izquierdo, destino);
        postordenRec(nodo.derecho, destino);
        destino.agregarFinal(nodo.dato);
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int tamano() {
        return tamano;
    }

    @Override
    public Iterator<T> iterator() {
        ListaEnlazada<T> recorrido = new ListaEnlazada<>();
        inorden(recorrido);
        return recorrido.iterator();
    }
}
