package com.example.loren.altklausurenneu;

//todo clear this class

public class Exam {
    private String name;
    private String semester;
    private String category;
    private String userid;
    private String filepath;









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

    public Exam(String name, String semester, String category, String userid, String filepath) {
        this.name = name;
        this.semester = semester;
        this.category = category;
        this.userid = userid;
        this.filepath = filepath;

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

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getUserid() {

        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
