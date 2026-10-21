package sga.servicios;

import sga.comparadores.ComparadorPreparacion;
import sga.comparadores.ComparadoresApartamento;
import sga.estructuras.ArbolBinarioBusqueda;
import sga.estructuras.Cola;
import sga.estructuras.ColaPrioridad;
import sga.estructuras.Grafo;
import sga.estructuras.ListaCircular;
import sga.estructuras.ListaEnlazada;
import sga.excepciones.CapacidadExcedidaException;
import sga.excepciones.FechasInvalidasException;
import sga.excepciones.ReservaSolapadaException;
import sga.excepciones.TransicionEstadoInvalidaException;
import sga.modelo.Apartamento;
import sga.modelo.CanalReserva;
import sga.modelo.EstadoApartamento;
import sga.modelo.EstadoReserva;
import sga.modelo.Estancia;
import sga.modelo.Huesped;
import sga.modelo.NocheLiquidada;
import sga.modelo.NodoEdificio;
import sga.modelo.Recepcionista;
import sga.modelo.Reserva;
import sga.modelo.SolicitudExterna;
import sga.modelo.TareaPreparacion;
import sga.modelo.Temporada;
import sga.modelo.TipoTemporada;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;

/**
 * Fachada del SGA: un único alojamiento con todas las operaciones del enunciado.
 */
public class SistemaAlojamiento {
    private final String nombre;
    private final ArbolBinarioBusqueda<Apartamento> apartamentos = new ArbolBinarioBusqueda<>();
    private final ArbolBinarioBusqueda<Huesped> huespedes = new ArbolBinarioBusqueda<>();
    private final ListaEnlazada<Reserva> reservas = new ListaEnlazada<>();
    private final ListaEnlazada<String> conflictosCanal = new ListaEnlazada<>();
    private final ListaEnlazada<Temporada> temporadas = new ListaEnlazada<>();
    private final Cola<SolicitudExterna> canalExterno = new Cola<>();
    private final ColaPrioridad<TareaPreparacion> colaPreparacion = new ColaPrioridad<>(ComparadorPreparacion.crear());
    private final ColaPrioridad<Reserva> pendientesPorVencer = new ColaPrioridad<>(
            Comparator.comparing(Reserva::getVenceEn)
    );
    private final ListaCircular<Recepcionista> turnos = new ListaCircular<>();
    private final Grafo<NodoEdificio> mapa = new Grafo<>();
    private final ListaEnlazada<NodoEdificio> nodosMapa = new ListaEnlazada<>();

    public SistemaAlojamiento(String nombre) {
        this.nombre = nombre;
    }

    public void registrarApartamento(Apartamento apartamento) {
        apartamentos.insertar(apartamento);
    }

    public void registrarHuesped(Huesped huesped) {
        huespedes.insertar(huesped);
    }

    public void registrarRecepcionista(Recepcionista recepcionista) {
        turnos.agregar(recepcionista);
    }

    public void registrarTemporada(Temporada temporada) {
        temporadas.agregarFinal(temporada);
    }

    public void registrarNodoMapa(NodoEdificio nodo) {
        nodosMapa.agregarFinal(nodo);
        mapa.agregarVertice(nodo);
    }

    public void registrarPasillo(String idOrigen, String idDestino, double peso) {
        mapa.agregarArista(buscarNodo(idOrigen), buscarNodo(idDestino), peso);
    }

    public Huesped buscarHuesped(String documento) {
        return huespedes.buscar(new Huesped(documento, "", LocalDate.now(), "", ""));
    }

    public Apartamento buscarApartamento(String codigo) {
        return apartamentos.buscar(new Apartamento(codigo, "", "", 0, 0, 0, "", 0, 0, 0));
    }

    public ListaEnlazada<Apartamento> buscarDisponibles(Estancia estancia, int personas, Comparator<Apartamento> orden) {
        ListaEnlazada<Apartamento> resultado = new ListaEnlazada<>();
        for (Apartamento apto : apartamentos) {
            if (apto.getCapacidadMaxima() >= personas && apto.disponibleEn(estancia)
                    && apto.getEstado() != EstadoApartamento.FUERA_DE_SERVICIO) {
                insertarOrdenado(resultado, apto, orden);
            }
        }
        return resultado;
    }

    public ListaEnlazada<NocheLiquidada> cotizar(Apartamento apartamento, Reserva reserva) {
        return liquidar(apartamento, reserva.getEstancia(), reserva);
    }

    public ListaEnlazada<NocheLiquidada> liquidar(Apartamento apartamento, Estancia estancia, Reserva reserva) {
        ListaEnlazada<NocheLiquidada> desglose = new ListaEnlazada<>();
        liquidarRec(apartamento, estancia.iterator(), reserva, desglose);
        return desglose;
    }

    private void liquidarRec(Apartamento apartamento, java.util.Iterator<LocalDate> noches,
                             Reserva reserva, ListaEnlazada<NocheLiquidada> desglose) {
        if (!noches.hasNext()) {
            return;
        }
        LocalDate noche = noches.next();
        TipoTemporada temporada = temporadaDe(noche);
        int pagan = reserva == null ? 1 : reserva.personasQuePagan(noche);
        desglose.agregarFinal(new NocheLiquidada(noche, temporada, pagan, apartamento.tarifa(temporada)));
        liquidarRec(apartamento, noches, reserva, desglose);
    }

    public Reserva crearReserva(String documento, String codigoApartamento, Estancia estancia, CanalReserva canal)
            throws ReservaSolapadaException, CapacidadExcedidaException, TransicionEstadoInvalidaException {
        Huesped huesped = buscarHuesped(documento);
        Apartamento apartamento = buscarApartamento(codigoApartamento);
        if (huesped == null || apartamento == null) {
            throw new IllegalArgumentException("Huésped o apartamento no encontrado");
        }
        Reserva reserva = new Reserva(huesped, apartamento, estancia, canal);
        apartamento.agregarReserva(reserva);
        reservas.agregarFinal(reserva);
        huesped.agregarAlHistorial(reserva);
        if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
            pendientesPorVencer.encolar(reserva);
        }
        return reserva;
    }

    public void pagarAnticipo(Reserva reserva, double valorEstancia)
            throws TransicionEstadoInvalidaException {
        reserva.getFolio().registrarPago("Anticipo 30%", valorEstancia * 0.30);
        reserva.transicionarA(EstadoReserva.CONFIRMADA);
    }

    public double valorDevolucion(Reserva reserva) {
        double pagado = 0;
        for (sga.modelo.MovimientoFolio movimiento : reserva.getFolio().getMovimientos()) {
            if (movimiento.getValor() < 0) {
                pagado += -movimiento.getValor();
            }
        }
        return pagado * 0.80;
    }

    public void cancelar(Reserva reserva) throws TransicionEstadoInvalidaException {
        reserva.transicionarA(EstadoReserva.CANCELADA);
    }

    public void checkIn(Reserva reserva) throws TransicionEstadoInvalidaException {
        if (reserva.getApartamento().getEstado() != EstadoApartamento.PREPARADO) {
            throw new TransicionEstadoInvalidaException("El check-in solo procede si el apartamento está Preparado");
        }
        reserva.transicionarA(EstadoReserva.EN_CURSO);
        reserva.getApartamento().cambiarEstado(EstadoApartamento.OCUPADO);
    }

    public void checkOut(Reserva reserva) throws TransicionEstadoInvalidaException {
        reserva.transicionarA(EstadoReserva.FINALIZADA);
        Apartamento apto = reserva.getApartamento();
        apto.cambiarEstado(EstadoApartamento.PENDIENTE_PREPARACION);
        Reserva siguiente = apto.reservaConEntrada(LocalDate.now());
        boolean entradaHoy = siguiente != null;
        LocalTime hora = entradaHoy ? LocalTime.of(15, 0) : LocalTime.MAX;
        colaPreparacion.encolar(new TareaPreparacion(apto, entradaHoy, hora));
    }

    public TareaPreparacion despacharPreparacion() throws TransicionEstadoInvalidaException {
        TareaPreparacion tarea = colaPreparacion.desencolar();
        tarea.getApartamento().cambiarEstado(EstadoApartamento.EN_PREPARACION);
        tarea.getApartamento().cambiarEstado(EstadoApartamento.PREPARADO);
        return tarea;
    }

    public ListaEnlazada<NodoEdificio> rutaPreparacion() {
        NodoEdificio recepcion = buscarNodoPorTipo(NodoEdificio.Tipo.RECEPCION);
        if (recepcion == null || colaPreparacion.estaVacia()) {
            return new ListaEnlazada<>();
        }
        NodoEdificio destino = buscarNodoApartamento(colaPreparacion.frente().getApartamento().getCodigo());
        return mapa.dijkstra(recepcion, destino);
    }

    public String procesarSiguienteExterna() throws FechasInvalidasException {
        if (canalExterno.estaVacia()) {
            return "No hay solicitudes externas pendientes";
        }
        SolicitudExterna solicitud = canalExterno.desencolar();
        Apartamento apto = buscarApartamento(solicitud.getCodigoApartamento());
        if (apto == null || apto.haySolapamiento(solicitud.getEstancia())) {
            String conflicto = "Conflicto canal externo: " + solicitud;
            conflictosCanal.agregarFinal(conflicto);
            return conflicto;
        }
        try {
            Reserva reserva = crearReserva(solicitud.getDocumentoTitular(), solicitud.getCodigoApartamento(),
                    solicitud.getEstancia(), CanalReserva.EXTERNA);
            return "Solicitud aceptada: " + reserva;
        } catch (Exception e) {
            String conflicto = "Conflicto canal externo: " + e.getMessage();
            conflictosCanal.agregarFinal(conflicto);
            return conflicto;
        }
    }

    public void encolarExterna(SolicitudExterna solicitud) {
        canalExterno.encolar(solicitud);
    }

    public int cancelarPendientesVencidas() {
        int canceladas = 0;
        ColaPrioridad<Reserva> vigentes = new ColaPrioridad<>(Comparator.comparing(Reserva::getVenceEn));
        while (!pendientesPorVencer.estaVacia()) {
            Reserva reserva = pendientesPorVencer.desencolar();
            if (reserva.getEstado() == EstadoReserva.PENDIENTE && !reserva.getVenceEn().isAfter(LocalDateTime.now())) {
                try {
                    reserva.transicionarA(EstadoReserva.CANCELADA);
                    canceladas++;
                } catch (TransicionEstadoInvalidaException ignored) {
                }
            } else if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
                vigentes.encolar(reserva);
            }
        }
        while (!vigentes.estaVacia()) {
            pendientesPorVencer.encolar(vigentes.desencolar());
        }
        return canceladas;
    }

    /**
     * Backtracking: combinación de apartamentos que aloja al grupo con el menor número de unidades.
     */
    public ListaEnlazada<Apartamento> asignarGrupoGrande(Estancia estancia, int personas) {
        ListaEnlazada<Apartamento> candidatos = buscarDisponibles(estancia, 1, ComparadoresApartamento.porCapacidad().reversed());
        ListaEnlazada<Apartamento> mejor = new ListaEnlazada<>();
        backtrackGrupo(candidatos, 0, personas, new ListaEnlazada<>(), mejor);
        return mejor;
    }

    private void backtrackGrupo(ListaEnlazada<Apartamento> candidatos, int indice, int faltan,
                                ListaEnlazada<Apartamento> actual, ListaEnlazada<Apartamento> mejor) {
        if (faltan <= 0) {
            if (mejor.estaVacia() || actual.tamano() < mejor.tamano()) {
                mejor.limpiar();
                for (Apartamento apto : actual) {
                    mejor.agregarFinal(apto);
                }
            }
            return;
        }
        if (indice >= candidatos.tamano()) {
            return;
        }
        if (!mejor.estaVacia() && actual.tamano() >= mejor.tamano()) {
            return;
        }
        Apartamento candidato = candidatos.obtener(indice);
        actual.agregarFinal(candidato);
        backtrackGrupo(candidatos, indice + 1, faltan - candidato.getCapacidadMaxima(), actual, mejor);
        actual.eliminar(actual.tamano() - 1);
        backtrackGrupo(candidatos, indice + 1, faltan, actual, mejor);
    }

    public ListaEnlazada<Apartamento> dfsDesde(String idNodo) {
        ListaEnlazada<Apartamento> aptos = new ListaEnlazada<>();
        for (NodoEdificio nodo : mapa.dfs(buscarNodo(idNodo))) {
            if (nodo.getApartamento() != null) {
                aptos.agregarFinal(nodo.getApartamento());
            }
        }
        return aptos;
    }

    public boolean pasilloCierraAisla(String idPasilloA, String idPasilloB, String idObjetivo) {
        Grafo<NodoEdificio> reducido = new Grafo<>();
        for (NodoEdificio nodo : nodosMapa) {
            reducido.agregarVertice(nodo);
        }
        NodoEdificio a = buscarNodo(idPasilloA);
        NodoEdificio b = buscarNodo(idPasilloB);
        for (NodoEdificio origen : nodosMapa) {
            for (Grafo.Arista<NodoEdificio> arista : mapa.adyacentes(origen)) {
                boolean esPasilloCerrado = (origen.equals(a) && arista.getDestino().equals(b))
                        || (origen.equals(b) && arista.getDestino().equals(a));
                if (!esPasilloCerrado && origen.getId().compareTo(arista.getDestino().getId()) < 0) {
                    reducido.agregarArista(origen, arista.getDestino(), arista.getPeso());
                }
            }
        }
        NodoEdificio recepcion = buscarNodoPorTipo(NodoEdificio.Tipo.RECEPCION);
        return !reducido.alcanzable(recepcion, buscarNodo(idObjetivo));
    }

    public Recepcionista turnoActual() {
        return turnos.turnoActual();
    }

    public Recepcionista avanzarTurno() {
        return turnos.avanzarTurno();
    }

    public TipoTemporada temporadaDe(LocalDate noche) {
        for (Temporada temporada : temporadas) {
            if (temporada.cubre(noche)) {
                return temporada.getTipo();
            }
        }
        return TipoTemporada.BAJA;
    }

    public String getNombre() {
        return nombre;
    }

    public ArbolBinarioBusqueda<Apartamento> getApartamentos() {
        return apartamentos;
    }

    public ArbolBinarioBusqueda<Huesped> getHuespedes() {
        return huespedes;
    }

    public ListaEnlazada<Reserva> getReservas() {
        return reservas;
    }

    public ListaEnlazada<String> getConflictosCanal() {
        return conflictosCanal;
    }

    public Cola<SolicitudExterna> getCanalExterno() {
        return canalExterno;
    }

    public ColaPrioridad<TareaPreparacion> getColaPreparacion() {
        return colaPreparacion;
    }

    public Grafo<NodoEdificio> getMapa() {
        return mapa;
    }

    public ListaEnlazada<NodoEdificio> getNodosMapa() {
        return nodosMapa;
    }

    public ListaCircular<Recepcionista> getTurnos() {
        return turnos;
    }

    private NodoEdificio buscarNodo(String id) {
        for (NodoEdificio nodo : nodosMapa) {
            if (nodo.getId().equals(id)) {
                return nodo;
            }
        }
        return null;
    }

    private NodoEdificio buscarNodoPorTipo(NodoEdificio.Tipo tipo) {
        for (NodoEdificio nodo : nodosMapa) {
            if (nodo.getTipo() == tipo) {
                return nodo;
            }
        }
        return null;
    }

    private NodoEdificio buscarNodoApartamento(String codigo) {
        for (NodoEdificio nodo : nodosMapa) {
            if (nodo.getApartamento() != null && nodo.getApartamento().getCodigo().equals(codigo)) {
                return nodo;
            }
        }
        return null;
    }

    private void insertarOrdenado(ListaEnlazada<Apartamento> lista, Apartamento apto, Comparator<Apartamento> orden) {
        ListaEnlazada<Apartamento> temporal = new ListaEnlazada<>();
        boolean puesto = false;
        for (Apartamento actual : lista) {
            if (!puesto && orden.compare(apto, actual) <= 0) {
                temporal.agregarFinal(apto);
                puesto = true;
            }
            temporal.agregarFinal(actual);
        }
        if (!puesto) {
            temporal.agregarFinal(apto);
        }
        lista.limpiar();
        for (Apartamento actual : temporal) {
            lista.agregarFinal(actual);
        }
    }
}
