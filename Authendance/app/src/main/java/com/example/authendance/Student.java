package com.example.authendance;

import android.widget.ImageView;

public class Student {

    private String studentID;
    private int imageResource;

    public Student(String studentID, int imageResource) {
        this.studentID = studentID;
        this.imageResource = imageResource;
    }

    public Student() {
    }

    public String getStudentID() {
        return studentID;
    }

    public int getImageResource() {
        return imageResource;
    }
}
