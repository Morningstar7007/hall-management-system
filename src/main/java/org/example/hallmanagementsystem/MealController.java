package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.example.hallmanagementsystem.database.DatabaseConnection;
import org.example.hallmanagementsystem.models.MealRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MealController {

    @FXML private Label noticeLabel, studentInfoLabel, monthYearLabel, statusMessageLabel;
    @FXML private TextField rollField;
    @FXML private DatePicker fromDatePicker, toDatePicker;
    @FXML private ToggleGroup mealStatusGroup;
    @FXML private GridPane calendarGrid;

    private YearMonth currentYearMonth;
    private final String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        LocalDate allowedDate = LocalDate.now().plusDays(2);
        noticeLabel.setText("You can off/on your meal from or later date of " + allowedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (UserSession.loggedInUsername != null) {
            rollField.setText(UserSession.loggedInUsername);
            fetchStudentInfo(UserSession.loggedInUsername);
            refreshCalendar();
        }
    }

    private void fetchStudentInfo(String studentId) {
        new Thread(() -> {
            String query = "SELECT fullName, department FROM Students WHERE studentId = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("fullName");
                    String dept = rs.getString("department");
                    Platform.runLater(() -> studentInfoLabel.setText("Student Name: " + name + ", Department: " + dept));
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }).start();
    }

    @FXML
    protected void onPrevMonthClick() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        refreshCalendar();
    }

    @FXML
    protected void onNextMonthClick() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        refreshCalendar();
    }

    private void refreshCalendar() {
        monthYearLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        new Thread(() -> {
            List<MealRecord> records = fetchMealRecords(UserSession.loggedInUsername);
            Platform.runLater(() -> drawCalendarGrid(records));
        }).start();
    }

    private List<MealRecord> fetchMealRecords(String studentId) {
        List<MealRecord> records = new ArrayList<>();
        String query = "SELECT fromDate, toDate, status FROM MealRecords WHERE studentId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(new MealRecord(
                        LocalDate.parse(rs.getString("fromDate")),
                        LocalDate.parse(rs.getString("toDate")),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return records;
    }

    private void drawCalendarGrid(List<MealRecord> records) {
        calendarGrid.getChildren().clear();

        // 1. CLEAR AND FORCE PERFECTLY EVEN COLUMNS & ROWS
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        // Enforce exactly 14.28% width for all 7 columns
        for (int i = 0; i < 7; i++) {
            javafx.scene.layout.ColumnConstraints colConst = new javafx.scene.layout.ColumnConstraints();
            colConst.setPercentWidth(100.0 / 7.0);
            calendarGrid.getColumnConstraints().add(colConst);
        }

        // Enforce consistent row heights (40px for header, 50px for days)
        for (int i = 0; i < 7; i++) {
            javafx.scene.layout.RowConstraints rowConst = new javafx.scene.layout.RowConstraints();
            rowConst.setMinHeight(i == 0 ? 40 : 50);
            calendarGrid.getRowConstraints().add(rowConst);
        }

        // Draw Day Headers (Sun, Mon, Tue...)
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5; -fx-font-size: 14px;");
            StackPane headerPane = new StackPane(dayLabel);
            headerPane.setStyle("-fx-background-color: #FFFFFF;");
            headerPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Force to completely fill the grid cell
            calendarGrid.add(headerPane, i, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int col = firstOfMonth.getDayOfWeek().getValue() % 7;
        int row = 1;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        // Draw empty spaces before the 1st
        for (int i = 0; i < col; i++) {
            StackPane emptyPane = new StackPane();
            emptyPane.setStyle("-fx-background-color: #F5F5F5;");
            emptyPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Force to completely fill
            calendarGrid.add(emptyPane, i, row);
        }

        // Draw the days
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = currentYearMonth.atDay(day);
            String status = determineMealStatus(currentDate, records);

            Label dayNum = new Label(String.valueOf(day));
            dayNum.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            StackPane dayPane = new StackPane(dayNum);
            dayPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Force to completely fill

            if (status.equals("ON")) {
                dayPane.setStyle("-fx-background-color: #2E7D32;"); // Green
                dayNum.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            } else if (status.equals("OFF")) {
                dayPane.setStyle("-fx-background-color: #D32F2F;"); // Red
                dayNum.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            }

            calendarGrid.add(dayPane, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }

        // Fill remaining grid spaces after the last day of the month
        while (row < 7) {
            StackPane emptyPane = new StackPane();
            emptyPane.setStyle("-fx-background-color: #F5F5F5;");
            emptyPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Force to completely fill
            calendarGrid.add(emptyPane, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    private String determineMealStatus(LocalDate targetDate, List<MealRecord> records) {
        for (int i = records.size() - 1; i >= 0; i--) {
            MealRecord record = records.get(i);
            if (!targetDate.isBefore(record.getFromDate()) && !targetDate.isAfter(record.getToDate())) {
                return record.getStatus();
            }
        }
        // DEFAULT TO "ON" IF NO RECORD IS FOUND
        return "ON";
    }

    @FXML
    protected void onUpdateClick() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate == null || toDate == null) {
            statusMessageLabel.setText("Please select both 'From' and 'To' dates.");
            return;
        }
        if (toDate.isBefore(fromDate)) {
            statusMessageLabel.setText("Error: 'To' date cannot be before 'From' date.");
            return;
        }

        RadioButton selectedRadio = (RadioButton) mealStatusGroup.getSelectedToggle();
        String status = selectedRadio.getText();
        String studentId = UserSession.loggedInUsername;

        new Thread(() -> {
            String query = "INSERT INTO MealRecords (studentId, fromDate, toDate, status) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, studentId);
                pstmt.setString(2, fromDate.toString());
                pstmt.setString(3, toDate.toString());
                pstmt.setString(4, status);
                pstmt.executeUpdate();

                Platform.runLater(() -> {
                    statusMessageLabel.setStyle("-fx-text-fill: #2E7D32;"); // Success text is green
                    statusMessageLabel.setText("Successfully updated meal status to " + status + ".");
                    refreshCalendar();
                });

            } catch (SQLException e) {
                Platform.runLater(() -> {
                    statusMessageLabel.setStyle("-fx-text-fill: #D32F2F;"); // Error text is red
                    statusMessageLabel.setText("Database Error: Could not save meal status.");
                });
            }
        }).start();
    }
}