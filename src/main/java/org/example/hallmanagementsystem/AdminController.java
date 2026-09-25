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

    @FXML
    protected void onManageStudentsClick() {
        statusLabel.setText("Student Management coming soon...");
    }

    @FXML
    protected void onLogoutClick() {
        try {
            // Take the admin back to the login screen
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}