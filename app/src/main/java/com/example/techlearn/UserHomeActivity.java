package com.example.techlearn;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.techlearn.Adapter.CourseUserAdapter;
import com.example.techlearn.Model.CourseModel;
import com.example.techlearn.databinding.ActivityUserHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity {

    private ActivityUserHomeBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Dialog loadingDialog;
    private ArrayList<CourseModel> courseList;
    private CourseUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize Firebase and Dialog
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        setupLoadingDialog();

        // Initialize RecyclerView
        courseList = new ArrayList<>();
        setupRecyclerView();

        // Load courses from Firebase
        loadCoursesFromFirebase();

//        // Set up SearchView functionality
//        setupSearchView();
//
//        // Set up Spinner for filtering
//        setupFilterSpinner();
    }

    private void setupLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rvCourse.setLayoutManager(layoutManager);
        adapter = new CourseUserAdapter(this, courseList);
        binding.rvCourse.setAdapter(adapter);

        Log.d("UserHomeActivity", "RecyclerView initialized with adapter");
    }

    private void loadCoursesFromFirebase() {
        loadingDialog.show();
        database.getReference("course")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CourseModel model = dataSnapshot.getValue(CourseModel.class);

                            if (model != null) {
                                Log.d("UserHomeActivity", "Course Title: " + model.getTitle());
                                courseList.add(model);
                            }
                        }

                        Log.d("UserHomeActivity", "Total Courses Loaded: " + courseList.size());

                        // ✅ Log before calling notifyDataSetChanged()
                        Log.d("UserHomeActivity", "Calling notifyDataSetChanged()");
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();

                        if (courseList.isEmpty()) {
                            Toast.makeText(UserHomeActivity.this, "No courses available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                        Toast.makeText(UserHomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void loadCoursesForAdmin(String currentUserId) {
        database.getReference("course")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CourseModel model = dataSnapshot.getValue(CourseModel.class);
                            if (model != null && model.getPostedBy() != null) {
                                // Log course details
                                Log.d("UserHomeActivity", "Course Name: " + model.getTitle() +
                                        ", Posted by: " + model.getPostedBy());

                                // Exclude courses posted by the Admin
//                                if (!model.getPostedBy().equals(currentUserId)) {
                                    courseList.add(model);
//                                }
                            } else {
                                Log.d("UserHomeActivity", "Null model or missing postedBy field.");
                            }
                        }

                        // Debug the final course list size
                        Log.d("UserHomeActivity", "Total courses loaded: " + courseList.size());

                        updateUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                        Toast.makeText(UserHomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadCoursesForUser() {
        database.getReference("course")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CourseModel model = dataSnapshot.getValue(CourseModel.class);
                            if (model != null) {
                                courseList.add(model);
                            }
                        }
                        updateUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                        Toast.makeText(UserHomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void setupSearchView() {
//        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adapter.filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.filter(newText);
//                return false;
//            }
//        });
//    }

//    private void setupFilterSpinner() {
//        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
//                R.array.filter_options, android.R.layout.simple_spinner_item);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.filterSpinner.setAdapter(spinnerAdapter);
//
//        binding.filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                handleFilterSelection(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // No action needed
//            }
//        });
//    }

    private void handleFilterSelection(int position) {
        switch (position) {
            case 0:
                // All Courses
                adapter.filter("");
                break;
            case 1:
                // Enrolled Courses
                loadEnrolledCourses(true);
                break;
            case 2:
                // Not Enrolled Courses
                loadEnrolledCourses(false);
                break;
        }
    }

    private void loadEnrolledCourses(boolean enrolled) {
        String userId = auth.getCurrentUser().getUid();
        database.getReference().child("enrollments").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> enrolledCourseIds = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            enrolledCourseIds.add(dataSnapshot.getKey());
                        }
                        adapter.filterByEnrollmentStatus(enrolled, enrolledCourseIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserHomeActivity.this, "Error loading enrollments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI() {
        adapter.notifyDataSetChanged();
        loadingDialog.dismiss();
        if (courseList.isEmpty()) {
            Toast.makeText(UserHomeActivity.this, "No courses available", Toast.LENGTH_SHORT).show();
        }
        loadingDialog.dismiss();
    }

}
