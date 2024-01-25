package org.example;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {
        // Initialize the database
        DatabaseManager.initializeDB();

        // Start the application GUI
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
