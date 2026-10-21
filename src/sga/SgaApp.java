package sga;

import sga.gui.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class SgaApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
