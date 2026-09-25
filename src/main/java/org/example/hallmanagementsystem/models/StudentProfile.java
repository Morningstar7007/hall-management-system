package org.example.hallmanagementsystem.models;

public class StudentProfile {
    private String studentId, fullName, fatherName, motherName, homeDistrict, address, department, degreeLevel, religion, gender, phoneNo, mobileNo, emailAddress, assignedRoomNumber, block, boarderNo, boarderType;
    private byte[] photo;

    public StudentProfile(String studentId, String fullName, String fatherName, String motherName, String homeDistrict, String address, String department, String degreeLevel, String religion, String gender, String phoneNo, String mobileNo, String emailAddress, String assignedRoomNumber, String block, String boarderNo, String boarderType, byte[] photo) {
        this.studentId = studentId; this.fullName = fullName; this.fatherName = fatherName; this.motherName = motherName;
        this.homeDistrict = homeDistrict; this.address = address; this.department = department; this.degreeLevel = degreeLevel;
        this.religion = religion; this.gender = gender; this.phoneNo = phoneNo; this.mobileNo = mobileNo;
        this.emailAddress = emailAddress; this.assignedRoomNumber = assignedRoomNumber; this.block = block;
        this.boarderNo = boarderNo; this.boarderType = boarderType; this.photo = photo;
    }

    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getFatherName() { return fatherName; }
    public String getMotherName() { return motherName; }
    public String getHomeDistrict() { return homeDistrict; }
    public String getAddress() { return address; }
    public String getDepartment() { return department; }
    public String getDegreeLevel() { return degreeLevel; }
    public String getReligion() { return religion; }
    public String getGender() { return gender; }
    public String getPhoneNo() { return phoneNo; }
    public String getMobileNo() { return mobileNo; }
    public String getEmailAddress() { return emailAddress; }
    public String getAssignedRoomNumber() { return assignedRoomNumber; }
    public String getBlock() { return block; }
    public String getBoarderNo() { return boarderNo; }
    public String getBoarderType() { return boarderType; }
    public byte[] getPhoto() { return photo; }
}