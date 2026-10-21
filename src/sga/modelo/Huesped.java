package sga.modelo;

import sga.estructuras.Conjunto;
import sga.estructuras.ListaEnlazada;

import java.time.LocalDate;

/**
 * Huésped comparable por documento, para guardarlo en el árbol de búsqueda.
 */
public class Huesped implements Comparable<Huesped> {
    private final String documento;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String correo;
    private String rutaFotografia;
    private final Conjunto<String> favoritos = new Conjunto<>();
    private final ListaEnlazada<Reserva> historial = new ListaEnlazada<>();

    public Huesped(String documento, String nombre, LocalDate fechaNacimiento, String telefono, String correo) {
        this.documento = documento;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.correo = correo;
    }

    public boolean agregarFavorito(String codigoApartamento) {
        return favoritos.agregar(codigoApartamento);
    }

    public void agregarAlHistorial(Reserva reserva) {
        historial.agregarFinal(reserva);
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRutaFotografia() {
        return rutaFotografia;
    }

    public void setRutaFotografia(String rutaFotografia) {
        this.rutaFotografia = rutaFotografia;
    }

    public Conjunto<String> getFavoritos() {
        return favoritos;
    }

    public ListaEnlazada<Reserva> getHistorial() {
        return historial;
    }

    @Override
    public int compareTo(Huesped otro) {
        return this.documento.compareToIgnoreCase(otro.documento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Huesped)) {
            return false;
        }
        return documento.equalsIgnoreCase(((Huesped) o).documento);
    }

    @Override
    public int hashCode() {
        return documento.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return nombre + " (" + documento + ")";
    }
}
