package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.hallmanagementsystem.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileController {

    @FXML private Label rollLabel;
    @FXML private Label nameLabel;
    @FXML private Label mobileLabel;
    @FXML private Label roomLabel;

    @FXML
    public void initialize() {
        // Fetch data for the dynamically logged-in student
        fetchStudentData(UserSession.loggedInUsername);
    }

    private void fetchStudentData(String studentId) {
        // Run database queries on a background thread to prevent UI freezing
        new Thread(() -> {
            String query = "SELECT * FROM Students WHERE studentId = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, studentId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Extract data from the database row
                    String fullName = rs.getString("fullName");
                    String contactNumber = rs.getString("contactNumber");
                    String assignedRoom = rs.getString("assignedRoomNumber");

                    // Update the UI labels on the main JavaFX application thread
                    Platform.runLater(() -> {
                        rollLabel.setText(": " + studentId);
                        nameLabel.setText(": " + fullName);
                        mobileLabel.setText(": " + contactNumber);
                        roomLabel.setText(": " + assignedRoom);
                    });
                } else {
                    Platform.runLater(() -> nameLabel.setText(": Record not found"));
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                Platform.runLater(() -> nameLabel.setText(": Error loading data"));
            }
        }).start();
    }
}