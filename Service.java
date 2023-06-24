package com.example.passwordsmanager;

public class Service {
    private String Service;
    private String Login;
    private String Password;
    private String Notes;
    private int id;

    // creating getter and setter methods
    public String getService() {
        return Service;
    }

    public void setService(String ServiceName) {
        this.Service = ServiceName;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String Login) {
        this.Login = Login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Pass) {
        this.Password = Pass;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String Note) {
        this.Notes = Note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // constructor
    public Service(String Service, String Login, String Password, String Notes) {
        this.Service = Service;
        this.Login = Login;
        this.Password = Password;
        this.Notes = Notes;

    }
}