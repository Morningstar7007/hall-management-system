package org.example.hallmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminController {

    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;

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
    protected void onManageStudentsClick() {
        loadView("manage-students-view.fxml");
    }

    @FXML
    protected void onLogoutClick() {
        // 1. Clear the active admin session for security
        UserSession.loggedInUsername = null;

        // 2. Route back to the Login Gateway seamlessly
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));

            // IMPORTANT: Replace 'contentArea' below with the name of ANY @FXML Node
            // that is already defined at the top of your AdminController
            // (such as your main layout pane, border pane, or a button).
            contentArea.getScene().setRoot(fxmlLoader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}