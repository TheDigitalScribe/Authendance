package com.example.authendance;


public class StudentModuleItem {
    private String module;
    private String module_lecturer;
    private String module_date;

    public StudentModuleItem(){
        this.module = "No module info";
        this.module_lecturer = "No lecturer info";
        this.module_date = "No date info";
    }
    public StudentModuleItem(String module, String module_lecturer, String module_date) {
        this.module = module;
        this.module_lecturer = module_lecturer;
        this.module_date = module_date;
    }

//    public StudentModuleItem(String module) {
//        this.module = module;
//        this.module_lecturer = "No lecturer info";
//        this.module_date = "No date info";
//    }

    public String getModule() {
        return module;
    }
    public String getModule_lecturer() {
        return module_lecturer;
    }
    public String getModule_date() {
        return module_date;
    }



}
