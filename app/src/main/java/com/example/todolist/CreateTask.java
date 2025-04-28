package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateTask extends NavParent {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_create_task;
    }

    private EditText taskTitleEditText;
    private EditText taskDescriptionEditText;
    private Button saveButton;
    private DatePicker dueDatePicker;
    private TimePicker dueTimePicker;
    private FirebaseDatabase database;
    private DatabaseReference tasksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance();
        tasksRef = database.getReference("tasks");

        // Initialize UI elements
        taskTitleEditText = findViewById(R.id.taskTitle);
        taskDescriptionEditText = findViewById(R.id.taskDescription);
        saveButton = findViewById(R.id.saveButton);
        dueDatePicker = findViewById(R.id.dueDatePicker);
        dueTimePicker = findViewById(R.id.dueTimePicker);

        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTaskToDatabase();
            }
        });
    }


    private void saveTaskToDatabase() {
        String title = taskTitleEditText.getText().toString().trim();
        String description = taskDescriptionEditText.getText().toString().trim();

        // Get the current date in "yyyy-MM-dd HH:mm:ss" format for due date and time
        Calendar calendar = Calendar.getInstance();
        calendar.set(dueDatePicker.getYear(), dueDatePicker.getMonth(), dueDatePicker.getDayOfMonth(),
                dueTimePicker.getCurrentHour(), dueTimePicker.getCurrentMinute());
        String dueDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(CreateTask.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current date and time when the task is being recorded
        String recordedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

        // Create a unique task ID
        String taskId = tasksRef.push().getKey();

        if (taskId != null) {
            // Create a map to hold the task data
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("userId", AuthUser.userId);
            taskData.put("title", title);
            taskData.put("description", description);
            taskData.put("dueDateTime", dueDateTime); // Store the combined due date and time
            taskData.put("recordedDateTime", recordedDateTime); // Store the recorded date and time
            taskData.put("isChecked", false); // Store the "isChecked" property (default to false)

            // Store the task in the Firebase Realtime Database under the generated task ID
            tasksRef.child(taskId).setValue(taskData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateTask.this, "Task saved successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, Home.class));
                            finish(); // Close this activity and go back
                        } else {
                            Toast.makeText(CreateTask.this, "Failed to save task", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
