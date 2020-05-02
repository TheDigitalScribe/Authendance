//Model class for showing all the attendance date records to the admin

package com.example.authendance;

public class AdminAttendance {

    private String date;

    public AdminAttendance(String date) {
        this.date = date;
    }

    public AdminAttendance() {
    }

    public String getDate() {
        return date;
    }
}
