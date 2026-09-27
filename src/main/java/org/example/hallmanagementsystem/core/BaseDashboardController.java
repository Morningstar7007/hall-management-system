 package org.example.hallmanagementsystem.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.example.hallmanagementsystem.HelloApplication;
import org.example.hallmanagementsystem.UserSession;

import java.io.IOException;

/**
 * Abstract base class for dashboards that implements common navigation logic.
 * Demonstrates Advanced OOP Concepts (Abstract Classes & Inheritance).
 */
public abstract class BaseDashboardController implements DashboardNavigation {

    // Abstract methods that force child classes to provide their specific UI components
    protected abstract StackPane getContentArea();
    protected abstract Label getStatusLabel();

    @Override
    public void loadView(String fxmlFileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFileName));
            javafx.scene.Node view = fxmlLoader.load();
            getContentArea().getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            if (getStatusLabel() != null) {
                getStatusLabel().setText("Error loading view: " + fxmlFileName);
            }
        }
    }

    @Override
    public void handleLogout() {
        // Clear session for security
        UserSession.loggedInUsername = null;
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            getContentArea().getScene().setRoot(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

