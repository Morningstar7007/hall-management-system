package org.example.hallmanagementsystem.models;

public class StudentProfile {
    private String studentId;
    private String fullName;
    private String roomNumber;
    private String contactNumber;

    public StudentProfile(String studentId, String fullName, String roomNumber, String contactNumber) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.roomNumber = roomNumber;
        this.contactNumber = contactNumber;
    }

    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getRoomNumber() { return roomNumber; }
    public String getContactNumber() { return contactNumber; }
}