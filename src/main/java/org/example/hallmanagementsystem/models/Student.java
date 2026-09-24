package org.example.hallmanagementsystem.models;

public class Student {
    private String studentId;
    private String fullName;
    private String assignedRoomNumber;
    private String contactNumber;
    private boolean feePaid;

    // Constructor
    public Student(String studentId, String fullName, String assignedRoomNumber, String contactNumber, boolean feePaid) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.assignedRoomNumber = assignedRoomNumber;
        this.contactNumber = contactNumber;
        this.feePaid = feePaid;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAssignedRoomNumber() {
        return assignedRoomNumber;
    }

    public void setAssignedRoomNumber(String assignedRoomNumber) {
        this.assignedRoomNumber = assignedRoomNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isFeePaid() {
        return feePaid;
    }

    public void setFeePaid(boolean feePaid) {
        this.feePaid = feePaid;
    }
}