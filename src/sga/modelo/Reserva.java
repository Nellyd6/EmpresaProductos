package sga.modelo;

import sga.estructuras.ListaEnlazada;
import sga.excepciones.CapacidadExcedidaException;
import sga.excepciones.TransicionEstadoInvalidaException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reserva implements Comparable<Reserva> {
    private final String id;
    private final Huesped titular;
    private final Apartamento apartamento;
    private final Estancia estancia;
    private final CanalReserva canal;
    private final ListaEnlazada<Ocupante> ocupantes = new ListaEnlazada<>();
    private final Folio folio = new Folio();
    private EstadoReserva estado;
    private final LocalDateTime creadaEn;
    private final LocalDateTime venceEn;
    private String motivoConflicto;

    public Reserva(Huesped titular, Apartamento apartamento, Estancia estancia, CanalReserva canal)
            throws CapacidadExcedidaException {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.titular = titular;
        this.apartamento = apartamento;
        this.estancia = estancia;
        this.canal = canal;
        this.estado = canal == CanalReserva.PORTAL ? EstadoReserva.PENDIENTE : EstadoReserva.CONFIRMADA;
        this.creadaEn = LocalDateTime.now();
        this.venceEn = this.creadaEn.plusHours(24);
        ocupantes.agregarFinal(new Ocupante(titular.getNombre(), titular.getFechaNacimiento()));
        validarCapacidad();
    }

    public void agregarOcupante(Ocupante ocupante) throws CapacidadExcedidaException {
        ocupantes.agregarFinal(ocupante);
        validarCapacidad();
    }

    private void validarCapacidad() throws CapacidadExcedidaException {
        if (ocupantes.tamano() > apartamento.getCapacidadMaxima()) {
            throw new CapacidadExcedidaException(
                    "El grupo (" + ocupantes.tamano() + ") supera la capacidad de " + apartamento.getCodigo());
        }
    }

    public boolean esActiva() {
        return estado == EstadoReserva.PENDIENTE
                || estado == EstadoReserva.CONFIRMADA
                || estado == EstadoReserva.EN_CURSO;
    }

    public void transicionarA(EstadoReserva nuevo) throws TransicionEstadoInvalidaException {
        if (!transicionPermitida(estado, nuevo)) {
            throw new TransicionEstadoInvalidaException("No se puede pasar de " + estado + " a " + nuevo);
        }
        this.estado = nuevo;
    }

    private boolean transicionPermitida(EstadoReserva actual, EstadoReserva nuevo) {
        if (actual == EstadoReserva.PENDIENTE) {
            return nuevo == EstadoReserva.CONFIRMADA || nuevo == EstadoReserva.CANCELADA;
        }
        if (actual == EstadoReserva.CONFIRMADA) {
            return nuevo == EstadoReserva.EN_CURSO || nuevo == EstadoReserva.CANCELADA || nuevo == EstadoReserva.NO_SHOW;
        }
        if (actual == EstadoReserva.EN_CURSO) {
            return nuevo == EstadoReserva.FINALIZADA;
        }
        return false;
    }

    public int personasQuePagan(java.time.LocalDate noche) {
        int pagan = 0;
        for (Ocupante ocupante : ocupantes) {
            if (!ocupante.esMenorDeSeis(noche)) {
                pagan++;
            }
        }
        return pagan;
    }

    public String getId() {
        return id;
    }

    public Huesped getTitular() {
        return titular;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public Estancia getEstancia() {
        return estancia;
    }

    public CanalReserva getCanal() {
        return canal;
    }

    public ListaEnlazada<Ocupante> getOcupantes() {
        return ocupantes;
    }

    public Folio getFolio() {
        return folio;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public LocalDateTime getVenceEn() {
        return venceEn;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    public String getMotivoConflicto() {
        return motivoConflicto;
    }

    public void setMotivoConflicto(String motivoConflicto) {
        this.motivoConflicto = motivoConflicto;
    }

    @Override
    public int compareTo(Reserva otra) {
        int porFecha = this.estancia.getEntrada().compareTo(otra.estancia.getEntrada());
        if (porFecha != 0) {
            return porFecha;
        }
        return this.id.compareTo(otra.id);
    }

    @Override
    public String toString() {
        return id + " | " + titular.getNombre() + " | " + apartamento.getCodigo() + " | " + estancia + " | " + estado;
    }
}
