package com.example.todolist;

public class User {
    public String name, username, gender, password;
//    public String name, username, gender, password, profileImageUrl;

    // Default constructor required for Firebase data deserialization
    public User() {
    }

    public User(String name, String gender, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
//        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
    }
}
