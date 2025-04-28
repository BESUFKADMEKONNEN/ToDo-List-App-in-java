package com.example.todolist;
public class Task {
    public String title;
    public String description;
    public String dueDateTime;
    public String recordedDateTime;
    public Boolean isChecked;
    public String userId;

    public Task() {} // Needed for Firebase

    public Task(String title, String description, String dueDateTime, String recordedDateTime, Boolean isChecked, String userId) {
        this.title = title;
        this.description = description;
        this.dueDateTime = dueDateTime;
        this.recordedDateTime = recordedDateTime;
        this.isChecked = isChecked;
        this.userId = userId;
    }
}
