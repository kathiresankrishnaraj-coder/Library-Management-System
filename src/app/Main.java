package app;

import gui.LoginFrame;
import theme.ThemeManager;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Initialize FlatLaf theme BEFORE any Swing components are created.
        // This must happen outside invokeLater so the L&F is set before the EDT
        // begins constructing any window.
        ThemeManager.init();

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}