package sga.gui;

import sga.estructuras.Grafo;
import sga.estructuras.ListaEnlazada;
import sga.modelo.EstadoApartamento;
import sga.modelo.NodoEdificio;
import sga.servicios.SistemaAlojamiento;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PanelMapa extends JPanel {
    private final SistemaAlojamiento sistema;
    private ListaEnlazada<NodoEdificio> ruta = new ListaEnlazada<>();

    public PanelMapa(SistemaAlojamiento sistema) {
        this.sistema = sistema;
        setBackground(new Color(245, 248, 252));
    }

    public void setRuta(ListaEnlazada<NodoEdificio> ruta) {
        this.ruta = ruta;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(3f));
        g2.setColor(new Color(180, 190, 200));
        for (NodoEdificio origen : sistema.getNodosMapa()) {
            for (Grafo.Arista<NodoEdificio> arista : sistema.getMapa().adyacentes(origen)) {
                if (origen.getId().compareTo(arista.getDestino().getId()) < 0) {
                    g2.drawLine(origen.getX(), origen.getY(), arista.getDestino().getX(), arista.getDestino().getY());
                }
            }
        }

        if (ruta != null && ruta.tamano() > 1) {
            g2.setColor(new Color(30, 136, 229));
            g2.setStroke(new BasicStroke(4f));
            NodoEdificio previo = null;
            for (NodoEdificio nodo : ruta) {
                if (previo != null) {
                    g2.drawLine(previo.getX(), previo.getY(), nodo.getX(), nodo.getY());
                }
                previo = nodo;
            }
        }

        for (NodoEdificio nodo : sistema.getNodosMapa()) {
            g2.setColor(colorDe(nodo));
            g2.fillOval(nodo.getX() - 16, nodo.getY() - 16, 32, 32);
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(nodo.getX() - 16, nodo.getY() - 16, 32, 32);
            g2.drawString(nodo.getEtiqueta(), nodo.getX() - 18, nodo.getY() + 32);
        }
    }

    private Color colorDe(NodoEdificio nodo) {
        if (nodo.getTipo() != NodoEdificio.Tipo.APARTAMENTO || nodo.getApartamento() == null) {
            return new Color(120, 144, 156);
        }
        EstadoApartamento estado = nodo.getApartamento().getEstado();
        switch (estado) {
            case PREPARADO:
                return new Color(67, 160, 71);
            case OCUPADO:
                return new Color(30, 136, 229);
            case PENDIENTE_PREPARACION:
            case EN_PREPARACION:
                return new Color(251, 192, 45);
            default:
                return new Color(229, 57, 53);
        }
    }
}
