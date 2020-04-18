package com.example.authendance;


//Object class for the student modules to be displayed in a RecyclerView
public class StudentModuleItem {
    private String module;

    public StudentModuleItem() {
        //Empty constructor
    }

    public StudentModuleItem(String module) {
        this.module = module;
    }

    public String getModule() {
        return module;
    }


}
