package com.example.todolist;

public class AuthUser {
    public static String username, name, password, gender, userId;

    public AuthUser() {
    }

    public AuthUser(String name, String gender, String username, String password, String userId) {
        AuthUser.name = name;
        AuthUser.gender = gender;
        AuthUser.username = username;
        AuthUser.password = password;
        AuthUser.userId = userId;
    }

}
