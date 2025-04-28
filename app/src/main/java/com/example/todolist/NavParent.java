package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public abstract class NavParent extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(getContentLayoutId());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        // Initialize DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));


        // Set up the navigation view
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Get header view to access header elements
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profile_image);
        TextView userEmail = headerView.findViewById(R.id.user_email);

        // Simulating authenticated user data
//        String haveProfileImage = AuthUser.profileImageUrl;  // Get the base64 image string
//        if (haveProfileImage != null && !haveProfileImage.isEmpty()) {
//            profileImage.setImageBitmap(decodeBase64Image());
////        } else {
            if (AuthUser.gender!=null&&AuthUser.gender.equals("Male")) {
                profileImage.setImageResource(R.drawable.ic_male);  // Replace with your default image
            } else if (AuthUser.gender.equals("Female")) {
                profileImage.setImageResource(R.drawable.ic_female);  // Replace with your default image
            }
//        }
        String authenticatedEmail = AuthUser.name;
        userEmail.setText(authenticatedEmail);

        // Set a click listener for the profile image
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up the navigation item selected listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return handleNavigation(item);
            }
        });


    }

    protected abstract int getContentLayoutId();


    // Abstract method to be implemented by child classes for navigation handling
    protected boolean handleNavigation(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            startActivity(new Intent(this,Home.class));
            finish();
        }
        else if (id == R.id.nav_create_task) {
            startActivity(new Intent(this, CreateTask.class));
//            finish();
        }
        else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
            finish();
        } else if (id == R.id.nav_finished) {
            startActivity(new Intent(this, FinishedActivity.class));
            finish();
        } else if (id == R.id.nav_help) {
            Toast.makeText(this, "Help is on development", Toast.LENGTH_SHORT).show();
//            finish();
        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_exit_app) {
            Toast.makeText(this, "Thank you for being with us.", Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
        drawerLayout.closeDrawers(); // Close the drawer after clicking
        return true;
    }

}
