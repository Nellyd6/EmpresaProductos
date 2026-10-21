package sga.modelo;

import sga.estructuras.ListaEnlazada;
import sga.estructuras.Pila;
import sga.excepciones.EstructuraVaciaException;

/**
 * Cuenta de la reserva. Los movimientos nunca se borran: deshacer
 * registra el movimiento contrario usando una pila.
 */
public class Folio {
    private final ListaEnlazada<MovimientoFolio> movimientos = new ListaEnlazada<>();
    private final Pila<MovimientoFolio> deshacer = new Pila<>();

    public void registrarCargo(String descripcion, double valor) {
        registrar(new MovimientoFolio(MovimientoFolio.Tipo.CARGO, descripcion, valor, false));
    }

    public void registrarPago(String descripcion, double valor) {
        registrar(new MovimientoFolio(MovimientoFolio.Tipo.PAGO, descripcion, -valor, false));
    }

    public void deshacerUltimaOperacion() {
        if (deshacer.estaVacia()) {
            throw new EstructuraVaciaException("No hay operaciones para deshacer");
        }
        MovimientoFolio ultimo = deshacer.desapilar();
        MovimientoFolio reverso = ultimo.contrario();
        movimientos.agregarFinal(reverso);
    }

    public double saldo() {
        double total = 0;
        for (MovimientoFolio movimiento : movimientos) {
            total += movimiento.getValor();
        }
        return total;
    }

    public ListaEnlazada<MovimientoFolio> getMovimientos() {
        return movimientos;
    }

    private void registrar(MovimientoFolio movimiento) {
        movimientos.agregarFinal(movimiento);
        deshacer.apilar(movimiento);
    }
}
