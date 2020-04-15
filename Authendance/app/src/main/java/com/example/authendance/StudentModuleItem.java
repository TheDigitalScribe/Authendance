package com.example.authendance;

//Object class for the student modules to be displayed in a RecyclerView
public class StudentModuleItem {
    private String name;

    public StudentModuleItem(String name) {
        this.name = name;
    }

    public StudentModuleItem() {
        //Empty constructor
    }

    public String getName() {
        return name;
    }
}
