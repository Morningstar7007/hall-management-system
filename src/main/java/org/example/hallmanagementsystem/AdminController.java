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