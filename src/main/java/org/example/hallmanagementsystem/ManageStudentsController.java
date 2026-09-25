package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.example.hallmanagementsystem.database.DatabaseConnection;
import org.example.hallmanagementsystem.models.StudentProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageStudentsController {

    @FXML private TextField fId, fName, fFather, fMother, fDistrict, fAddress, fReligion, fPhone, fMobile, fEmail, fRoom, fBoarderNo, fBoarderType;
    @FXML private ComboBox<String> fDept, fDegree, fGender, fBlock;
    @FXML private Label formStatusLabel;
    @FXML private TableView<StudentProfile> studentsTable;
    @FXML private TableColumn<StudentProfile, String> colId, colName, colRoom, colMobile, colDept, colBoarderNo, colBoarderType;

    @FXML
    public void initialize() {
        fDept.setItems(FXCollections.observableArrayList("CSE", "EEE", "ME", "CE", "URP", "Arch", "BECM", "LE", "ECE", "BME", "MSE", "IEM"));
        fDegree.setItems(FXCollections.observableArrayList("B.Sc. (Undergraduate)", "M.Sc. (Postgraduate)", "Ph.D."));
        fGender.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        fBlock.setItems(FXCollections.observableArrayList("A", "B", "C", "D"));

        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("assignedRoomNumber"));
        colMobile.setCellValueFactory(new PropertyValueFactory<>("mobileNo"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colBoarderNo.setCellValueFactory(new PropertyValueFactory<>("boarderNo"));
        colBoarderType.setCellValueFactory(new PropertyValueFactory<>("boarderType"));

        studentsTable.setRowFactory(tv -> {
            TableRow<StudentProfile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showFullProfileDialog(row.getItem());
                }
            });
            return row;
        });

        loadStudentData();
    }

    private void showFullProfileDialog(StudentProfile p) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Profile Details");
        alert.setHeaderText(p.getFullName() + " (ID: " + p.getStudentId() + ")");
        alert.getDialogPane().setMinWidth(450);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(new Label("Student Roll:"), 0, 0); grid.add(new Label(p.getStudentId()), 1, 0);
        grid.add(new Label("Father's Name:"), 0, 1); grid.add(new Label(p.getFatherName()), 1, 1);
        grid.add(new Label("Mother's Name:"), 0, 2); grid.add(new Label(p.getMotherName()), 1, 2);
        grid.add(new Label("Home District:"), 0, 3); grid.add(new Label(p.getHomeDistrict()), 1, 3);
        grid.add(new Label("Address:"), 0, 4); grid.add(new Label(p.getAddress()), 1, 4);
        grid.add(new Label("Department:"), 0, 5); grid.add(new Label(p.getDepartment()), 1, 5);
        grid.add(new Label("Degree Level:"), 0, 6); grid.add(new Label(p.getDegreeLevel()), 1, 6);
        grid.add(new Label("Religion:"), 0, 7); grid.add(new Label(p.getReligion()), 1, 7);
        grid.add(new Label("Gender:"), 0, 8); grid.add(new Label(p.getGender()), 1, 8);
        grid.add(new Label("Phone No:"), 0, 9); grid.add(new Label(p.getPhoneNo()), 1, 9);
        grid.add(new Label("Mobile No:"), 0, 10); grid.add(new Label(p.getMobileNo()), 1, 10);
        grid.add(new Label("Email:"), 0, 11); grid.add(new Label(p.getEmailAddress()), 1, 11);
        grid.add(new Label("Room No:"), 0, 12); grid.add(new Label(p.getAssignedRoomNumber()), 1, 12);
        grid.add(new Label("Block:"), 0, 13); grid.add(new Label(p.getBlock()), 1, 13);
        grid.add(new Label("Boarder No:"), 0, 14); grid.add(new Label(p.getBoarderNo()), 1, 14);
        grid.add(new Label("Boarder Type:"), 0, 15); grid.add(new Label(p.getBoarderType()), 1, 15);

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }

    private void loadStudentData() {
        new Thread(() -> {
            ObservableList<StudentProfile> students = FXCollections.observableArrayList();
            String query = "SELECT * FROM Students";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    students.add(new StudentProfile(
                            rs.getString("studentId"), rs.getString("fullName"), rs.getString("fatherName"),
                            rs.getString("motherName"), rs.getString("homeDistrict"), rs.getString("address"),
                            rs.getString("department"), rs.getString("degreeLevel"), rs.getString("religion"),
                            rs.getString("gender"), rs.getString("phoneNo"), rs.getString("mobileNo"),
                            rs.getString("emailAddress"), rs.getString("assignedRoomNumber"), rs.getString("block"),
                            rs.getString("boarderNo"), rs.getString("boarderType")
                    ));
                }
                Platform.runLater(() -> studentsTable.setItems(students));
            } catch (SQLException e) { e.printStackTrace(); }
        }).start();
    }

    private String getComboValue(ComboBox<String> combo) {
        return combo.getValue() == null ? "" : combo.getValue().trim();
    }

    private void clearForm() {
        fId.clear(); fName.clear(); fFather.clear(); fMother.clear(); fDistrict.clear(); fAddress.clear();
        fReligion.clear(); fPhone.clear(); fMobile.clear(); fEmail.clear(); fRoom.clear(); fBoarderNo.clear(); fBoarderType.clear();
        fDept.setValue(null); fDegree.setValue(null); fGender.setValue(null); fBlock.setValue(null);
    }

    @FXML
    protected void onSaveClick() {
        String deptVal = getComboValue(fDept);
        String genderVal = getComboValue(fGender);

        if (fId.getText().trim().isEmpty() || fName.getText().trim().isEmpty() ||
                fFather.getText().trim().isEmpty() || fMother.getText().trim().isEmpty() ||
                fAddress.getText().trim().isEmpty() || deptVal.isEmpty() ||
                genderVal.isEmpty() || fMobile.getText().trim().isEmpty() ||
                fBoarderNo.getText().trim().isEmpty()) {

            formStatusLabel.setText("Error: Fields marked with * are mandatory.");
            return;
        }

        String id = fId.getText().trim();

        new Thread(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                try (PreparedStatement pstmtUser = conn.prepareStatement("INSERT INTO Users (username, password, role) VALUES (?, ?, ?)")) {
                    pstmtUser.setString(1, id);
                    pstmtUser.setString(2, "123456");
                    pstmtUser.setString(3, "STUDENT");
                    pstmtUser.executeUpdate();
                }

                String insertQuery = "INSERT INTO Students (studentId, fullName, fatherName, motherName, homeDistrict, address, department, degreeLevel, religion, gender, phoneNo, mobileNo, emailAddress, assignedRoomNumber, block, boarderNo, boarderType) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    pstmt.setString(1, id); pstmt.setString(2, fName.getText().trim()); pstmt.setString(3, fFather.getText().trim());
                    pstmt.setString(4, fMother.getText().trim()); pstmt.setString(5, fDistrict.getText().trim());
                    pstmt.setString(6, fAddress.getText().trim()); pstmt.setString(7, deptVal);
                    pstmt.setString(8, getComboValue(fDegree)); pstmt.setString(9, fReligion.getText().trim());
                    pstmt.setString(10, genderVal); pstmt.setString(11, fPhone.getText().trim());
                    pstmt.setString(12, fMobile.getText().trim()); pstmt.setString(13, fEmail.getText().trim());
                    pstmt.setString(14, fRoom.getText().trim()); pstmt.setString(15, getComboValue(fBlock));
                    pstmt.setString(16, fBoarderNo.getText().trim()); pstmt.setString(17, fBoarderType.getText().trim());
                    pstmt.executeUpdate();
                }

                conn.commit();
                Platform.runLater(() -> { formStatusLabel.setText("Student Added Successfully!"); clearForm(); loadStudentData(); });
            } catch (SQLException e) {
                Platform.runLater(() -> formStatusLabel.setText("Error adding student: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    protected void onUpdateClick() {
        String id = fId.getText().trim();
        if (id.isEmpty()) { formStatusLabel.setText("Error: Student ID is required to update a record."); return; }

        new Thread(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String selectQuery = "SELECT * FROM Students WHERE studentId = ?";
                ResultSet rs;
                try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                    selectStmt.setString(1, id);
                    rs = selectStmt.executeQuery();

                    if (!rs.next()) {
                        Platform.runLater(() -> formStatusLabel.setText("Error: Student ID not found in database."));
                        return;
                    }

                    String name = resolveValue(fName.getText(), rs.getString("fullName"));
                    String father = resolveValue(fFather.getText(), rs.getString("fatherName"));
                    String mother = resolveValue(fMother.getText(), rs.getString("motherName"));
                    String district = resolveValue(fDistrict.getText(), rs.getString("homeDistrict"));
                    String address = resolveValue(fAddress.getText(), rs.getString("address"));
                    String dept = resolveValue(getComboValue(fDept), rs.getString("department"));
                    String degree = resolveValue(getComboValue(fDegree), rs.getString("degreeLevel"));
                    String religion = resolveValue(fReligion.getText(), rs.getString("religion"));
                    String gender = resolveValue(getComboValue(fGender), rs.getString("gender"));
                    String phone = resolveValue(fPhone.getText(), rs.getString("phoneNo"));
                    String mobile = resolveValue(fMobile.getText(), rs.getString("mobileNo"));
                    String email = resolveValue(fEmail.getText(), rs.getString("emailAddress"));
                    String room = resolveValue(fRoom.getText(), rs.getString("assignedRoomNumber"));
                    String block = resolveValue(getComboValue(fBlock), rs.getString("block"));
                    String boarderNo = resolveValue(fBoarderNo.getText(), rs.getString("boarderNo"));
                    String boarderType = resolveValue(fBoarderType.getText(), rs.getString("boarderType"));

                    String updateQuery = "UPDATE Students SET fullName=?, fatherName=?, motherName=?, homeDistrict=?, address=?, department=?, degreeLevel=?, religion=?, gender=?, phoneNo=?, mobileNo=?, emailAddress=?, assignedRoomNumber=?, block=?, boarderNo=?, boarderType=? WHERE studentId=?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, name); updateStmt.setString(2, father); updateStmt.setString(3, mother);
                        updateStmt.setString(4, district); updateStmt.setString(5, address); updateStmt.setString(6, dept);
                        updateStmt.setString(7, degree); updateStmt.setString(8, religion); updateStmt.setString(9, gender);
                        updateStmt.setString(10, phone); updateStmt.setString(11, mobile); updateStmt.setString(12, email);
                        updateStmt.setString(13, room); updateStmt.setString(14, block); updateStmt.setString(15, boarderNo);
                        updateStmt.setString(16, boarderType); updateStmt.setString(17, id);
                        updateStmt.executeUpdate();
                    }
                }
                Platform.runLater(() -> { formStatusLabel.setText("Student Record Updated!"); clearForm(); loadStudentData(); });
            } catch (SQLException e) {
                Platform.runLater(() -> formStatusLabel.setText("Error updating record."));
            }
        }).start();
    }

    @FXML
    protected void onDeleteClick() {
        String id = fId.getText().trim();
        if (id.isEmpty()) { formStatusLabel.setText("Error: Student ID is required to delete."); return; }
        new Thread(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement p1 = conn.prepareStatement("DELETE FROM Students WHERE studentId = ?");
                     PreparedStatement p2 = conn.prepareStatement("DELETE FROM Users WHERE username = ?")) {
                    p1.setString(1, id); p1.executeUpdate();
                    p2.setString(1, id); p2.executeUpdate();
                }
                conn.commit();
                Platform.runLater(() -> { formStatusLabel.setText("Student Deleted!"); clearForm(); loadStudentData(); });
            } catch (SQLException e) { Platform.runLater(() -> formStatusLabel.setText("Error deleting student.")); }
        }).start();
    }

    private String resolveValue(String input, String dbValue) {
        input = input.trim();
        return input.isEmpty() ? dbValue : input;
    }
}