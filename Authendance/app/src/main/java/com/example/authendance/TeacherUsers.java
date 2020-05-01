package com.example.authendance;

public class TeacherUsers {

    private String teacher_id;
    private String name;

    public TeacherUsers(String teacher_id, String name) {
        this.teacher_id = teacher_id;
        this.name = name;
    }

    public TeacherUsers() {
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public String getName() {
        return name;
    }
}
