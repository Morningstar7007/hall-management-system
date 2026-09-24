package org.example.hallmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField loginIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    protected void onLoginClick() {
        String id = loginIdField.getText();
        String pass = passwordField.getText();

        if (id.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please enter your credentials.");
        } else if (id.equals("2307099") && pass.equals("123456")) {
            // Successful login, load the main dashboard
            loadDashboard();
        } else {
            errorLabel.setText("Invalid Login Id or Password.");
        }
    }

    private void loadDashboard() {
        try {
            // Get the current window (stage)
            Stage stage = (Stage) loginIdField.getScene().getWindow();

            // Load the main dashboard FXML
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);

            // Swap the screen
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}