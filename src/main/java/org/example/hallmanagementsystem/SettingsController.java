package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.hallmanagementsystem.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsController {

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField hintField;

    @FXML
    protected void onUpdateClick() {
        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        // Dynamically get the logged-in student's ID
        String studentId = UserSession.loggedInUsername;

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert("Error", "Please fill in all password fields.", Alert.AlertType.ERROR);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert("Error", "New Password and Confirm Password do not match.", Alert.AlertType.ERROR);
            return;
        }

        new Thread(() -> {
            // First, verify the old password
            String checkQuery = "SELECT password FROM Students WHERE studentId = ?";
            String updateQuery = "UPDATE Students SET password = ? WHERE studentId = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

                checkStmt.setString(1, studentId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && oldPass.equals(rs.getString("password"))) {
                    // Old password matches, proceed with update
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, newPass);
                        updateStmt.setString(2, studentId);
                        updateStmt.executeUpdate();

                        Platform.runLater(() -> {
                            showAlert("Success", "Password updated successfully!", Alert.AlertType.INFORMATION);
                            oldPasswordField.clear();
                            newPasswordField.clear();
                            confirmPasswordField.clear();
                            hintField.clear();
                        });
                    }
                } else {
                    Platform.runLater(() -> showAlert("Error", "Incorrect Old Password.", Alert.AlertType.ERROR));
                }
            } catch (SQLException e) {
                Platform.runLater(() -> showAlert("Error", "Database error occurred.", Alert.AlertType.ERROR));
                e.printStackTrace();
            }
        }).start();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}