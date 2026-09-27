package org.example.hallmanagementsystem.models;

import java.time.LocalDate;

public class MealRecord {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String status;

    public MealRecord(LocalDate fromDate, LocalDate toDate, String status) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.status = status;
    }

    public LocalDate getFromDate() { return fromDate; }
    public LocalDate getToDate() { return toDate; }
    public String getStatus() { return status; }
}
