package com.example.techlearn;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RateAndCommentActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitButton;

    private String postId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_and_comment);

        // Set up Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Views
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitButton = findViewById(R.id.submitButton);

        // Get data from Intent
        postId = getIntent().getStringExtra("postId");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Submit button click listener
        submitButton.setOnClickListener(view -> submitRatingAndComment());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Handle back button
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void submitRatingAndComment() {
//        float rating = ratingBar.getRating();
//        String comment = commentEditText.getText().toString().trim();
//
//        if (rating == 0) {
//            Toast.makeText(this, "Please give a rating", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (comment.isEmpty()) {
//            Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Save rating and comment to Firebase
//        Map<String, Object> ratingData = new HashMap<>();
//        ratingData.put("rating", rating);
//        ratingData.put("comment", comment);
//
//        FirebaseDatabase.getInstance().getReference("course_reviews")
//                .child(postId)
//                .child(userId)
//                .setValue(ratingData)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(RateAndCommentActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e -> Toast.makeText(RateAndCommentActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show());
//    }

    private void submitRatingAndComment() {
        float rating = ratingBar.getRating();
        String comment = commentEditText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please give a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save user's rating and comment
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);
        ratingData.put("comment", comment);

        FirebaseDatabase.getInstance().getReference("course_reviews")
                .child(postId)
                .child(userId)
                .setValue(ratingData)
                .addOnSuccessListener(aVoid -> {
                    // After saving user feedback, calculate the new average rating
                    calculateAndSaveAverageRating();
                })
                .addOnFailureListener(e -> Toast.makeText(RateAndCommentActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show());
    }
    private void calculateAndSaveAverageRating() {
        FirebaseDatabase.getInstance().getReference("course_reviews")
                .child(postId)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        double totalRating = 0;
                        int ratingCount = 0;

                        // Calculate total rating and count
                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                            Float reviewRating = reviewSnapshot.child("rating").getValue(Float.class);
                            if (reviewRating != null) {
                                totalRating += reviewRating;
                                ratingCount++;
                            }
                        }

                        // Calculate average rating
                        double averageRating = totalRating / ratingCount;

                        // Save average rating to the course node
                        FirebaseDatabase.getInstance().getReference("course")
                                .child(postId)
                                .child("rating")
                                .setValue(averageRating)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RateAndCommentActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                                    // Navigate back to the previous page
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(RateAndCommentActivity.this, "Failed to update course rating", Toast.LENGTH_SHORT).show());
                    } else {
                        // Navigate back if no data exists
                        Toast.makeText(RateAndCommentActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(RateAndCommentActivity.this, "Failed to retrieve reviews", Toast.LENGTH_SHORT).show());
    }


}