package com.example.b07demosummer2024;

public class User {
    private String user;
    private String pass;

    public User(){

    }

    public User(String username, String pass){
        this.user = username;
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }
    public String getPass() {
        return pass;
    }

}
