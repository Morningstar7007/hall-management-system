package org.example.hallmanagementsystem.core;

/**
 * Interface defining the standard navigation behaviors for any Dashboard controller.
 * Demonstrates Advanced OOP Concepts (Interfaces).
 */
public interface DashboardNavigation {
    
    /**
     * Loads an FXML view into the center content area.
     * @param fxmlFileName the name of the FXML file
     */
    void loadView(String fxmlFileName);

    /**
     * Handles the logout process.
     */
    void handleLogout();
}

