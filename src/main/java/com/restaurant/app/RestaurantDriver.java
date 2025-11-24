package com.restaurant.app;

import com.restaurant.gui.LoginGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RestaurantDriver {
    public static void main(String[] args) {
        // Mengatur Look and Feel agar sesuai sistem operasi (Windows/Mac/Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ignored) {
        }

        // Menjalankan LoginGUI pada Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}