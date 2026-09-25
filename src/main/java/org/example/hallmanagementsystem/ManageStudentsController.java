package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.hallmanagementsystem.database.DatabaseConnection;
import org.example.hallmanagementsystem.models.StudentProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageStudentsController {

    // Form Fields
    @FXML private TextField studentIdField;
    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private TextField roomField;
    @FXML private Label formStatusLabel;

    // Table Elements
    @FXML private TableView<StudentProfile> studentsTable;
    @FXML private TableColumn<StudentProfile, String> colId;
    @FXML private TableColumn<StudentProfile, String> colName;
    @FXML private TableColumn<StudentProfile, String> colRoom;
    @FXML private TableColumn<StudentProfile, String> colContact;

    @FXML
    public void initialize() {
        // 1. Bind table columns to StudentProfile properties
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        // 2. Load existing data into the table
        loadStudentData();
    }

    private void loadStudentData() {
        new Thread(() -> {
            ObservableList<StudentProfile> students = FXCollections.observableArrayList();
            String query = "SELECT studentId, fullName, assignedRoomNumber, contactNumber FROM Students";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    students.add(new StudentProfile(
                            rs.getString("studentId"),
                            rs.getString("fullName"),
                            rs.getString("assignedRoomNumber"),
                            rs.getString("contactNumber")
                    ));
                }

                Platform.runLater(() -> studentsTable.setItems(students));

            } catch (SQLException e) {
                System.out.println("Error loading students: " + e.getMessage());
            }
        }).start();
    }

    @FXML
    protected void onSaveStudentClick() {
        String id = studentIdField.getText().trim();
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String room = roomField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || contact.isEmpty() || room.isEmpty()) {
            formStatusLabel.setText("Error: All fields are required.");
            formStatusLabel.setStyle("-fx-text-fill: #D32F2F;"); // Red text
            return;
        }

        new Thread(() -> {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();

                // START SQL TRANSACTION
                conn.setAutoCommit(false);

                // 1. Create Login Credentials (Users Table)
                String insertUser = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtUser = conn.prepareStatement(insertUser)) {
                    pstmtUser.setString(1, id);
                    pstmtUser.setString(2, "123456"); // Default password
                    pstmtUser.setString(3, "STUDENT");
                    pstmtUser.executeUpdate();
                }

                // 2. Create Student Profile (Students Table)
                String insertStudent = "INSERT INTO Students (studentId, fullName, assignedRoomNumber, contactNumber, feePaid) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmtStudent = conn.prepareStatement(insertStudent)) {
                    pstmtStudent.setString(1, id);
                    pstmtStudent.setString(2, name);
                    pstmtStudent.setString(3, room);
                    pstmtStudent.setString(4, contact);
                    pstmtStudent.setBoolean(5, true); // Assuming fee paid for simplicity
                    pstmtStudent.executeUpdate();
                }

                // IF BOTH SUCCEED, COMMIT TO DATABASE
                conn.commit();

                // Update UI on success
                Platform.runLater(() -> {
                    formStatusLabel.setText("Success: Account provisioned for " + id);
                    formStatusLabel.setStyle("-fx-text-fill: #388E3C;"); // Green text

                    // Clear fields
                    studentIdField.clear();
                    nameField.clear();
                    contactField.clear();
                    roomField.clear();

                    // Refresh the table instantly to show the new student
                    loadStudentData();
                });

            } catch (SQLException e) {
                // IF ANYTHING FAILS, ROLLBACK ALL CHANGES
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

                Platform.runLater(() -> {
                    formStatusLabel.setText("Database Error: Could not save student. Ensure Room exists and ID is unique.");
                    formStatusLabel.setStyle("-fx-text-fill: #D32F2F;"); // Red text
                });
                System.out.println("Transaction failed and rolled back: " + e.getMessage());

            } finally {
                // Restore default connection behavior
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}