package sga.gui;

import sga.comparadores.ComparadoresApartamento;
import sga.estructuras.ListaEnlazada;
import sga.excepciones.FechasInvalidasException;
import sga.modelo.Apartamento;
import sga.modelo.CanalReserva;
import sga.modelo.Estancia;
import sga.modelo.Huesped;
import sga.modelo.NocheLiquidada;
import sga.modelo.Reserva;
import sga.modelo.TareaPreparacion;
import sga.modelo.TipoTemporada;
import sga.persistencia.CargadorEscenario;
import sga.servicios.SistemaAlojamiento;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Comparator;

public class VentanaPrincipal extends JFrame {
    private SistemaAlojamiento sistema;
    private final JPanel tablero = new JPanel(new GridLayout(0, 2, 8, 8));
    private final JTextArea areaReservas = new JTextArea();
    private final JTextArea areaCalendario = new JTextArea();
    private final JTextArea areaUsuario = new JTextArea();
    private final JTextArea areaEstadisticas = new JTextArea();
    private final DefaultListModel<String> modeloPreparacion = new DefaultListModel<>();
    private PanelMapa panelMapa;

    public VentanaPrincipal() {
        super("SGA — Aparta-Suites UQ");
        this.sistema = CargadorEscenario.cargar();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Inicio", crearInicio());
        pestanas.addTab("Reservas", crearReservas());
        pestanas.addTab("Calendario", crearCalendario());
        pestanas.addTab("Huésped", crearUsuario());
        pestanas.addTab("Preparación", crearPreparacion());
        pestanas.addTab("Mapa", crearMapa());
        pestanas.addTab("Estadísticas", crearEstadisticas());
        add(pestanas);
        refrescarTodo();
    }

    private JPanel crearInicio() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JLabel titulo = new JLabel("Tablero de apartamentos — " + sistema.getNombre(), SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        JButton cargar = new JButton("Cargar escenario de prueba");
        cargar.addActionListener(e -> {
            sistema = CargadorEscenario.cargar();
            panelMapa = new PanelMapa(sistema);
            refrescarTodo();
        });
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablero), BorderLayout.CENTER);
        panel.add(cargar, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel crearReservas() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField doc = new JTextField("1003", 8);
        JTextField codigo = new JTextField("A102", 6);
        JTextField entrada = new JTextField(LocalDate.now().plusDays(7).toString(), 10);
        JTextField salida = new JTextField(LocalDate.now().plusDays(10).toString(), 10);
        JTextField personas = new JTextField("2", 3);
        JComboBox<String> orden = new JComboBox<>(new String[]{"precio", "capacidad", "dormitorios"});
        JButton buscar = new JButton("Buscar disponibles");
        JButton crearDirecta = new JButton("Reserva directa");
        JButton crearPortal = new JButton("Reserva portal + anticipo");
        JButton externa = new JButton("Procesar canal externo");
        form.add(new JLabel("Doc:"));
        form.add(doc);
        form.add(new JLabel("Apto:"));
        form.add(codigo);
        form.add(new JLabel("Entrada:"));
        form.add(entrada);
        form.add(new JLabel("Salida:"));
        form.add(salida);
        form.add(new JLabel("Pers:"));
        form.add(personas);
        form.add(orden);
        form.add(buscar);
        form.add(crearDirecta);
        form.add(crearPortal);
        form.add(externa);
        areaReservas.setEditable(false);
        buscar.addActionListener(e -> buscarDisponibles(entrada.getText(), salida.getText(), personas.getText(), (String) orden.getSelectedItem()));
        crearDirecta.addActionListener(e -> crearReserva(doc.getText(), codigo.getText(), entrada.getText(), salida.getText(), CanalReserva.DIRECTA, false));
        crearPortal.addActionListener(e -> crearReserva(doc.getText(), codigo.getText(), entrada.getText(), salida.getText(), CanalReserva.PORTAL, true));
        externa.addActionListener(e -> {
            try {
                JOptionPane.showMessageDialog(this, sistema.procesarSiguienteExterna());
                refrescarTodo();
            } catch (FechasInvalidasException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaReservas), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearCalendario() {
        JPanel panel = new JPanel(new BorderLayout());
        areaCalendario.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaCalendario.setEditable(false);
        panel.add(new JScrollPane(areaCalendario), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearUsuario() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel norte = new JPanel();
        JTextField doc = new JTextField("1001", 10);
        JButton ver = new JButton("Ver panel del huésped");
        JButton fav = new JButton("Agregar A102 a favoritos");
        norte.add(new JLabel("Documento:"));
        norte.add(doc);
        norte.add(ver);
        norte.add(fav);
        areaUsuario.setEditable(false);
        ver.addActionListener(e -> mostrarUsuario(doc.getText()));
        fav.addActionListener(e -> {
            Huesped h = sistema.buscarHuesped(doc.getText().trim());
            if (h != null) {
                h.agregarFavorito("A102");
                mostrarUsuario(doc.getText());
            }
        });
        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaUsuario), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPreparacion() {
        JPanel panel = new JPanel(new BorderLayout());
        JList<String> lista = new JList<>(modeloPreparacion);
        JButton despachar = new JButton("Despachar siguiente (prioridad)");
        JButton ruta = new JButton("Calcular ruta más corta");
        JLabel turno = new JLabel();
        despachar.addActionListener(e -> {
            try {
                TareaPreparacion t = sistema.despacharPreparacion();
                JOptionPane.showMessageDialog(this, "Preparado: " + t.getApartamento().getCodigo());
                refrescarTodo();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        ruta.addActionListener(e -> {
            panelMapa.setRuta(sistema.rutaPreparacion());
            JOptionPane.showMessageDialog(this, "Ruta dibujada en la pestaña Mapa");
        });
        JPanel sur = new JPanel();
        sur.add(despachar);
        sur.add(ruta);
        sur.add(new JButton("Avanzar turno") {{
            addActionListener(ev -> {
                sistema.avanzarTurno();
                turno.setText("Turno: " + sistema.turnoActual());
            });
        }});
        turno.setText("Turno: " + sistema.turnoActual());
        sur.add(turno);
        panel.add(new JScrollPane(lista), BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearMapa() {
        panelMapa = new PanelMapa(sistema);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Verde Preparado · Azul Ocupado · Amarillo preparación · Rojo fuera de servicio",
                SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(panelMapa, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout());
        areaEstadisticas.setEditable(false);
        panel.add(new JScrollPane(areaEstadisticas), BorderLayout.CENTER);
        return panel;
    }

    private void buscarDisponibles(String entrada, String salida, String personasTxt, String criterio) {
        try {
            Estancia estancia = new Estancia(LocalDate.parse(entrada.trim()), LocalDate.parse(salida.trim()));
            int personas = Integer.parseInt(personasTxt.trim());
            Comparator<Apartamento> orden;
            switch (criterio) {
                case "capacidad":
                    orden = ComparadoresApartamento.porCapacidad();
                    break;
                case "dormitorios":
                    orden = ComparadoresApartamento.porDormitorios();
                    break;
                default:
                    orden = ComparadoresApartamento.porPrecio(TipoTemporada.BAJA);
            }
            ListaEnlazada<Apartamento> lista = sistema.buscarDisponibles(estancia, personas, orden);
            StringBuilder sb = new StringBuilder("Disponibles:\n");
            for (Apartamento apto : lista) {
                sb.append(" - ").append(apto).append(" cap=").append(apto.getCapacidadMaxima())
                        .append(" tarifa baja=").append(apto.tarifa(TipoTemporada.BAJA)).append("\n");
            }
            areaReservas.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void crearReserva(String doc, String codigo, String entrada, String salida, CanalReserva canal, boolean anticipo) {
        try {
            Estancia estancia = new Estancia(LocalDate.parse(entrada.trim()), LocalDate.parse(salida.trim()));
            Reserva reserva = sistema.crearReserva(doc.trim(), codigo.trim(), estancia, canal);
            if (anticipo) {
                ListaEnlazada<NocheLiquidada> desglose = sistema.liquidar(reserva.getApartamento(), estancia, reserva);
                double total = 0;
                StringBuilder sb = new StringBuilder("Desglose:\n");
                for (NocheLiquidada noche : desglose) {
                    sb.append(noche).append("\n");
                    total += noche.getSubtotal();
                }
                sistema.pagarAnticipo(reserva, total);
                sb.append("Total: ").append(total).append("\nAnticipo 30% cobrado. Reserva: ").append(reserva);
                areaReservas.setText(sb.toString());
            } else {
                areaReservas.setText("Reserva creada:\n" + reserva);
            }
            refrescarTodo();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void mostrarUsuario(String documento) {
        Huesped h = sistema.buscarHuesped(documento.trim());
        if (h == null) {
            areaUsuario.setText("Huésped no encontrado");
            return;
        }
        StringBuilder sb = new StringBuilder(h + "\nFavoritos:\n");
        for (String fav : h.getFavoritos()) {
            sb.append(" - ").append(fav).append("\n");
        }
        sb.append("Historial:\n");
        for (Reserva r : h.getHistorial()) {
            sb.append(" - ").append(r).append(" saldo folio=").append(r.getFolio().saldo()).append("\n");
        }
        areaUsuario.setText(sb.toString());
    }

    private void refrescarTodo() {
        tablero.removeAll();
        for (Apartamento apto : sistema.getApartamentos()) {
            JButton boton = new JButton("<html><b>" + apto.getCodigo() + "</b><br>" + apto.getNombre()
                    + "<br>" + apto.getEstado() + "</html>");
            boton.setBackground(colorEstado(apto));
            boton.setOpaque(true);
            tablero.add(boton);
        }
        tablero.revalidate();
        tablero.repaint();

        StringBuilder cal = new StringBuilder(String.format("%-8s", "Apto"));
        LocalDate inicio = LocalDate.now();
        for (int i = 0; i < 14; i++) {
            cal.append(String.format("%6s", inicio.plusDays(i).getDayOfMonth()));
        }
        cal.append("\n");
        for (Apartamento apto : sistema.getApartamentos()) {
            cal.append(String.format("%-8s", apto.getCodigo()));
            for (int i = 0; i < 14; i++) {
                LocalDate noche = inicio.plusDays(i);
                boolean ocupada = false;
                try {
                    ocupada = apto.haySolapamiento(new Estancia(noche, noche.plusDays(1)));
                } catch (FechasInvalidasException ignored) {
                }
                cal.append(String.format("%6s", ocupada ? "██" : "·"));
            }
            cal.append("\n");
        }
        areaCalendario.setText(cal.toString());

        modeloPreparacion.clear();
        for (TareaPreparacion tarea : sistema.getColaPreparacion()) {
            modeloPreparacion.addElement(tarea.toString());
        }
        if (panelMapa != null) {
            panelMapa.repaint();
        }

        StringBuilder est = new StringBuilder();
        est.append("Turno recepción: ").append(sistema.turnoActual()).append("\n");
        est.append("Reservas por canal:\n");
        int portal = 0, directa = 0, externa = 0, canceladas = 0;
        for (Reserva r : sistema.getReservas()) {
            if (r.getCanal() == CanalReserva.PORTAL) portal++;
            if (r.getCanal() == CanalReserva.DIRECTA) directa++;
            if (r.getCanal() == CanalReserva.EXTERNA) externa++;
            if (r.getEstado().name().contains("CANCEL")) canceladas++;
        }
        est.append(" Portal=").append(portal).append(" Directa=").append(directa)
                .append(" Externa=").append(externa).append(" Canceladas=").append(canceladas).append("\n");
        est.append("Conflictos de canal:\n");
        for (String c : sistema.getConflictosCanal()) {
            est.append(" - ").append(c).append("\n");
        }
        areaEstadisticas.setText(est.toString());
    }

    private Color colorEstado(Apartamento apto) {
        switch (apto.getEstado()) {
            case PREPARADO:
                return new Color(165, 214, 167);
            case OCUPADO:
                return new Color(144, 202, 249);
            case PENDIENTE_PREPARACION:
            case EN_PREPARACION:
                return new Color(255, 241, 118);
            default:
                return new Color(239, 154, 154);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
