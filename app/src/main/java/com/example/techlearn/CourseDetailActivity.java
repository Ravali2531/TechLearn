//package com.example.techlearn;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.example.techlearn.Model.CourseModel;
//import com.example.techlearn.Model.UserModel;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CourseDetailActivity extends AppCompatActivity {
//
//    private static final int EDIT_COURSE_REQUEST_CODE = 100;
//
//    private ImageView courseThumbnail;
//    private TextView courseTitle, courseDescription, coursePrice, courseDuration;
//    private FloatingActionButton fabEditCourse;
//    private MaterialButton uploadPlaylist, viewEnrolledMembersButton;
//
//    private String postId;
//    private FirebaseDatabase database;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_course_detail);
//
//        // Initialize Toolbar
//        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("Course Details");
//        }
//
//        // Initialize Views
//        courseThumbnail = findViewById(R.id.courseThumbnail);
//        courseTitle = findViewById(R.id.courseTitle);
//        courseDescription = findViewById(R.id.courseDescription);
//        coursePrice = findViewById(R.id.coursePrice);
//        courseDuration = findViewById(R.id.courseDuration);
//        fabEditCourse = findViewById(R.id.fabEditCourse);
//        uploadPlaylist = findViewById(R.id.uploadPlaylist);
//        viewEnrolledMembersButton = findViewById(R.id.viewEnrolledMembersButton);
//
//        // Get postId from Intent
//        postId = getIntent().getStringExtra("postId");
//        database = FirebaseDatabase.getInstance();
//
//        // Load course details
//        loadCourseDetails();
//
//        // FAB Edit button
//        fabEditCourse.setOnClickListener(view -> {
//            Intent intent = new Intent(CourseDetailActivity.this, EditCourseActivity.class);
//            intent.putExtra("postId", postId);
//            startActivityForResult(intent, EDIT_COURSE_REQUEST_CODE);
//        });
//
//        // Upload Playlist button
//        uploadPlaylist.setOnClickListener(view -> {
//            Intent intent = new Intent(CourseDetailActivity.this, UploadPlayListActivity.class);
//            intent.putExtra("postId", postId);
//            startActivity(intent);
//        });
//
//        // View Enrolled Members button
//        viewEnrolledMembersButton.setOnClickListener(view -> {
//            Intent intent = new Intent(CourseDetailActivity.this, EnrolledMembers.class);
//            intent.putExtra("postId", postId);
//            startActivity(intent);
//        });
//    }
//
//    private void loadCourseDetails() {
//        database.getReference().child("course").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    CourseModel course = snapshot.getValue(CourseModel.class);
//
//                    if (course != null) {
//                        Glide.with(CourseDetailActivity.this).load(course.getThumbnail()).into(courseThumbnail);
//                        courseTitle.setText(course.getTitle());
//                        courseDescription.setText(course.getDescription());
//                        coursePrice.setText("$" + course.getPrice());
//                        courseDuration.setText(course.getDuration() + " hrs");
//
//                        if (getSupportActionBar() != null) {
//                            getSupportActionBar().setTitle(course.getTitle());
//                        }
//                    }
//                } else {
//                    Toast.makeText(CourseDetailActivity.this, "Course not found", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(CourseDetailActivity.this, "Error loading course: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == EDIT_COURSE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            courseTitle.setText(data.getStringExtra("title"));
//            courseDescription.setText(data.getStringExtra("description"));
//            coursePrice.setText("$" + data.getLongExtra("price", 0));
//            courseDuration.setText(data.getStringExtra("duration") + " hrs");
//
//            if (getSupportActionBar() != null) {
//                getSupportActionBar().setTitle(data.getStringExtra("title"));
//            }
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}


package com.example.techlearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.techlearn.Model.CourseModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseDetailActivity extends AppCompatActivity {

    private static final int EDIT_COURSE_REQUEST_CODE = 100;

    private ImageView courseThumbnail;
    private TextView courseTitle, courseDescription, coursePrice, courseDuration;
    private FloatingActionButton fabEditCourse;
    private MaterialButton uploadPlaylist, viewEnrolledMembersButton, deleteCourseButton;

    private String postId;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Initialize Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Course Details");
        }

        // Initialize Views
        courseThumbnail = findViewById(R.id.courseThumbnail);
        courseTitle = findViewById(R.id.courseTitle);
        courseDescription = findViewById(R.id.courseDescription);
        coursePrice = findViewById(R.id.coursePrice);
        courseDuration = findViewById(R.id.courseDuration);
        fabEditCourse = findViewById(R.id.fabEditCourse);
        uploadPlaylist = findViewById(R.id.uploadPlaylist);
        viewEnrolledMembersButton = findViewById(R.id.viewEnrolledMembersButton);
        deleteCourseButton = findViewById(R.id.deleteCourseButton);
        deleteCourseButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));;

        // Get postId from Intent
        postId = getIntent().getStringExtra("postId");
        database = FirebaseDatabase.getInstance();

        // Load course details
        loadCourseDetails();

        // FAB Edit button
        fabEditCourse.setOnClickListener(view -> {
            Intent intent = new Intent(CourseDetailActivity.this, EditCourseActivity.class);
            intent.putExtra("postId", postId);
            startActivityForResult(intent, EDIT_COURSE_REQUEST_CODE);
        });

        // Upload Playlist button
        uploadPlaylist.setOnClickListener(view -> {
            Intent intent = new Intent(CourseDetailActivity.this, UploadPlayListActivity.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });

        // View Enrolled Members button
        viewEnrolledMembersButton.setOnClickListener(view -> {
            Intent intent = new Intent(CourseDetailActivity.this, EnrolledMembers.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });

        // Delete Course button
        deleteCourseButton.setOnClickListener(view -> deleteCourse());
    }

    private void loadCourseDetails() {
        database.getReference().child("course").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CourseModel course = snapshot.getValue(CourseModel.class);

                    if (course != null) {
                        Glide.with(CourseDetailActivity.this).load(course.getThumbnail()).into(courseThumbnail);
                        courseTitle.setText(course.getTitle());
                        courseDescription.setText(course.getDescription());
                        coursePrice.setText("$" + course.getPrice());
                        courseDuration.setText(course.getDuration() + " hrs");

                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(course.getTitle());
                        }
                    }
                } else {
                    Toast.makeText(CourseDetailActivity.this, "Course not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourseDetailActivity.this, "Error loading course: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCourse() {
        // Delete course
        database.getReference().child("course").child(postId).removeValue();
        // Delete course reviews
        database.getReference().child("course_reviews").child(postId).removeValue();
        // Delete enrollments
        database.getReference().child("enrollments").child(postId).removeValue();

        Toast.makeText(CourseDetailActivity.this, "Course deleted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_COURSE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            courseTitle.setText(data.getStringExtra("title"));
            courseDescription.setText(data.getStringExtra("description"));
            coursePrice.setText("$" + data.getLongExtra("price", 0));
            courseDuration.setText(data.getStringExtra("duration") + " hrs");

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(data.getStringExtra("title"));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
