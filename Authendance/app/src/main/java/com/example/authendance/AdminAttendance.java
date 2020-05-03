//Model class for showing all the attendance date records to the admin

package com.example.authendance;

public class AdminAttendance {

    private String date;
    private Integer attendance;

    public AdminAttendance(String date, Integer attendance) {
        this.date = date;
        this.attendance = attendance;
    }

    public AdminAttendance() {
        this.date = "No date available";
        this.attendance = 0;
    }

    public String getDate() {
        return date;
    }

    public Integer getAttendance(){ return attendance; }
}
