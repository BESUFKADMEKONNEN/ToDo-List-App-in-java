package com.example.todolist;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.AuthUser;
import com.example.todolist.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;



public class Home extends NavParent {
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_home;
    }

    private LinearLayout tasksContainer;
    private FirebaseDatabase database;
    private DatabaseReference tasksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        tasksRef = database.getReference("tasks");

        // Initialize UI container
        tasksContainer = findViewById(R.id.tasksContainer);

        if (tasksContainer == null) {
            // Log or show an error if tasksContainer is not found
            Log.e("Home", "tasksContainer is null. Check if the layout contains a LinearLayout with ID tasksContainer.");
            return;  // Exit early if the view is not found
        }

        // Fetch tasks from Firebase
        fetchTasksFromDatabase();
    }

    private void fetchTasksFromDatabase() {
        // Query Firebase to get all tasks for the user
        tasksRef.orderByChild("userId").equalTo(AuthUser.userId) // Match userId
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (tasksContainer == null) {
                            Log.e("Home", "tasksContainer is null during data fetch.");
                            return; // Safeguard in case tasksContainer is null during data fetch
                        }

                        tasksContainer.removeAllViews(); // Clear previous tasks in UI

                        if (!dataSnapshot.hasChildren()) {
                            Toast.makeText(Home.this, "No tasks to display!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Loop through each task in Firebase
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> taskData = (Map<String, Object>) snapshot.getValue();
                            String taskId = snapshot.getKey(); // Unique task ID

                            if (taskData != null) {
                                String taskUserId = (String) taskData.get("userId");
                                Boolean isChecked = (Boolean) taskData.get("isChecked");
                                String title = (String) taskData.get("title");
                                String description = (String) taskData.get("description");
                                String dueDateTime = (String) taskData.get("dueDateTime");
                                String recordedDateTime = (String) taskData.get("recordedDateTime");

                                // Filter tasks by isChecked value
                                if (isChecked != null && !isChecked) { // Only show tasks where isChecked is false
                                    // Inflate task view from layout
                                    View taskView = getLayoutInflater().inflate(R.layout.item_task, tasksContainer, false);

                                    TextView taskTitle = taskView.findViewById(R.id.taskTitle);
                                    TextView taskDescription = taskView.findViewById(R.id.taskDescription);
                                    TextView taskDueDate = taskView.findViewById(R.id.taskDueDate);
                                    CheckBox taskCheckbox = taskView.findViewById(R.id.taskCheckbox);

                                    taskTitle.setText(title);
                                    taskDescription.setText(description);
                                    taskDueDate.setText(dueDateTime);
                                    taskCheckbox.setChecked(isChecked);

                                    // Update checkbox in Firebase when it's clicked
                                    taskCheckbox.setOnCheckedChangeListener((buttonView, isCheckedNow) -> {
                                        if (taskId != null) {
                                            tasksRef.child(taskId).child("isChecked").setValue(isCheckedNow)
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(Home.this, isCheckedNow?"Task Completed":"Task is not Completed", Toast.LENGTH_SHORT).show();
                                                            fetchTasksFromDatabase();  // ✅ Refetch data after update
                                                        } else {
                                                            Toast.makeText(Home.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                        fetchTasksFromDatabase();
                                    });


                                    // Open TaskDetailActivity when task is clicked
                                    taskView.setOnClickListener(v -> {
                                        Intent intent = new Intent(Home.this, TaskDetailActivity.class);
                                        intent.putExtra("title", title);
                                        intent.putExtra("description", description);
                                        intent.putExtra("dueDateTime", dueDateTime);
                                        intent.putExtra("recordedDateTime", recordedDateTime);
                                        intent.putExtra("isChecked", isChecked);
                                        intent.putExtra("taskId", taskId);

                                        startActivity(intent);
                                    });

                                    // Long press to delete task with confirmation dialog
                                    taskView.setOnLongClickListener(v -> {
                                        new androidx.appcompat.app.AlertDialog.Builder(Home.this)
                                                .setTitle("Delete Task")
                                                .setMessage("Are you sure you want to delete this task?")
                                                .setIcon(R.drawable.ic_delete)  // Optional: custom icon
                                                .setPositiveButton("Delete", (dialog, which) -> {
                                                    if (taskId != null) {
                                                        tasksRef.child(taskId).removeValue()
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Task was successfully deleted
                                                                    Toast.makeText(Home.this, "Task deleted", Toast.LENGTH_SHORT).show();
                                                                    fetchTasksFromDatabase(); // Refresh UI
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    // An error occurred during the deletion
                                                                    Toast.makeText(Home.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                                                                    Log.e("FirebaseError", "Delete failed: ", e);
                                                                });
                                                    }
                                                })
                                                .setNegativeButton("Cancel", null)
                                                .show();
                                        return true; // Consume the long click event
                                    });



                                    // Add task view to container (UI)
                                    tasksContainer.addView(taskView);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Home.this, "Failed to load tasks.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}







