package com.example.loren.altklausurenneu;

public class Exam {
    private String name;
    private String semester;

    public Exam(String name, String semester) {
        this.name = name;
        this.semester = semester;
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
