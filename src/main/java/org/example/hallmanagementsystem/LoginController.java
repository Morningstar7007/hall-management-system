package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.hallmanagementsystem.database.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    @FXML
    protected void onLoginClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        // Authenticate against the SQLite database on a background thread
        new Thread(() -> {
            String query = "SELECT role FROM Users WHERE username = ? AND password = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Valid credentials - fetch the role
                    String role = rs.getString("role");
                    UserSession.loggedInUsername = username;

                    // Route the user based on their specific role
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        Platform.runLater(() -> loadDashboard("admin-view.fxml")); // Update this if your admin FXML name is different
                    } else {
                        Platform.runLater(() -> loadDashboard("hello-view.fxml"));
                    }
                } else {
                    Platform.runLater(() -> errorLabel.setText("Invalid username or password."));
                }
            } catch (SQLException e) {
                Platform.runLater(() -> errorLabel.setText("Database error: " + e.getMessage()));
            }
        }).start();
    }

    private void loadDashboard(String fxmlFileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFileName));

            // Swap the root content to maintain full-screen state seamlessly
            loginButton.getScene().setRoot(fxmlLoader.load());

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error loading dashboard layout: " + fxmlFileName);
        }
    }
}