package org.example.hallmanagementsystem.models;

public class PaymentRecord {
    private int sl;
    private String fromYear;
    private String fromMonth;
    private String toYear;
    private String toMonth;
    private double messing;
    private double monthlyFeast;
    private double fine;
    private double generator;
    private double waterSupply;
    private double miscellaneous;

    public PaymentRecord(int sl, String fromYear, String fromMonth, String toYear, String toMonth, double messing, double monthlyFeast, double fine, double generator, double waterSupply, double miscellaneous) {
        this.sl = sl;
        this.fromYear = fromYear;
        this.fromMonth = fromMonth;
        this.toYear = toYear;
        this.toMonth = toMonth;
        this.messing = messing;
        this.monthlyFeast = monthlyFeast;
        this.fine = fine;
        this.generator = generator;
        this.waterSupply = waterSupply;
        this.miscellaneous = miscellaneous;
    }

    // Getters are required for JavaFX TableView PropertyValueFactory
    public int getSl() { return sl; }
    public String getFromYear() { return fromYear; }
    public String getFromMonth() { return fromMonth; }
    public String getToYear() { return toYear; }
    public String getToMonth() { return toMonth; }
    public double getMessing() { return messing; }
    public double getMonthlyFeast() { return monthlyFeast; }
    public double getFine() { return fine; }
    public double getGenerator() { return generator; }
    public double getWaterSupply() { return waterSupply; }
    public double getMiscellaneous() { return miscellaneous; }
}