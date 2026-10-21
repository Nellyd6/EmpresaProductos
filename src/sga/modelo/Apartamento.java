package sga.modelo;

import sga.estructuras.ArbolBinarioBusqueda;
import sga.estructuras.ListaEnlazada;
import sga.excepciones.ReservaSolapadaException;
import sga.excepciones.TransicionEstadoInvalidaException;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Apartamento comparable por código. Sus reservas viven en un BST ordenado por fecha de entrada.
 */
public class Apartamento implements Comparable<Apartamento> {
    private final String codigo;
    private String nombre;
    private String bloque;
    private int piso;
    private int dormitorios;
    private int capacidadMaxima;
    private String caracteristicas;
    private double tarifaBaja;
    private double tarifaMedia;
    private double tarifaAlta;
    private EstadoApartamento estado = EstadoApartamento.PREPARADO;
    private String motivoFueraServicio;
    private final ArbolBinarioBusqueda<Reserva> reservas = new ArbolBinarioBusqueda<>(
            Comparator.comparing((Reserva r) -> r.getEstancia().getEntrada())
                    .thenComparing(Reserva::getId)
    );

    public Apartamento(String codigo, String nombre, String bloque, int piso, int dormitorios,
                       int capacidadMaxima, String caracteristicas,
                       double tarifaBaja, double tarifaMedia, double tarifaAlta) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.bloque = bloque;
        this.piso = piso;
        this.dormitorios = dormitorios;
        this.capacidadMaxima = capacidadMaxima;
        this.caracteristicas = caracteristicas;
        this.tarifaBaja = tarifaBaja;
        this.tarifaMedia = tarifaMedia;
        this.tarifaAlta = tarifaAlta;
    }

    public void agregarReserva(Reserva reserva) throws ReservaSolapadaException {
        if (haySolapamiento(reserva.getEstancia())) {
            throw new ReservaSolapadaException("El apartamento " + codigo + " ya tiene una reserva activa en esas noches");
        }
        reservas.insertar(reserva);
    }

    /**
     * Recorre el árbol de reservas de forma recursiva para detectar choques de noches.
     */
    public boolean haySolapamiento(Estancia estancia) {
        return reservas.algunoCumple(existente ->
                existente.esActiva() && existente.getEstancia().solapaCon(estancia));
    }

    public boolean disponibleEn(Estancia estancia) {
        if (estado == EstadoApartamento.FUERA_DE_SERVICIO) {
            return false;
        }
        return !haySolapamiento(estancia);
    }

    public double tarifa(TipoTemporada temporada) {
        switch (temporada) {
            case MEDIA:
                return tarifaMedia;
            case ALTA:
                return tarifaAlta;
            default:
                return tarifaBaja;
        }
    }

    public void cambiarEstado(EstadoApartamento nuevo) throws TransicionEstadoInvalidaException {
        if (estado == EstadoApartamento.FUERA_DE_SERVICIO && nuevo != EstadoApartamento.PREPARADO) {
            throw new TransicionEstadoInvalidaException("Un apartamento fuera de servicio solo puede pasar a Preparado");
        }
        this.estado = nuevo;
        if (nuevo != EstadoApartamento.FUERA_DE_SERVICIO) {
            this.motivoFueraServicio = null;
        }
    }

    public void ponerFueraDeServicio(String motivo) {
        this.estado = EstadoApartamento.FUERA_DE_SERVICIO;
        this.motivoFueraServicio = motivo;
    }

    public Reserva reservaConEntrada(LocalDate fecha) {
        for (Reserva reserva : reservas) {
            if (reserva.esActiva() && reserva.getEstancia().getEntrada().equals(fecha)) {
                return reserva;
            }
        }
        return null;
    }

    public int cantidadReservas() {
        return reservas.tamano();
    }

    public ListaEnlazada<Reserva> listarReservas() {
        ListaEnlazada<Reserva> lista = new ListaEnlazada<>();
        reservas.inorden(lista);
        return lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getBloque() {
        return bloque;
    }

    public int getPiso() {
        return piso;
    }

    public int getDormitorios() {
        return dormitorios;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public EstadoApartamento getEstado() {
        return estado;
    }

    public String getMotivoFueraServicio() {
        return motivoFueraServicio;
    }

    public ArbolBinarioBusqueda<Reserva> getReservas() {
        return reservas;
    }

    @Override
    public int compareTo(Apartamento otro) {
        return this.codigo.compareToIgnoreCase(otro.codigo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Apartamento)) {
            return false;
        }
        return codigo.equalsIgnoreCase(((Apartamento) o).codigo);
    }

    @Override
    public int hashCode() {
        return codigo.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + estado + ")";
    }
}
