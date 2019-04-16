package com.example.mobile3.Model;

public class ReportSession {
    private String startingTime;
    private String endingTime;
    private double hoursWorked;


    public ReportSession(String startingTime, String endingTime, double hoursWorked) {
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.hoursWorked = hoursWorked;

    }

    public String getStartingTime() {
        return startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String toString() {
        return "Session: " + startingTime;
    }
}
