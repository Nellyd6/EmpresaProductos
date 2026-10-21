package sga.persistencia;

import sga.excepciones.FechasInvalidasException;
import sga.modelo.Apartamento;
import sga.modelo.CanalReserva;
import sga.modelo.Estancia;
import sga.modelo.Huesped;
import sga.modelo.NodoEdificio;
import sga.modelo.Recepcionista;
import sga.modelo.SolicitudExterna;
import sga.modelo.Temporada;
import sga.modelo.TipoTemporada;
import sga.servicios.SistemaAlojamiento;

import java.time.LocalDate;

/**
 * Carga un escenario de prueba inicial (botón de la GUI).
 */
public class CargadorEscenario {

    public static SistemaAlojamiento cargar() {
        SistemaAlojamiento sga = new SistemaAlojamiento("Aparta-Suites UQ");
        sga.registrarTemporada(new Temporada(TipoTemporada.BAJA, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 14)));
        sga.registrarTemporada(new Temporada(TipoTemporada.ALTA, LocalDate.of(2026, 6, 15), LocalDate.of(2026, 8, 15)));
        sga.registrarTemporada(new Temporada(TipoTemporada.MEDIA, LocalDate.of(2026, 8, 16), LocalDate.of(2026, 12, 15)));
        sga.registrarTemporada(new Temporada(TipoTemporada.ALTA, LocalDate.of(2026, 12, 16), LocalDate.of(2026, 12, 31)));

        Apartamento a101 = new Apartamento("A101", "Suite Jardín", "A", 1, 1, 2, "Vista jardín, cocina", 80000, 110000, 150000);
        Apartamento a102 = new Apartamento("A102", "Suite Familiar", "A", 1, 2, 4, "Dos habitaciones", 120000, 160000, 210000);
        Apartamento b201 = new Apartamento("B201", "Loft Valle", "B", 2, 1, 3, "Balcón al valle", 100000, 140000, 190000);
        Apartamento b202 = new Apartamento("B202", "Penthouse", "B", 2, 3, 6, "Terraza, jacuzzi", 180000, 240000, 320000);
        sga.registrarApartamento(a101);
        sga.registrarApartamento(a102);
        sga.registrarApartamento(b201);
        sga.registrarApartamento(b202);

        sga.registrarHuesped(new Huesped("1001", "Ana Pérez", LocalDate.of(1995, 4, 12), "3001112233", "ana@mail.com"));
        sga.registrarHuesped(new Huesped("1002", "Carlos Ruiz", LocalDate.of(1988, 9, 3), "3002223344", "carlos@mail.com"));
        sga.registrarHuesped(new Huesped("1003", "Luisa Gómez", LocalDate.of(2001, 1, 20), "3003334455", "luisa@mail.com"));

        sga.registrarRecepcionista(new Recepcionista("R1", "Marta"));
        sga.registrarRecepcionista(new Recepcionista("R2", "Julián"));
        sga.registrarRecepcionista(new Recepcionista("R3", "Sofía"));

        NodoEdificio recepcion = new NodoEdificio("REC", NodoEdificio.Tipo.RECEPCION, "Recepción", 80, 220);
        NodoEdificio esc1 = new NodoEdificio("ESC1", NodoEdificio.Tipo.ESCALERA, "Escalera 1", 220, 220);
        NodoEdificio aseo = new NodoEdificio("ASEO", NodoEdificio.Tipo.ASEO, "Cuarto aseo", 220, 80);
        NodoEdificio n101 = new NodoEdificio("N101", NodoEdificio.Tipo.APARTAMENTO, "A101", 360, 300);
        NodoEdificio n102 = new NodoEdificio("N102", NodoEdificio.Tipo.APARTAMENTO, "A102", 520, 300);
        NodoEdificio n201 = new NodoEdificio("N201", NodoEdificio.Tipo.APARTAMENTO, "B201", 360, 120);
        NodoEdificio n202 = new NodoEdificio("N202", NodoEdificio.Tipo.APARTAMENTO, "B202", 520, 120);
        n101.setApartamento(a101);
        n102.setApartamento(a102);
        n201.setApartamento(b201);
        n202.setApartamento(b202);
        sga.registrarNodoMapa(recepcion);
        sga.registrarNodoMapa(esc1);
        sga.registrarNodoMapa(aseo);
        sga.registrarNodoMapa(n101);
        sga.registrarNodoMapa(n102);
        sga.registrarNodoMapa(n201);
        sga.registrarNodoMapa(n202);
        sga.registrarPasillo("REC", "ESC1", 20);
        sga.registrarPasillo("ESC1", "N101", 12);
        sga.registrarPasillo("N101", "N102", 10);
        sga.registrarPasillo("ESC1", "ASEO", 8);
        sga.registrarPasillo("ESC1", "N201", 15);
        sga.registrarPasillo("N201", "N202", 10);

        try {
            Estancia e1 = new Estancia(LocalDate.now().plusDays(2), LocalDate.now().plusDays(5));
            sga.crearReserva("1001", "A101", e1, CanalReserva.DIRECTA);
            sga.encolarExterna(new SolicitudExterna("Booking", "1002", "A101", e1, 2));
            sga.encolarExterna(new SolicitudExterna("Airbnb", "1003", "B201",
                    new Estancia(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12)), 2));
        } catch (FechasInvalidasException | RuntimeException ex) {
            System.err.println("No se pudo cargar reservas de ejemplo: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("No se pudo cargar reservas de ejemplo: " + ex.getMessage());
        }
        return sga;
    }
}
