package org.example.hallmanagementsystem.models;

public class AdminPaymentRecord {
    private int sl;
    private String studentId;
    private String fromMonthYear;
    private String toMonthYear;
    private double messing;
    private double feast;
    private double fine;
    private double total;

    public AdminPaymentRecord(int sl, String studentId, String fromMonthYear, String toMonthYear, double messing, double feast, double fine, double total) {
        this.sl = sl;
        this.studentId = studentId;
        this.fromMonthYear = fromMonthYear;
        this.toMonthYear = toMonthYear;
        this.messing = messing;
        this.feast = feast;
        this.fine = fine;
        this.total = total;
    }

    public int getSl() { return sl; }
    public String getStudentId() { return studentId; }
    public String getFromMonthYear() { return fromMonthYear; }
    public String getToMonthYear() { return toMonthYear; }
    public double getMessing() { return messing; }
    public double getFeast() { return feast; }
    public double getFine() { return fine; }
    public double getTotal() { return total; }
}

