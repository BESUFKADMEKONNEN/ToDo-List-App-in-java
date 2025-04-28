package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TaskDetailActivity extends NavParent {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_task_detail;
    }

    private EditText taskTitleEditText;
    private EditText taskDescriptionEditText;
    private Button saveButton, editButton,backButton;
    private DatePicker dueDatePicker;
    private TimePicker dueTimePicker;
    private CheckBox isChecked;
    private FirebaseDatabase database;
    private DatabaseReference tasksRef;
    private Boolean isFieldsEnabled = false;
    private TextView recordedDateTimeTextView;
    private String taskId; // Unique Firebase key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UI elements
        taskTitleEditText = findViewById(R.id.taskTitle);
        taskDescriptionEditText = findViewById(R.id.taskDescription);
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
        dueDatePicker = findViewById(R.id.dueDatePicker);
        dueTimePicker = findViewById(R.id.dueTimePicker);
        isChecked = findViewById(R.id.isChecked);
        recordedDateTimeTextView = findViewById(R.id.recordedDateTime);

        // Initialize Firebase database and reference
        database = FirebaseDatabase.getInstance();
        tasksRef = database.getReference("tasks");

        // Get intent data
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String dueDateTime = intent.getStringExtra("dueDateTime");
            String recordedDateTime = intent.getStringExtra("recordedDateTime");
            taskId = intent.getStringExtra("taskId");

            boolean isCheckedValue = intent.getBooleanExtra("isChecked", false); // Default value is false

            // Set the text fields
            taskTitleEditText.setText(title);
            taskDescriptionEditText.setText(description);

            // Display the recordedDateTime and isChecked values
            recordedDateTimeTextView.setText(recordedDateTime);
            isChecked.setChecked(isCheckedValue);

            // Parse and set DatePicker and TimePicker
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dueDate = sdf.parse(dueDateTime);
                Calendar calendar = Calendar.getInstance();
                if (dueDate != null) {
                    calendar.setTime(dueDate);
                    dueDatePicker.updateDate(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));

                    dueTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                    dueTimePicker.setMinute(calendar.get(Calendar.MINUTE));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setFieldsEnabled(isFieldsEnabled);
        editButton.setOnClickListener(v -> setFieldsEnabled(true));
        saveButton.setOnClickListener(v -> saveTask());
        backButton.setOnClickListener(v -> setFieldsEnabled(false));
        isChecked.setOnCheckedChangeListener((buttonView, isCheckedValue) -> {
            if (taskId != null) {
                tasksRef.child(taskId).child("isChecked").setValue(isCheckedValue)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Status updated!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
            }

        });

    }

    private void setFieldsEnabled(boolean enabled) {
        taskTitleEditText.setEnabled(enabled);
        taskDescriptionEditText.setEnabled(enabled);
        dueDatePicker.setEnabled(enabled);
        dueTimePicker.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        isFieldsEnabled = !isFieldsEnabled;
        saveButton.setVisibility(enabled?View.VISIBLE:View.GONE);
        backButton.setVisibility(enabled?View.VISIBLE:View.GONE);
        editButton.setVisibility(enabled?View.GONE:View.VISIBLE);
    }

    private void saveTask() {
        String title = taskTitleEditText.getText().toString().trim();
        String description = taskDescriptionEditText.getText().toString().trim();
        boolean completed = isChecked.isChecked();

        // Date and time
        int day = dueDatePicker.getDayOfMonth();
        int month = dueDatePicker.getMonth();
        int year = dueDatePicker.getYear();
        int hour = dueTimePicker.getHour();
        int minute = dueTimePicker.getMinute();

        Calendar dueCalendar = Calendar.getInstance();
        dueCalendar.set(year, month, day, hour, minute);

        String dueDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(dueCalendar.getTime());

        // Get current recorded date and time
        String recordedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to Firebase
        Map<String, Object> task = new HashMap<>();
        task.put("title", title);
        task.put("description", description);
        task.put("dueDateTime", dueDateTime);
        task.put("recordedDateTime", recordedDateTime);
        task.put("isChecked", completed);
        task.put("userId", AuthUser.userId);

        if (taskId != null) {
            tasksRef.child(taskId).setValue(task)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show());
        }


        setFieldsEnabled(false);
    }
}

