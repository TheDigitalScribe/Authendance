package com.example.authendance;

public class AdminAttendance {

    private String date;
    private Integer attendance;

    public AdminAttendance(String date, Integer attendance) { this.date = date; this.attendance = attendance;}
    public AdminAttendance(String date) {
        this.date = date;
        this.attendance = 0;
    }

    public AdminAttendance() {
    }

    public String getDate() {
        return date;
    }

    public Integer getAttendance(){ return attendance; }
}
