package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.example.hallmanagementsystem.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class MealController {

    @FXML private ToggleGroup mealStatusGroup;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML
    protected void onUpdateClick() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        // Validation: Ensure dates are picked
        if (fromDate == null || toDate == null) {
            showAlert("Error", "Please select both 'From' and 'To' dates.");
            return;
        }

        RadioButton selectedRadio = (RadioButton) mealStatusGroup.getSelectedToggle();
        String status = selectedRadio.getText();
        String studentId = "2307099"; // Hardcoded to match our seed data

        // Run database INSERT on a background thread
        new Thread(() -> {
            String query = "INSERT INTO MealRecords (studentId, fromDate, toDate, status) VALUES (?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                // Set the parameters safely to prevent SQL injection
                pstmt.setString(1, studentId);
                pstmt.setString(2, fromDate.toString());
                pstmt.setString(3, toDate.toString());
                pstmt.setString(4, status);

                // executeUpdate() is used for INSERT, UPDATE, and DELETE
                pstmt.executeUpdate();

                Platform.runLater(() -> showAlert("Success", "Meal status (" + status + ") saved to database successfully!"));

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                Platform.runLater(() -> showAlert("Error", "Could not save meal status."));
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}