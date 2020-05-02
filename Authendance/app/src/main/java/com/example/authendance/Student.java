//Model class to show a list of the students that have attended a module

package com.example.authendance;

public class Student {

    private String student_id;

    public Student(String student_id) {
        this.student_id = student_id;
    }

    public Student() {

        this.student_id = "No information available";
    }

    public String getStudent_id() {
        return student_id;
    }
}
