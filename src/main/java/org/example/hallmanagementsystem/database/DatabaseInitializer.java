package org.example.hallmanagementsystem.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTables() {
        String createRoomsTable = "CREATE TABLE IF NOT EXISTS Rooms ("
                + "roomNumber TEXT PRIMARY KEY,"
                + "capacity INTEGER NOT NULL,"
                + "currentOccupancy INTEGER NOT NULL,"
                + "hasAirConditioning BOOLEAN NOT NULL"
                + ");";

        String createStudentsTable = "CREATE TABLE IF NOT EXISTS Students ("
                + "studentId TEXT PRIMARY KEY,"
                + "fullName TEXT NOT NULL,"
                + "assignedRoomNumber TEXT,"
                + "contactNumber TEXT,"
                + "feePaid BOOLEAN NOT NULL,"
                + "FOREIGN KEY (assignedRoomNumber) REFERENCES Rooms(roomNumber)"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createRoomsTable);
            stmt.execute(createStudentsTable);
            System.out.println("Database tables checked/created successfully.");

            // Call the new method to insert dummy data
            insertTestData(conn);

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    private static void insertTestData(Connection conn) {
        // We use INSERT OR IGNORE so it doesn't crash if the data is already there
        String insertRoom = "INSERT OR IGNORE INTO Rooms (roomNumber, capacity, currentOccupancy, hasAirConditioning) VALUES ('B-1462', 2, 1, false)";
        String insertStudent = "INSERT OR IGNORE INTO Students (studentId, fullName, assignedRoomNumber, contactNumber, feePaid) VALUES ('2307099', 'MONOJIT PAUL TANMAY', 'B-1462', '01550086298', true)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(insertRoom);
            stmt.execute(insertStudent);
            System.out.println("Test data inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting test data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}