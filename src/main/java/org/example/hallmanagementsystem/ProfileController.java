package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.hallmanagementsystem.database.DatabaseConnection;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileController {

    @FXML private Label rollLabel, nameLabel, fatherNameLabel, motherNameLabel, homeDistrictLabel, addressLabel, departmentLabel, degreeLevelLabel, religionLabel, genderLabel, phoneLabel, mobileLabel, emailLabel, roomLabel, blockLabel, boarderNoLabel, boarderTypeLabel;
    @FXML private ImageView profileImageView; // Maps to the UI

    @FXML
    public void initialize() {
        if (UserSession.loggedInUsername != null) fetchStudentData(UserSession.loggedInUsername);
    }

    private void fetchStudentData(String studentId) {
        new Thread(() -> {
            String query = "SELECT * FROM Students WHERE studentId = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, studentId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String[] data = {
                            rs.getString("studentId"), rs.getString("fullName"), rs.getString("fatherName"),
                            rs.getString("motherName"), rs.getString("homeDistrict"), rs.getString("address"),
                            rs.getString("department"), rs.getString("degreeLevel"), rs.getString("religion"),
                            rs.getString("gender"), rs.getString("phoneNo"), rs.getString("mobileNo"),
                            rs.getString("emailAddress"), rs.getString("assignedRoomNumber"), rs.getString("block"),
                            rs.getString("boarderNo"), rs.getString("boarderType")
                    };

                    // Retrieve binary image data
                    byte[] photoBytes = rs.getBytes("photo");

                    Platform.runLater(() -> {
                        rollLabel.setText(": " + data[0]); nameLabel.setText(": " + data[1]); fatherNameLabel.setText(": " + data[2]);
                        motherNameLabel.setText(": " + data[3]); homeDistrictLabel.setText(": " + data[4]); addressLabel.setText(": " + data[5]);
                        departmentLabel.setText(": " + data[6]); degreeLevelLabel.setText(": " + data[7]); religionLabel.setText(": " + data[8]);
                        genderLabel.setText(": " + data[9]); phoneLabel.setText(": " + data[10]); mobileLabel.setText(": " + data[11]);
                        emailLabel.setText(": " + data[12]); roomLabel.setText(": " + data[13]); blockLabel.setText(": " + data[14]);
                        boarderNoLabel.setText(": " + data[15]); boarderTypeLabel.setText(": " + data[16]);

                        // Set image if it exists in the database
                        if (photoBytes != null) {
                            profileImageView.setImage(new Image(new ByteArrayInputStream(photoBytes)));
                        }
                    });
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }).start();
    }
}