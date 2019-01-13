package com.example.loren.altklausurenneu;

public class User {
    private String UserID;
    private String EMail;
    private String Studiengang;
    private String hochschule;

    public User(String userID, String EMail, String studiengang) {
        UserID = userID;
        this.EMail = EMail;
        Studiengang = studiengang;
    }

    public User() {
    }

    public User(String userID, String EMail) {

        UserID = userID;
        this.EMail = EMail;
    }

    public User(String userID) {

        UserID = userID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getEMail() {
        return EMail;
    }

    public void setEMail(String EMail) {
        this.EMail = EMail;
    }

    public String getStudiengang() {
        return Studiengang;
    }

    public void setStudiengang(String studiengang) {
        Studiengang = studiengang;
    }

    public String getHochschule() {
        return hochschule;
    }

    public void setHochschule(String hochschule) {
        this.hochschule = hochschule;
    }
}
