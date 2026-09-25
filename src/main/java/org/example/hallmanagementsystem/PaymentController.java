package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.hallmanagementsystem.database.DatabaseConnection;
import org.example.hallmanagementsystem.models.PaymentRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentController {

    @FXML private TableView<PaymentRecord> paymentTable;
    @FXML private TableColumn<PaymentRecord, Integer> slCol;
    @FXML private TableColumn<PaymentRecord, String> fromYearCol;
    @FXML private TableColumn<PaymentRecord, String> fromMonthCol;
    @FXML private TableColumn<PaymentRecord, String> toYearCol;
    @FXML private TableColumn<PaymentRecord, String> toMonthCol;
    @FXML private TableColumn<PaymentRecord, Double> messingCol;
    @FXML private TableColumn<PaymentRecord, Double> monthlyFeastCol;
    @FXML private TableColumn<PaymentRecord, Double> fineCol;
    @FXML private TableColumn<PaymentRecord, Double> generatorCol;
    @FXML private TableColumn<PaymentRecord, Double> waterSupplyCol;
    @FXML private TableColumn<PaymentRecord, Double> miscellaneousCol;

    @FXML
    public void initialize() {
        // Link each column directly to the property names in PaymentRecord.java
        slCol.setCellValueFactory(new PropertyValueFactory<>("sl"));
        fromYearCol.setCellValueFactory(new PropertyValueFactory<>("fromYear"));
        fromMonthCol.setCellValueFactory(new PropertyValueFactory<>("fromMonth"));
        toYearCol.setCellValueFactory(new PropertyValueFactory<>("toYear"));
        toMonthCol.setCellValueFactory(new PropertyValueFactory<>("toMonth"));
        messingCol.setCellValueFactory(new PropertyValueFactory<>("messing"));
        monthlyFeastCol.setCellValueFactory(new PropertyValueFactory<>("monthlyFeast"));
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));
        generatorCol.setCellValueFactory(new PropertyValueFactory<>("generator"));
        waterSupplyCol.setCellValueFactory(new PropertyValueFactory<>("waterSupply"));
        miscellaneousCol.setCellValueFactory(new PropertyValueFactory<>("miscellaneous"));

        // Fetch and load the data from SQLite dynamically using the session
        if (UserSession.loggedInUsername != null) {
            loadPaymentData(UserSession.loggedInUsername);
        }
    }

    private void loadPaymentData(String studentId) {
        new Thread(() -> {
            ObservableList<PaymentRecord> records = FXCollections.observableArrayList();
            String query = "SELECT * FROM Payments WHERE studentId = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, studentId);
                ResultSet rs = pstmt.executeQuery();

                // Loop through all database results and create Java objects
                while (rs.next()) {
                    records.add(new PaymentRecord(
                            rs.getInt("sl"),
                            rs.getString("fromYear"),
                            rs.getString("fromMonth"),
                            rs.getString("toYear"),
                            rs.getString("toMonth"),
                            rs.getDouble("messing"),
                            rs.getDouble("monthlyFeast"),
                            rs.getDouble("fine"),
                            rs.getDouble("generator"),
                            rs.getDouble("waterSupply"),
                            rs.getDouble("miscellaneous")
                    ));
                }

                // Update the TableView on the main JavaFX thread
                Platform.runLater(() -> paymentTable.setItems(records));

            } catch (SQLException e) {
                System.out.println("Error loading payments: " + e.getMessage());
            }
        }).start();
    }
}