package org.example.hallmanagementsystem.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTables() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "username TEXT PRIMARY KEY,"
                + "password TEXT NOT NULL,"
                + "role TEXT NOT NULL"
                + ");";

        String createRoomsTable = "CREATE TABLE IF NOT EXISTS Rooms ("
                + "roomNumber TEXT PRIMARY KEY,"
                + "capacity INTEGER NOT NULL,"
                + "currentOccupancy INTEGER NOT NULL,"
                + "hasAirConditioning BOOLEAN NOT NULL"
                + ");";

        String createStudentsTable = "CREATE TABLE IF NOT EXISTS Students ("
                + "studentId TEXT PRIMARY KEY,"
                + "fullName TEXT NOT NULL,"
                + "fatherName TEXT,"
                + "motherName TEXT,"
                + "homeDistrict TEXT,"
                + "address TEXT,"
                + "department TEXT,"
                + "degreeLevel TEXT,"
                + "religion TEXT,"
                + "gender TEXT,"
                + "phoneNo TEXT,"
                + "mobileNo TEXT,"
                + "emailAddress TEXT,"
                + "assignedRoomNumber TEXT,"
                + "block TEXT,"
                + "boarderNo TEXT,"
                + "boarderType TEXT,"
                + "photo BLOB," // NEW COLUMN
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

            insertTestData(conn);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    private static void insertTestData(Connection conn) {
        String insertAdminUser = "INSERT OR IGNORE INTO Users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN')";
        String insertStudentUser = "INSERT OR IGNORE INTO Users (username, password, role) VALUES ('2307099', '123456', 'STUDENT')";
        String insertRoom = "INSERT OR IGNORE INTO Rooms (roomNumber, capacity, currentOccupancy, hasAirConditioning) VALUES ('B-1462', 2, 1, false)";

        // Notice the NULL at the end for the missing photo on the seed data
        String insertStudentProfile = "INSERT OR IGNORE INTO Students (studentId, fullName, fatherName, motherName, homeDistrict, address, department, degreeLevel, religion, gender, phoneNo, mobileNo, emailAddress, assignedRoomNumber, block, boarderNo, boarderType, photo) "
                + "VALUES ('2307099', 'MONOJIT PAUL TANMAY', 'John Doe', 'Jane Doe', 'Dhaka', '123 Main St', 'CSE', 'Undergraduate', 'Hinduism', 'Male', 'N/A', '01550086298', 'monojitpaul704@gmail.com', 'B-1462', 'B', '101', 'Resident', NULL)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(insertAdminUser);
            stmt.execute(insertStudentUser);
            stmt.execute(insertRoom);
            stmt.execute(insertStudentProfile);
        } catch (SQLException e) {
            System.out.println("Error inserting test data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}