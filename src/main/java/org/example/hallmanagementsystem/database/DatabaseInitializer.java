package org.example.hallmanagementsystem.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTables() {
        // 1. Users Table (Handles all logins)
        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "username TEXT PRIMARY KEY,"
                + "password TEXT NOT NULL,"
                + "role TEXT NOT NULL" // Will be either 'ADMIN' or 'STUDENT'
                + ");";

        // 2. Rooms Table
        String createRoomsTable = "CREATE TABLE IF NOT EXISTS Rooms ("
                + "roomNumber TEXT PRIMARY KEY,"
                + "capacity INTEGER NOT NULL,"
                + "currentOccupancy INTEGER NOT NULL,"
                + "hasAirConditioning BOOLEAN NOT NULL"
                + ");";

        // 3. Students Table (Profile data only, no password here)
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS Students ("
                + "studentId TEXT PRIMARY KEY,"
                + "fullName TEXT NOT NULL,"
                + "assignedRoomNumber TEXT,"
                + "contactNumber TEXT,"
                + "feePaid BOOLEAN NOT NULL,"
                + "FOREIGN KEY (studentId) REFERENCES Users(username),"
                + "FOREIGN KEY (assignedRoomNumber) REFERENCES Rooms(roomNumber)"
                + ");";

        String createMealsTable = "CREATE TABLE IF NOT EXISTS MealRecords ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "studentId TEXT NOT NULL,"
                + "fromDate TEXT NOT NULL,"
                + "toDate TEXT NOT NULL,"
                + "status TEXT NOT NULL,"
                + "FOREIGN KEY (studentId) REFERENCES Users(username)"
                + ");";

        String createPaymentsTable = "CREATE TABLE IF NOT EXISTS Payments ("
                + "sl INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "studentId TEXT NOT NULL,"
                + "fromYear TEXT, fromMonth TEXT, toYear TEXT, toMonth TEXT,"
                + "messing REAL, monthlyFeast REAL, fine REAL, generator REAL, waterSupply REAL, miscellaneous REAL,"
                + "FOREIGN KEY (studentId) REFERENCES Users(username)"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createRoomsTable);
            stmt.execute(createStudentsTable);
            stmt.execute(createMealsTable);
            stmt.execute(createPaymentsTable);
            System.out.println("Role-based database tables created successfully.");

            insertTestData(conn);

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    private static void insertTestData(Connection conn) {
        // Seed 1: The Master Admin Account
        String insertAdminUser = "INSERT OR IGNORE INTO Users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN')";

        // Seed 2: The Student Account & Profile
        String insertStudentUser = "INSERT OR IGNORE INTO Users (username, password, role) VALUES ('2307099', '123456', 'STUDENT')";
        String insertRoom = "INSERT OR IGNORE INTO Rooms (roomNumber, capacity, currentOccupancy, hasAirConditioning) VALUES ('B-1462', 2, 1, false)";
        String insertStudentProfile = "INSERT OR IGNORE INTO Students (studentId, fullName, assignedRoomNumber, contactNumber, feePaid) VALUES ('2307099', 'MONOJIT PAUL TANMAY', 'B-1462', '01550086298', true)";

        String insertPayment1 = "INSERT OR IGNORE INTO Payments (sl, studentId, fromYear, fromMonth, toYear, toMonth, messing, monthlyFeast, fine, generator, waterSupply, miscellaneous) "
                + "VALUES (1, '2307099', '2026', '9', '2026', '9', 1950.00, 70.00, 0.00, 5.00, 10.00, 0.00)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(insertAdminUser);
            stmt.execute(insertStudentUser);
            stmt.execute(insertRoom);
            stmt.execute(insertStudentProfile);
            stmt.execute(insertPayment1);
        } catch (SQLException e) {
            System.out.println("Error inserting test data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}