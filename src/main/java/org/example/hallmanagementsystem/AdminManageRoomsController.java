package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.hallmanagementsystem.database.DatabaseConnection;
import org.example.hallmanagementsystem.models.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminManageRoomsController {

    @FXML private TextField roomNumberField;
    @FXML private TextField capacityField;
    @FXML private Label formStatusLabel;

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> colRoomNumber;
    @FXML private TableColumn<Room, Integer> colCapacity;
    @FXML private TableColumn<Room, Integer> colOccupancy;

    @FXML
    public void initialize() {
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colOccupancy.setCellValueFactory(new PropertyValueFactory<>("currentOccupancy"));

        roomsTable.setRowFactory(tv -> {
            TableRow<Room> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    populateForm(row.getItem());
                }
            });
            return row;
        });

        loadRooms();
    }

    private void loadRooms() {
        org.example.hallmanagementsystem.core.ConcurrencyManager.execute(() -> {
            ObservableList<Room> rooms = FXCollections.observableArrayList();
            
            // Dynamically calculate occupancy based on assigned students
            String query = "SELECT r.roomNumber, r.capacity, " +
                           "(SELECT COUNT(*) FROM Students s WHERE s.assignedRoomNumber = r.roomNumber) as calculatedOccupancy " +
                           "FROM Rooms r";
                           
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                 
                while (rs.next()) {
                    rooms.add(new Room(
                            rs.getString("roomNumber"),
                            rs.getInt("capacity"),
                            rs.getInt("calculatedOccupancy")
                    ));
                    
                    // Optional: Update the Rooms table with this calculated value to keep it synced
                    String updateQuery = "UPDATE Rooms SET currentOccupancy = ? WHERE roomNumber = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, rs.getInt("calculatedOccupancy"));
                        updateStmt.setString(2, rs.getString("roomNumber"));
                        updateStmt.executeUpdate();
                    }
                }
                Platform.runLater(() -> roomsTable.setItems(rooms));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void populateForm(Room room) {
        roomNumberField.setText(room.getRoomNumber());
        capacityField.setText(String.valueOf(room.getCapacity()));
        formStatusLabel.setText("");
    }

    @FXML
    protected void onClearClick() {
        roomNumberField.clear();
        capacityField.clear();
        formStatusLabel.setText("");
    }

    @FXML
    protected void onSaveClick() {
        String roomNumber = roomNumberField.getText().trim();
        String capacityStr = capacityField.getText().trim();

        if (roomNumber.isEmpty() || capacityStr.isEmpty()) {
            setStatus("Error: Room Number and Capacity are required.", false);
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
        } catch (NumberFormatException e) {
            setStatus("Error: Capacity must be a number.", false);
            return;
        }

        org.example.hallmanagementsystem.core.ConcurrencyManager.execute(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String checkQuery = "SELECT 1 FROM Rooms WHERE roomNumber = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setString(1, roomNumber);
                    if (checkStmt.executeQuery().next()) {
                        Platform.runLater(() -> setStatus("Error: Room already exists.", false));
                        return;
                    }
                }

                String insertQuery = "INSERT INTO Rooms (roomNumber, capacity, currentOccupancy) VALUES (?, ?, 0)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, roomNumber);
                    insertStmt.setInt(2, capacity);
                    insertStmt.executeUpdate();
                    
                    Platform.runLater(() -> {
                        setStatus("Room added successfully!", true);
                        onClearClick();
                        loadRooms();
                    });
                }
            } catch (SQLException e) {
                Platform.runLater(() -> setStatus("Database Error: Could not add room.", false));
            }
        });
    }

    @FXML
    protected void onUpdateClick() {
        String roomNumber = roomNumberField.getText().trim();
        String capacityStr = capacityField.getText().trim();

        if (roomNumber.isEmpty() || capacityStr.isEmpty()) {
            setStatus("Error: Room Number and Capacity are required.", false);
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
        } catch (NumberFormatException e) {
            setStatus("Error: Capacity must be a number.", false);
            return;
        }

        org.example.hallmanagementsystem.core.ConcurrencyManager.execute(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String updateQuery = "UPDATE Rooms SET capacity = ? WHERE roomNumber = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, capacity);
                    updateStmt.setString(2, roomNumber);
                    
                    int affectedRows = updateStmt.executeUpdate();
                    if (affectedRows > 0) {
                        Platform.runLater(() -> {
                            setStatus("Room updated successfully!", true);
                            loadRooms();
                        });
                    } else {
                        Platform.runLater(() -> setStatus("Error: Room not found.", false));
                    }
                }
            } catch (SQLException e) {
                Platform.runLater(() -> setStatus("Database Error: Could not update room.", false));
            }
        });
    }

    @FXML
    protected void onDeleteClick() {
        String roomNumber = roomNumberField.getText().trim();
        
        if (roomNumber.isEmpty()) {
            setStatus("Error: Select a room to delete.", false);
            return;
        }

        org.example.hallmanagementsystem.core.ConcurrencyManager.execute(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Check if anyone is assigned to this room before deleting
                String checkQuery = "SELECT 1 FROM Students WHERE assignedRoomNumber = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setString(1, roomNumber);
                    if (checkStmt.executeQuery().next()) {
                        Platform.runLater(() -> setStatus("Error: Cannot delete room because students are assigned to it.", false));
                        return;
                    }
                }

                String deleteQuery = "DELETE FROM Rooms WHERE roomNumber = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setString(1, roomNumber);
                    int affectedRows = deleteStmt.executeUpdate();
                    
                    if (affectedRows > 0) {
                        Platform.runLater(() -> {
                            setStatus("Room deleted successfully!", true);
                            onClearClick();
                            loadRooms();
                        });
                    } else {
                        Platform.runLater(() -> setStatus("Error: Room not found.", false));
                    }
                }
            } catch (SQLException e) {
                Platform.runLater(() -> setStatus("Database Error: Could not delete room.", false));
            }
        });
    }
    
    private void setStatus(String message, boolean success) {
        formStatusLabel.setStyle(success ? "-fx-text-fill: #2E7D32;" : "-fx-text-fill: #D32F2F;");
        formStatusLabel.setText(message);
    }
}

