package sga.modelo;

import java.time.LocalDate;
import java.time.Period;

public class Ocupante {
    private final String nombre;
    private final LocalDate fechaNacimiento;

    public Ocupante(String nombre, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean esMenorDeSeis(LocalDate fechaReferencia) {
        return Period.between(fechaNacimiento, fechaReferencia).getYears() < 6;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
}
