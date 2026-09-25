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
        String username = loginIdField.getText();
        String pass = passwordField.getText();

        if (username.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please enter your credentials.");
            return;
        }

        // Query the Users table for password and role
        String query = "SELECT password, role FROM Users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                String role = rs.getString("role");

                if (pass.equals(dbPassword)) {
                    // Save the user's ID to the global session
                    UserSession.loggedInUsername = username;

                    // Smart Routing: Load different dashboards based on role
                    if (role.equals("ADMIN")) {
                        loadDashboard("admin-view.fxml");
                    } else if (role.equals("STUDENT")) {
                        loadDashboard("hello-view.fxml");
                    }
                } else {
                    errorLabel.setText("Invalid Password.");
                }
            } else {
                errorLabel.setText("User not found.");
            }

        } catch (SQLException e) {
            errorLabel.setText("Database error occurred.");
            e.printStackTrace();
        }
    }

    private void loadDashboard(String fxmlFile) {
        try {
            Stage stage = (Stage) loginIdField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}