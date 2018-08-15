package com.example.loren.altklausurenneu;

public class Exam {
    private String name;
    private String semester;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Exam(String name, String semester, String category) {
        this.name = name;
        this.semester = semester;
        this.category = category;


    }

    //default constructor
    public Exam() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
