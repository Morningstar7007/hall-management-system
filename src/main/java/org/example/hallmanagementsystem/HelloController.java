package org.example.hallmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;

    /**
     * Helper method to load different screens into the center of the dashboard
     */
    private void loadView(String fxmlFileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFileName));
            javafx.scene.Node view = fxmlLoader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            if (statusLabel != null) {
                statusLabel.setText("Error loading view: " + fxmlFileName);
            }
        }
    }

    @FXML
    protected void onProfileClick() {
        loadView("profile-view.fxml");
    }

    @FXML
    protected void onMealClick() {
        loadView("meal-view.fxml");
    }

    @FXML
    protected void onPaymentClick() {
        loadView("payment-view.fxml");
    }

    @FXML
    protected void onSettingsClick() {
        loadView("settings-view.fxml");
    }

    @FXML
    protected void onLogoutClick() {
        // 1. Clear the active user session for security
        UserSession.loggedInUsername = null;

        // 2. Route the user back to the Login Gateway
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}