//Retrieves all student users to show to the admin

package com.example.authendance;

public class StudentUsers {

    private String student_id;
    private String name;

    public StudentUsers(String student_id, String name) {
        this.student_id = student_id;
        this.name = name;
    }

    public StudentUsers() {
    }

    public String getStudent_id() {
        return student_id;
    }

    public String getName() {
        return name;
    }
}
