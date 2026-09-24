package org.example.hallmanagementsystem.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTables() {
        // SQL query to create the Rooms table
        String createRoomsTable = "CREATE TABLE IF NOT EXISTS Rooms ("
                + "roomNumber TEXT PRIMARY KEY,"
                + "capacity INTEGER NOT NULL,"
                + "currentOccupancy INTEGER NOT NULL,"
                + "hasAirConditioning BOOLEAN NOT NULL"
                + ");";

        // SQL query to create the Students table
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS Students ("
                + "studentId TEXT PRIMARY KEY,"
                + "fullName TEXT NOT NULL,"
                + "assignedRoomNumber TEXT,"
                + "contactNumber TEXT,"
                + "feePaid BOOLEAN NOT NULL,"
                + "FOREIGN KEY (assignedRoomNumber) REFERENCES Rooms(roomNumber)"
                + ");";

        // Execute the queries
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createRoomsTable);
            stmt.execute(createStudentsTable);
            System.out.println("Database tables checked/created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // A temporary main method just so we can run this specific file to build the database
    public static void main(String[] args) {
        createTables();
    }
}