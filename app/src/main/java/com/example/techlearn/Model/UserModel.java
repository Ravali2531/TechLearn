package com.example.techlearn.Model;

public class UserModel {
    private String name, email, password, profile, role;

    public UserModel() {
    }

    public UserModel(String name, String email, String password, String profile, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
