package com.example.authendance;

import java.util.List;

public class Student {

    private List<String> students_attended;

    public Student() {
        //Empty constructor
    }

    public Student(List<String> students_attended) {
        this.students_attended = students_attended;
    }

    public List<String> getStudents_attended() {
        return students_attended;
    }
}
