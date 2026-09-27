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
import org.example.hallmanagementsystem.models.AdminPaymentRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminManagePaymentsController {

    @FXML private TextField studentIdField, fromYearField, fromMonthField, toYearField, toMonthField;
    @FXML private TextField messingField, monthlyFeastField, fineField, generatorField, waterSupplyField, miscellaneousField;
    @FXML private Label formStatusLabel;

    @FXML private TableView<AdminPaymentRecord> paymentsTable;
    @FXML private TableColumn<AdminPaymentRecord, String> colStudentId;
    @FXML private TableColumn<AdminPaymentRecord, String> colFrom;
    @FXML private TableColumn<AdminPaymentRecord, String> colTo;
    @FXML private TableColumn<AdminPaymentRecord, Double> colMessing;
    @FXML private TableColumn<AdminPaymentRecord, Double> colFeast;
    @FXML private TableColumn<AdminPaymentRecord, Double> colFine;
    @FXML private TableColumn<AdminPaymentRecord, Double> colTotal;

    @FXML
    public void initialize() {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("fromMonthYear"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("toMonthYear"));
        colMessing.setCellValueFactory(new PropertyValueFactory<>("messing"));
        colFeast.setCellValueFactory(new PropertyValueFactory<>("feast"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        loadRecentPayments();
    }

    private void loadRecentPayments() {
        org.example.hallmanagementsystem.core.ConcurrencyManager.execute(() -> {
            ObservableList<AdminPaymentRecord> records = FXCollections.observableArrayList();
            String query = "SELECT * FROM Payments ORDER BY sl DESC LIMIT 50";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String fromStr = rs.getString("fromMonth") + " " + rs.getString("fromYear");
                    String toStr = rs.getString("toMonth") + " " + rs.getString("toYear");
                    double messing = rs.getDouble("messing");
                    double feast = rs.getDouble("monthlyFeast");
                    double fine = rs.getDouble("fine");
                    double total = messing + feast + fine + rs.getDouble("generator") + rs.getDouble("waterSupply") + rs.getDouble("miscellaneous");

                    records.add(new AdminPaymentRecord(
                            rs.getInt("sl"),
                            rs.getString("studentId"),
                            fromStr,
                            toStr,
                            messing,
                            feast,
                            fine,
                            total
                    ));
                }

                Platform.runLater(() -> paymentsTable.setItems(records));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    protected void onSaveClick() {
        String studentId = studentIdField.getText().trim();
        String fromYear = fromYearField.getText().trim();
        String fromMonth = fromMonthField.getText().trim();

        if (studentId.isEmpty() || fromYear.isEmpty() || fromMonth.isEmpty()) {
            formStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
            formStatusLabel.setText("Error: Student ID, From Year, and From Month are required.");
            return;
        }

        double messing = parseDoubleSecure(messingField.getText());
        double monthlyFeast = parseDoubleSecure(monthlyFeastField.getText());
        double fine = parseDoubleSecure(fineField.getText());
        double generator = parseDoubleSecure(generatorField.getText());
        double waterSupply = parseDoubleSecure(waterSupplyField.getText());
        double miscellaneous = parseDoubleSecure(miscellaneousField.getText());
        String toYear = toYearField.getText().trim();
        String toMonth = toMonthField.getText().trim();

        org.example.hallmanagementsystem.core.ConcurrencyManager.execute(() -> {
            // First check if student exists
            String checkStudent = "SELECT 1 FROM Students WHERE studentId = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(checkStudent)) {
                
                checkStmt.setString(1, studentId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    Platform.runLater(() -> {
                        formStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        formStatusLabel.setText("Error: Student ID not found in database.");
                    });
                    return;
                }

                String insertQuery = "INSERT INTO Payments (studentId, fromYear, fromMonth, toYear, toMonth, messing, monthlyFeast, fine, generator, waterSupply, miscellaneous) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    pstmt.setString(1, studentId);
                    pstmt.setString(2, fromYear);
                    pstmt.setString(3, fromMonth);
                    pstmt.setString(4, toYear);
                    pstmt.setString(5, toMonth);
                    pstmt.setDouble(6, messing);
                    pstmt.setDouble(7, monthlyFeast);
                    pstmt.setDouble(8, fine);
                    pstmt.setDouble(9, generator);
                    pstmt.setDouble(10, waterSupply);
                    pstmt.setDouble(11, miscellaneous);
                    
                    pstmt.executeUpdate();
                    
                    Platform.runLater(() -> {
                        formStatusLabel.setStyle("-fx-text-fill: #2E7D32;");
                        formStatusLabel.setText("Payment added successfully!");
                        onClearClick();
                        loadRecentPayments();
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    formStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
                    formStatusLabel.setText("Database error: Could not save payment.");
                });
            }
        });
    }

    private double parseDoubleSecure(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @FXML
    protected void onClearClick() {
        studentIdField.clear();
        fromYearField.clear();
        fromMonthField.clear();
        toYearField.clear();
        toMonthField.clear();
        messingField.clear();
        monthlyFeastField.clear();
        fineField.clear();
        generatorField.clear();
        waterSupplyField.clear();
        miscellaneousField.clear();
        formStatusLabel.setText("");
    }
}

