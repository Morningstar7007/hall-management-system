package org.example.hallmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.hallmanagementsystem.database.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            return;
        }

        // Database Authentication
        String query = "SELECT password FROM Students WHERE studentId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (pass.equals(dbPassword)) {
                    loadDashboard();
                } else {
                    errorLabel.setText("Invalid Password.");
                }
            } else {
                errorLabel.setText("Student ID not found.");
            }

        } catch (SQLException e) {
            errorLabel.setText("Database error occurred.");
            e.printStackTrace();
        }
    }

    private void loadDashboard() {
        try {
            Stage stage = (Stage) loginIdField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}