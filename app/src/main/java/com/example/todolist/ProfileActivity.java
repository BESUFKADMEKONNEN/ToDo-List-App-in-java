package com.example.todolist;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends NavParent {
    private static final int IMAGE_REQUEST_CODE = 100;
    private String USER_ID;
    private EditText nameEditText, usernameEditText;
    private Spinner genderSpinner;
    private Button updateButton,cancelButton,confirmButton;
    private ImageView profileImageView;
    private TextView usernameView;
    private TextInputLayout confirmPasswordEditLayout;
    private TextInputEditText passwordEditText,confirmPasswordEditText;
    private Uri imageUri;
    AuthUser authUser;
    private  TextView userEmail;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_profile;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         authUser=new AuthUser();


        nameEditText = findViewById(R.id.nameEditText);
        genderSpinner = findViewById(R.id.genderSpinnerUpdate);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        updateButton = findViewById(R.id.updateButton);
        cancelButton = findViewById(R.id.btnCancel);
        confirmButton = findViewById(R.id.btnConfirm);
        profileImageView = findViewById(R.id.profile_image_view);
        usernameView=findViewById(R.id.usernameView);
        confirmPasswordEditLayout=findViewById(R.id.confirmPasswordInputLayout);

            if (AuthUser.gender.equals("Male")) {
                profileImageView.setImageResource(R.drawable.ic_male);  // Replace with your default image
            } else if (AuthUser.gender.equals("Female")) {
                profileImageView.setImageResource(R.drawable.ic_female);  // Replace with your default image
            }


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);


        USER_ID=AuthUser.userId;
        nameEditText.setText(AuthUser.name);
        if (AuthUser.gender != null) {
            int genderPos = AuthUser.gender.equals("Male") ? 1 : AuthUser.gender.equals("Female") ? 2 : 0;
            genderSpinner.setSelection(genderPos); // Default to "Sex" if gender is null or invalid
        } else {
            genderSpinner.setSelection(0);
        }


         usernameEditText.setText(AuthUser.username);
         usernameView.setText(AuthUser.username);
         usernameView.setHint("You Can't Change The Username!");
        passwordEditText.setText(AuthUser.password);
        confirmPasswordEditText.setText(AuthUser.password);


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeComponents(true);
                confirmButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);
                confirmPasswordEditText.setVisibility(View.VISIBLE);
                confirmPasswordEditLayout.setVisibility(View.VISIBLE);
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeComponents(false);
                confirmButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                updateButton.setVisibility((View.VISIBLE));
                confirmPasswordEditText.setVisibility(View.INVISIBLE);
                confirmPasswordEditLayout.setVisibility(View.GONE);

            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String gender = genderSpinner.getSelectedItem().toString();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (name.isEmpty()  || gender.equals("Select Gender")) {
                    Toast.makeText(ProfileActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (gender.equals("Sex")) {
                    Toast.makeText(ProfileActivity.this, "Sex cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(ProfileActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                String encodedImage = null;
                if (imageUri != null) {
                    encodedImage = encodeImageToBase64(imageUri);

                }

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                String userId = AuthUser.userId;
                if (userId != null) {
                    AuthUser authUser = new AuthUser(name,gender,username,password,userId);
                    User newUser = new User(name, gender, username, hashedPassword);
                    database.child("users").child(userId).setValue(newUser)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                    changeComponents(false);
                                    confirmButton.setVisibility(View.INVISIBLE);
                                    cancelButton.setVisibility(View.INVISIBLE);
                                    updateButton.setVisibility((View.VISIBLE));
                                    confirmPasswordEditText.setVisibility(View.INVISIBLE);
                                    confirmPasswordEditLayout.setVisibility(View.GONE);
                                    refreshPage();
                                    startActivity(new Intent(ProfileActivity.this,Home.class));
                                    finish();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }


        });}



    private void refreshPage() {
        finish();
        startActivity(getIntent());
    }

    private void changeComponents(boolean value) {
        nameEditText.setEnabled(value);
        genderSpinner.setEnabled(value);
        passwordEditText.setEnabled(value);
        confirmPasswordEditText.setEnabled(value);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                try {
                    // Load the image into a Bitmap
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);

                    // Display the image in the ImageView
                    profileImageView.setImageBitmap(selectedImage);

                    // Optionally encode to Base64
                    String encodedImage = encodeImageToBase64(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
