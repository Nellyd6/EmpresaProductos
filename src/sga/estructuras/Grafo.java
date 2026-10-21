package sga.estructuras;

import java.util.Iterator;

/**
 * Grafo ponderado no dirigido genérico. Representa el mapa del edificio:
 * nodos = apartamentos, recepción, escaleras y cuartos de aseo;
 * aristas = pasillos con peso en metros o segundos.
 */
public class Grafo<T> implements Iterable<T> {

    public static class Arista<T> {
        private final T destino;
        private final double peso;

        public Arista(T destino, double peso) {
            this.destino = destino;
            this.peso = peso;
        }

        public T getDestino() {
            return destino;
        }

        public double getPeso() {
            return peso;
        }
    }

    private static class Vertice<T> {
        final T dato;
        final ListaEnlazada<Arista<T>> adyacentes = new ListaEnlazada<>();

        Vertice(T dato) {
            this.dato = dato;
        }
    }

    private final ListaEnlazada<Vertice<T>> vertices = new ListaEnlazada<>();

    public void agregarVertice(T dato) {
        if (buscarVertice(dato) == null) {
            vertices.agregarFinal(new Vertice<>(dato));
        }
    }

    public void agregarArista(T origen, T destino, double peso) {
        agregarVertice(origen);
        agregarVertice(destino);
        buscarVertice(origen).adyacentes.agregarFinal(new Arista<>(destino, peso));
        buscarVertice(destino).adyacentes.agregarFinal(new Arista<>(origen, peso));
    }

    public ListaEnlazada<Arista<T>> adyacentes(T dato) {
        Vertice<T> vertice = buscarVertice(dato);
        return vertice == null ? new ListaEnlazada<>() : vertice.adyacentes;
    }

    /**
     * Recorrido en profundidad. Sirve para detectar conectividad y agrupar por bloque.
     */
    public ListaEnlazada<T> dfs(T inicio) {
        ListaEnlazada<T> visitados = new ListaEnlazada<>();
        dfsRec(inicio, visitados);
        return visitados;
    }

    private void dfsRec(T actual, ListaEnlazada<T> visitados) {
        if (actual == null || visitados.contiene(actual) || buscarVertice(actual) == null) {
            return;
        }
        visitados.agregarFinal(actual);
        for (Arista<T> arista : adyacentes(actual)) {
            dfsRec(arista.destino, visitados);
        }
    }

    /**
     * Recorrido en amplitud. También puede usarse como ruta no ponderada.
     */
    public ListaEnlazada<T> bfs(T inicio) {
        ListaEnlazada<T> visitados = new ListaEnlazada<>();
        if (buscarVertice(inicio) == null) {
            return visitados;
        }
        Cola<T> cola = new Cola<>();
        cola.encolar(inicio);
        visitados.agregarFinal(inicio);
        while (!cola.estaVacia()) {
            T actual = cola.desencolar();
            for (Arista<T> arista : adyacentes(actual)) {
                if (!visitados.contiene(arista.destino)) {
                    visitados.agregarFinal(arista.destino);
                    cola.encolar(arista.destino);
                }
            }
        }
        return visitados;
    }

    /**
     * Ruta más corta por peso (metros o segundos) usando Dijkstra.
     */
    public ListaEnlazada<T> dijkstra(T origen, T destino) {
        ListaEnlazada<T> nodos = new ListaEnlazada<>();
        for (Vertice<T> vertice : vertices) {
            nodos.agregarFinal(vertice.dato);
        }
        ListaEnlazada<T> pendientes = copiar(nodos);
        ListaEnlazada<Double> distancias = new ListaEnlazada<>();
        ListaEnlazada<T> anteriores = new ListaEnlazada<>();
        for (T nodo : nodos) {
            distancias.agregarFinal(nodo.equals(origen) ? 0.0 : Double.POSITIVE_INFINITY);
            anteriores.agregarFinal(null);
        }

        while (!pendientes.estaVacia()) {
            int indiceMin = indiceMinimo(pendientes, nodos, distancias);
            T u = pendientes.eliminar(indiceMin);
            if (u.equals(destino)) {
                break;
            }
            int iu = indiceDe(nodos, u);
            for (Arista<T> arista : adyacentes(u)) {
                if (!pendientes.contiene(arista.destino)) {
                    continue;
                }
                int iv = indiceDe(nodos, arista.destino);
                double alternativa = distancias.obtener(iu) + arista.peso;
                if (alternativa < distancias.obtener(iv)) {
                    distancias.set(iv, alternativa);
                    anteriores.set(iv, u);
                }
            }
        }

        ListaEnlazada<T> ruta = new ListaEnlazada<>();
        T actual = destino;
        if (distancias.obtener(indiceDe(nodos, destino)).isInfinite()) {
            return ruta;
        }
        while (actual != null) {
            ruta.agregarInicio(actual);
            actual = anteriores.obtener(indiceDe(nodos, actual));
        }
        return ruta;
    }

    public boolean alcanzable(T origen, T destino) {
        return dfs(origen).contiene(destino);
    }

    public int cantidadVertices() {
        return vertices.tamano();
    }

    private Vertice<T> buscarVertice(T dato) {
        for (Vertice<T> vertice : vertices) {
            if (vertice.dato.equals(dato)) {
                return vertice;
            }
        }
        return null;
    }

    private ListaEnlazada<T> copiar(ListaEnlazada<T> origen) {
        ListaEnlazada<T> copia = new ListaEnlazada<>();
        for (T dato : origen) {
            copia.agregarFinal(dato);
        }
        return copia;
    }

    private int indiceDe(ListaEnlazada<T> lista, T dato) {
        int i = 0;
        for (T actual : lista) {
            if ((actual == null && dato == null) || (actual != null && actual.equals(dato))) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private int indiceMinimo(ListaEnlazada<T> pendientes, ListaEnlazada<T> nodos, ListaEnlazada<Double> distancias) {
        int mejorPendiente = 0;
        double mejor = Double.POSITIVE_INFINITY;
        int i = 0;
        for (T pendiente : pendientes) {
            double d = distancias.obtener(indiceDe(nodos, pendiente));
            if (d < mejor) {
                mejor = d;
                mejorPendiente = i;
            }
            i++;
        }
        return mejorPendiente;
    }

    @Override
    public Iterator<T> iterator() {
        ListaEnlazada<T> datos = new ListaEnlazada<>();
        for (Vertice<T> vertice : vertices) {
            datos.agregarFinal(vertice.dato);
        }
        return datos.iterator();
    }
}
