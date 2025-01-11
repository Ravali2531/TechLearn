package com.example.techlearn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditCourseActivity extends AppCompatActivity {

    private EditText editCourseTitle, editCourseDescription, editCoursePrice, editCourseDuration;
    private Button saveCourseButton;
    private String postId;
    private DatabaseReference courseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        // Set up Toolbar with Back button
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        // Initialize Views
        editCourseTitle = findViewById(R.id.editCourseTitle);
        editCourseDescription = findViewById(R.id.editCourseDescription);
        editCoursePrice = findViewById(R.id.editCoursePrice);
        editCourseDuration = findViewById(R.id.editCourseDuration);
        saveCourseButton = findViewById(R.id.saveCourseButton);

        // Get postId from Intent
        postId = getIntent().getStringExtra("postId");

        // Firebase reference
        courseRef = FirebaseDatabase.getInstance().getReference().child("course").child(postId);

        // Load existing course details
        loadCourseDetails();

        // Save button click listener
        saveCourseButton.setOnClickListener(v -> saveCourseDetails());
    }

    // Handle Back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Navigate back to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Load existing course details from Firebase
    private void loadCourseDetails() {
        courseRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Populate the EditText fields with current values
                    editCourseTitle.setText(snapshot.child("title").getValue(String.class));
                    editCourseDescription.setText(snapshot.child("description").getValue(String.class));
                    editCoursePrice.setText(String.valueOf(snapshot.child("price").getValue(Long.class)));
                    editCourseDuration.setText(snapshot.child("duration").getValue(String.class));
                } else {
                    Toast.makeText(EditCourseActivity.this, "Course not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(EditCourseActivity.this, "Error loading course: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save updated course details to Firebase
    private void saveCourseDetails() {
        // Get input values
        String title = editCourseTitle.getText().toString().trim();
        String description = editCourseDescription.getText().toString().trim();
        String priceString = editCoursePrice.getText().toString().trim();
        String duration = editCourseDuration.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(duration)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert price to a number
        long price;
        try {
            price = Long.parseLong(priceString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map with the fields to be updated
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("description", description);
        updates.put("price", price);
        updates.put("duration", duration);

        // Update the course in Firebase
        courseRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditCourseActivity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();

                // Set the result to indicate success and include updated values
                Intent resultIntent = new Intent();
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("description", description);
                resultIntent.putExtra("price", price);
                resultIntent.putExtra("duration", duration);
                setResult(RESULT_OK, resultIntent);

                finish();  // Close the activity
            } else {
                Toast.makeText(EditCourseActivity.this, "Failed to update course", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
