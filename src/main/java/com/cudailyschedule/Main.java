package com.cudailyschedule;

import com.cudailyschedule.service.DatabaseService;
import com.cudailyschedule.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application launcher for CU Daily Schedule Desktop Application.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // High DPI & text anti-aliasing properties
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Initialize FlatLaf Modern Look & Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("ProgressBar.arc", 12);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize FlatLaf look and feel, using system default: " + e.getMessage());
        }

        // Test database connection in background thread on startup
        new Thread(() -> {
            try {
                LOGGER.info("Checking PostgreSQL database connection...");
                DatabaseService.checkConnection();
                LOGGER.info("Database status: " + DatabaseService.getStatusMessage());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Initial database check failed: " + e.getMessage());
            }
        }).start();

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                LOGGER.info("CU Daily Schedule Java Swing application started successfully.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to launch main application frame: " + e.getMessage(), e);
            }
        });
    }
}
