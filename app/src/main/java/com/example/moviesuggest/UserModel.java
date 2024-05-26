package com.example.moviesuggest;

public class UserModel {
    String name,email,username,password;
    public UserModel() {
    }
    public UserModel(String n, String e, String u, String p){
        name = n;
        email = e;
        username = u;
        password = p;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPass() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPass(String pass) {
        this.password = pass;
    }
}