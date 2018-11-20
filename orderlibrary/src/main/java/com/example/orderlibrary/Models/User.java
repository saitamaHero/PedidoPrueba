package com.example.orderlibrary.Models;

public class User  {
    private String user;
    private String password;
    private char level;
    private Vendor vendor;

    public User(String user, char level, Vendor vendor) {
        this.user = user;
        this.level = level;
        this.vendor = vendor;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public char getLevel() {
        return level;
    }

    public void setLevel(char level) {
        this.level = level;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}
