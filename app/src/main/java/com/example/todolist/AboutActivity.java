package com.example.todolist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends NavParent {

    private TextView appDescriptionTextView;
    private TextView developerInfoTextView;
    private TextView versionInfoTextView;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_about;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the TextViews
        appDescriptionTextView = findViewById(R.id.appDescriptionTextView);
        developerInfoTextView = findViewById(R.id.developerInfoTextView);
        versionInfoTextView = findViewById(R.id.versionInfoTextView);

        // Set the content for the TextViews
        appDescriptionTextView.setText("TaskX is a powerful and intuitive to-do list app designed to help you stay organized, manage your tasks efficiently, and boost your productivity. Whether it's daily chores, assignments, or long-term goals — TaskX has got you covered.");

        developerInfoTextView.setText("Developer: Helina Anteneh\nEmail: helinaAnteneh@taskxapp.com");

        versionInfoTextView.setText("Version: 1.0.0");
    }

}
