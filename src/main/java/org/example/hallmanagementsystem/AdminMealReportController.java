package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.hallmanagementsystem.database.DatabaseConnection;
import org.example.hallmanagementsystem.models.AdminMealRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AdminMealReportController {

    @FXML private DatePicker datePicker;
    @FXML private Label mealsOnLabel;
    @FXML private Label mealsOffLabel;

    @FXML private DatePicker overrideFromDate;
    @FXML private DatePicker overrideToDate;
    @FXML private javafx.scene.control.ComboBox<String> overrideStatusCombo;
    @FXML private Label overrideStatusLabel;

    @FXML private TableView<AdminMealRecord> mealTable;
    @FXML private TableColumn<AdminMealRecord, String> colStudentId;
    @FXML private TableColumn<AdminMealRecord, String> colStudentName;
    @FXML private TableColumn<AdminMealRecord, String> colRoomNumber;
    @FXML private TableColumn<AdminMealRecord, String> colStatus;

    @FXML
    public void initialize() {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        datePicker.setValue(LocalDate.now());
        
        overrideStatusCombo.setItems(FXCollections.observableArrayList("OFF", "ON"));
        overrideStatusCombo.setValue("OFF");

        onDateSelected();
    }

    @FXML
    protected void onApplyGlobalOverride() {
        LocalDate fromDate = overrideFromDate.getValue();
        LocalDate toDate = overrideToDate.getValue();
        String status = overrideStatusCombo.getValue();

        if (fromDate == null || toDate == null) {
            overrideStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
            overrideStatusLabel.setText("Please select both From and To dates.");
            return;
        }
        if (toDate.isBefore(fromDate)) {
            overrideStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
            overrideStatusLabel.setText("Error: 'To' date cannot be before 'From' date.");
            return;
        }

        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Global Override");
        alert.setHeaderText("Apply " + status + " to ALL students?");
        alert.setContentText("This will create a new meal record for every student from " + fromDate + " to " + toDate + ".");

        if (alert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) != javafx.scene.control.ButtonType.OK) {
            return;
        }

        org.example.hallmanagementsystem.core.ConcurrencyManager.execute(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String insertRecord = "INSERT INTO GlobalMealOverrides (fromDate, toDate, status) VALUES (?, ?, ?)";
                
                try (PreparedStatement insertStmt = conn.prepareStatement(insertRecord)) {
                    insertStmt.setString(1, fromDate.toString());
                    insertStmt.setString(2, toDate.toString());
                    insertStmt.setString(3, status);
                    insertStmt.executeUpdate();
                }
                
                Platform.runLater(() -> {
                    overrideStatusLabel.setStyle("-fx-text-fill: #2E7D32;");
                    overrideStatusLabel.setText("Successfully applied Global Override!");
                    onDateSelected(); // Refresh the table
                });
                
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    overrideStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
                    overrideStatusLabel.setText("Database error occurred.");
                });
            }
        });
    }

    @FXML
    protected void onDateSelected() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) return;

        String dateString = selectedDate.toString();

        org.example.hallmanagementsystem.core.ConcurrencyManager.execute(() -> {
            ObservableList<AdminMealRecord> records = FXCollections.observableArrayList();
            int mealsOn = 0;
            int mealsOff = 0;

            String query = "SELECT s.studentId, s.fullName, s.assignedRoomNumber, " +
                    "COALESCE(" +
                    "  (SELECT status FROM GlobalMealOverrides g " +
                    "   WHERE g.fromDate <= ? AND g.toDate >= ? " +
                    "   ORDER BY id DESC LIMIT 1), " +
                    "  (SELECT status FROM MealRecords m " +
                    "   WHERE m.studentId = s.studentId " +
                    "   AND m.fromDate <= ? AND m.toDate >= ? " +
                    "   ORDER BY id DESC LIMIT 1), " +
                    "  'ON'" +
                    ") as currentStatus " +
                    "FROM Students s";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, dateString);
                pstmt.setString(2, dateString);
                pstmt.setString(3, dateString);
                pstmt.setString(4, dateString);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String status = rs.getString("currentStatus");
                    records.add(new AdminMealRecord(
                            rs.getString("studentId"),
                            rs.getString("fullName"),
                            rs.getString("assignedRoomNumber"),
                            status
                    ));

                    if ("ON".equalsIgnoreCase(status)) {
                        mealsOn++;
                    } else if ("OFF".equalsIgnoreCase(status)) {
                        mealsOff++;
                    }
                }

                int finalMealsOn = mealsOn;
                int finalMealsOff = mealsOff;
                Platform.runLater(() -> {
                    mealTable.setItems(records);
                    mealsOnLabel.setText(String.valueOf(finalMealsOn));
                    mealsOffLabel.setText(String.valueOf(finalMealsOff));
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}

