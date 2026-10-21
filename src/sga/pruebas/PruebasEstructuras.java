package sga.pruebas;

import sga.estructuras.ArbolBinarioBusqueda;
import sga.estructuras.Cola;
import sga.estructuras.Grafo;
import sga.estructuras.ListaEnlazada;
import sga.estructuras.Pila;
import sga.excepciones.EstructuraVaciaException;
import sga.excepciones.FechasInvalidasException;
import sga.excepciones.ReservaSolapadaException;
import sga.modelo.Apartamento;
import sga.modelo.CanalReserva;
import sga.modelo.Estancia;
import sga.modelo.Huesped;
import sga.persistencia.CargadorEscenario;
import sga.servicios.SistemaAlojamiento;

import java.time.LocalDate;

/**
 * Pruebas simples sin JUnit para verificar el núcleo. En la entrega final
 * conviene pasarlas a JUnit 5 (un assert por caso).
 */
public class PruebasEstructuras {
    private static int ok;
    private static int fail;

    public static void main(String[] args) {
        pruebaColaFifo();
        pruebaPilaLifo();
        pruebaArbolBusqueda();
        pruebaGrafoDijkstra();
        pruebaSolapamiento();
        pruebaEstanciaIterable();
        System.out.println("Pruebas OK=" + ok + " FALLIDAS=" + fail);
        if (fail > 0) {
            System.exit(1);
        }
    }

    private static void pruebaColaFifo() {
        Cola<Integer> cola = new Cola<>();
        cola.encolar(1);
        cola.encolar(2);
        afirmar("FIFO", cola.desencolar() == 1 && cola.desencolar() == 2);
        try {
            cola.desencolar();
            afirmar("cola vacía debía fallar", false);
        } catch (EstructuraVaciaException e) {
            afirmar("excepción cola vacía", true);
        }
    }

    private static void pruebaPilaLifo() {
        Pila<String> pila = new Pila<>();
        pila.apilar("a");
        pila.apilar("b");
        afirmar("LIFO", "b".equals(pila.desapilar()) && "a".equals(pila.desapilar()));
    }

    private static void pruebaArbolBusqueda() {
        ArbolBinarioBusqueda<Integer> arbol = new ArbolBinarioBusqueda<>();
        arbol.insertar(5);
        arbol.insertar(2);
        arbol.insertar(8);
        afirmar("buscar 8", arbol.buscar(8) != null);
        afirmar("eliminar 2", arbol.eliminar(2) && arbol.buscar(2) == null);
        afirmar("altura >= 1", arbol.altura() >= 1);
    }

    private static void pruebaGrafoDijkstra() {
        Grafo<String> grafo = new Grafo<>();
        grafo.agregarArista("A", "B", 2);
        grafo.agregarArista("B", "C", 2);
        grafo.agregarArista("A", "C", 10);
        ListaEnlazada<String> ruta = grafo.dijkstra("A", "C");
        afirmar("dijkstra A-B-C", ruta.tamano() == 3 && "B".equals(ruta.obtener(1)));
    }

    private static void pruebaSolapamiento() {
        SistemaAlojamiento sga = CargadorEscenario.cargar();
        try {
            Estancia e = new Estancia(LocalDate.now().plusDays(2), LocalDate.now().plusDays(5));
            sga.crearReserva("1002", "A101", e, CanalReserva.DIRECTA);
            afirmar("debía chocar A101", false);
        } catch (ReservaSolapadaException e) {
            afirmar("solapamiento detectado", true);
        } catch (Exception e) {
            afirmar("solapamiento: " + e.getMessage(), false);
        }
    }

    private static void pruebaEstanciaIterable() {
        try {
            Estancia e = new Estancia(LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 12));
            int n = 0;
            for (LocalDate ignore : e) {
                n++;
            }
            afirmar("10 al 12 = 2 noches", n == 2 && e.noches() == 2);
        } catch (FechasInvalidasException e) {
            afirmar("estancia", false);
        }
        Huesped h = new Huesped("9", "X", LocalDate.now(), "", "");
        Apartamento a = new Apartamento("Z", "Z", "A", 1, 1, 2, "", 1, 1, 1);
        afirmar("comparables", h.compareTo(new Huesped("9", "Y", LocalDate.now(), "", "")) == 0
                && a.compareTo(new Apartamento("Z", "", "", 0, 0, 0, "", 0, 0, 0)) == 0);
    }

    private static void afirmar(String nombre, boolean condicion) {
        if (condicion) {
            ok++;
            System.out.println("OK  " + nombre);
        } else {
            fail++;
            System.out.println("FAIL " + nombre);
        }
    }
}
