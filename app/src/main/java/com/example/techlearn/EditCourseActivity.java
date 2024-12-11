package com.example.techlearn;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techlearn.Model.CourseModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditCourseActivity extends AppCompatActivity {

    private EditText editCourseTitle, editCourseDescription, editCoursePrice, editCourseDuration;
    private Button saveCourseButton;
    private String postId; // Use postId instead of courseId
    private DatabaseReference courseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        // Initialize Views
        editCourseTitle = findViewById(R.id.editCourseTitle);
        editCourseDescription = findViewById(R.id.editCourseDescription);
        editCoursePrice = findViewById(R.id.editCoursePrice);
        editCourseDuration = findViewById(R.id.editCourseDuration);
        saveCourseButton = findViewById(R.id.saveCourseButton);

        // Get postId from Intent
        postId = getIntent().getStringExtra("postId");

        // Firebase reference for the course
        courseRef = FirebaseDatabase.getInstance().getReference().child("course").child(postId);

        // Load current course details
        loadCourseDetails();

        // Save Changes Button
        saveCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCourseDetails();
            }
        });
    }

    private void loadCourseDetails() {
        courseRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CourseModel course = dataSnapshot.getValue(CourseModel.class);
                    if (course != null) {
                        editCourseTitle.setText(course.getTitle());
                        editCourseDescription.setText(course.getDescription());
                        editCoursePrice.setText(String.valueOf(course.getPrice()));
                        editCourseDuration.setText(course.getDuration());
                    }
                } else {
                    Toast.makeText(EditCourseActivity.this, "Course not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                Toast.makeText(EditCourseActivity.this, "Error loading course: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCourseDetails() {
        // Validate inputs
        String title = editCourseTitle.getText().toString().trim();
        String description = editCourseDescription.getText().toString().trim();
        String priceString = editCoursePrice.getText().toString().trim();
        String duration = editCourseDuration.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(duration)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert price to appropriate data type
        long price = Long.parseLong(priceString);

        // Create CourseModel object with updated details
        CourseModel updatedCourse = new CourseModel(title, description, price, duration);

        // Save the updated course data to Firebase
        courseRef.setValue(updatedCourse)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditCourseActivity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after saving
                    } else {
                        Toast.makeText(EditCourseActivity.this, "Failed to update course", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}