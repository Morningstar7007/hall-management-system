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
                + "currentOccupancy INTEGER NOT NULL"
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

        String createGlobalMealOverridesTable = "CREATE TABLE IF NOT EXISTS GlobalMealOverrides ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "fromDate TEXT NOT NULL,"
                + "toDate TEXT NOT NULL,"
                + "status TEXT NOT NULL"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createRoomsTable);
            stmt.execute(createStudentsTable);
            stmt.execute(createMealsTable);
            stmt.execute(createPaymentsTable);
            stmt.execute(createGlobalMealOverridesTable);

            insertTestData(conn);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    private static void insertTestData(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Check if test data is already inserted to prevent duplicates on multiple runs
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Users WHERE username = 'admin'");
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Data already exists, exit early
            }

            // Admin
            stmt.execute("INSERT OR IGNORE INTO Users (username, password, role) VALUES ('admin', 'adminpassword', 'ADMIN')");
            
            // Rooms
            stmt.execute("INSERT OR IGNORE INTO Rooms (roomNumber, capacity, currentOccupancy) VALUES ('A-101', 2, 2)");
            stmt.execute("INSERT OR IGNORE INTO Rooms (roomNumber, capacity, currentOccupancy) VALUES ('A-102', 4, 3)");
            stmt.execute("INSERT OR IGNORE INTO Rooms (roomNumber, capacity, currentOccupancy) VALUES ('B-201', 2, 1)");

            // Student Users
            stmt.execute("INSERT OR IGNORE INTO Users (username, password, role) VALUES ('2300001', 'password123', 'STUDENT')");
            stmt.execute("INSERT OR IGNORE INTO Users (username, password, role) VALUES ('2300002', 'password123', 'STUDENT')");
            stmt.execute("INSERT OR IGNORE INTO Users (username, password, role) VALUES ('2300003', 'password123', 'STUDENT')");
            stmt.execute("INSERT OR IGNORE INTO Users (username, password, role) VALUES ('2300004', 'password123', 'STUDENT')");
            stmt.execute("INSERT OR IGNORE INTO Users (username, password, role) VALUES ('2300005', 'password123', 'STUDENT')");

            // Student Profiles
            String baseInsert = "INSERT OR IGNORE INTO Students (studentId, fullName, fatherName, motherName, homeDistrict, address, department, degreeLevel, religion, gender, phoneNo, mobileNo, emailAddress, assignedRoomNumber, block, boarderNo, boarderType, photo) VALUES ";
            stmt.execute(baseInsert + "('2300001', 'ALICE JOHNSON', 'Robert Johnson', 'Mary Johnson', 'Dhaka', '123 College Ave', 'CSE', 'Undergraduate', 'Christianity', 'Female', 'N/A', '555-0101', 'alice@example.com', 'A-101', 'A', '101', 'Resident', NULL)");
            stmt.execute(baseInsert + "('2300002', 'BOB SMITH', 'John Smith', 'Jane Smith', 'Chittagong', '456 Uni Rd', 'EEE', 'Undergraduate', 'Islam', 'Male', 'N/A', '555-0102', 'bob@example.com', 'A-101', 'A', '102', 'Resident', NULL)");
            stmt.execute(baseInsert + "('2300003', 'CHARLIE BROWN', 'David Brown', 'Sarah Brown', 'Sylhet', '789 Main St', 'ME', 'Undergraduate', 'Hinduism', 'Male', 'N/A', '555-0103', 'charlie@example.com', 'A-102', 'A', '103', 'Resident', NULL)");
            stmt.execute(baseInsert + "('2300004', 'DIANA PRINCE', 'James Prince', 'Laura Prince', 'Rajshahi', '321 School Ln', 'CE', 'Undergraduate', 'Christianity', 'Female', 'N/A', '555-0104', 'diana@example.com', 'A-102', 'A', '104', 'Resident', NULL)");
            stmt.execute(baseInsert + "('2300005', 'EVAN WRIGHT', 'Peter Wright', 'Chloe Wright', 'Khulna', '654 Park Blvd', 'CSE', 'Master', 'Buddhism', 'Male', 'N/A', '555-0105', 'evan@example.com', 'B-201', 'B', '201', 'Resident', NULL)");
            
            // Dummy Payment Records
            String paymentInsert = "INSERT INTO Payments (studentId, fromYear, fromMonth, toYear, toMonth, messing, monthlyFeast, fine, generator, waterSupply, miscellaneous) VALUES ";
            stmt.execute(paymentInsert + "('2300001', '2026', 'January', '2026', 'June', 1500.0, 200.0, 0.0, 300.0, 100.0, 50.0)");
            stmt.execute(paymentInsert + "('2300001', '2026', 'July', '2026', 'December', 1600.0, 250.0, 50.0, 300.0, 100.0, 0.0)");
            stmt.execute(paymentInsert + "('2300002', '2026', 'January', '2026', 'May', 1200.0, 200.0, 0.0, 300.0, 100.0, 0.0)");
            stmt.execute(paymentInsert + "('2300003', '2026', 'February', '2026', 'August', 1800.0, 300.0, 10.0, 300.0, 100.0, 25.0)");

        } catch (SQLException e) {
            System.out.println("Error inserting test data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}
