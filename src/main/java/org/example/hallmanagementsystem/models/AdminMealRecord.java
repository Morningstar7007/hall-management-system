package org.example.hallmanagementsystem.models;

public class AdminMealRecord {
    private String studentId;
    private String studentName;
    private String roomNumber;
    private String status;

    public AdminMealRecord(String studentId, String studentName, String roomNumber, String status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.roomNumber = roomNumber;
        this.status = status;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getRoomNumber() { return roomNumber; }
    public String getStatus() { return status; }
}

