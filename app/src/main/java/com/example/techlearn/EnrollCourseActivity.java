////package com.example.techlearn;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.recyclerview.widget.GridLayoutManager;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import android.os.Bundle;
////import android.util.Log;
////import android.widget.Toast;
////
////import com.example.techlearn.Adapter.CourseUserAdapter;
////import com.example.techlearn.Adapter.EnrollCourseAdapter;
////import com.example.techlearn.Model.CourseModel;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////
////import java.util.ArrayList;
////
////public class EnrollCourseActivity extends AppCompatActivity {
////
////    private RecyclerView recyclerView;
////    private EnrollCourseAdapter adapter;
////    private ArrayList<CourseModel> courseList;
////    private FirebaseAuth auth;
////    private FirebaseDatabase database;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_enroll_course);
////        recyclerView = findViewById(R.id.rvCourses);
////// Set GridLayoutManager with 2 columns
////        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
////
////
////        courseList = new ArrayList<>();
////        adapter = new EnrollCourseAdapter(this, courseList);
////        recyclerView.setAdapter(adapter);
////
////        auth = FirebaseAuth.getInstance();
////        database = FirebaseDatabase.getInstance();
////
////        loadCourses();
////    }
////    private void loadCourses() {
////        String currentUserId = auth.getUid();
////
////        database.getReference().child("course").addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                courseList.clear();
////                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
////                    CourseModel course = snapshot.getValue(CourseModel.class);
////
////                    if (course != null) {
////                        // Set the postId from the DataSnapshot key
////                        course.setPostId(snapshot.getKey());
////
////                        if (!course.getPostedBy().equals(currentUserId)) {
////                            courseList.add(course);
////                        }
////                    }
////                }
////                adapter.notifyDataSetChanged();
////
////                if (courseList.isEmpty()) {
////                    Toast.makeText(EnrollCourseActivity.this, "No courses available to enroll", Toast.LENGTH_SHORT).show();
////                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////                Toast.makeText(EnrollCourseActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////
////
////
////}
//
//package com.example.techlearn;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import com.example.techlearn.Adapter.EnrollCourseAdapter;
//import com.example.techlearn.Model.CourseModel;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EnrollCourseActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private EnrollCourseAdapter adapter;
//    private ArrayList<CourseModel> courseList;
//    private FirebaseAuth auth;
//    private FirebaseDatabase database;
//    private androidx.appcompat.widget.SearchView searchView;
//    private Spinner filterSpinner;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_enroll_course);
//
//        // Initialize Firebase
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//
//        // Initialize Views
//        recyclerView = findViewById(R.id.rvCourses);
//        searchView = findViewById(R.id.searchView);
//        filterSpinner = findViewById(R.id.filterSpinner);
//
//        // Set up RecyclerView with GridLayoutManager
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        courseList = new ArrayList<>();
//        adapter = new EnrollCourseAdapter(this, courseList);
//        recyclerView.setAdapter(adapter);
//
//        // Load courses from Firebase
//        loadCourses();
//
//        // Set up SearchView functionality
//        setupSearchView();
//
//        // Set up Filter Spinner functionality
//        setupFilterSpinner();
//    }
//
//    /**
//     * Loads courses from Firebase Realtime Database.
//     */
//    private void loadCourses() {
//        String currentUserId = auth.getUid();
//
//        database.getReference().child("course").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                courseList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    CourseModel course = snapshot.getValue(CourseModel.class);
//
//                    if (course != null) {
//                        // Set the postId from the DataSnapshot key
//                        course.setPostId(snapshot.getKey());
//
//                        // Exclude the current user's own courses
//                        if (!course.getPostedBy().equals(currentUserId)) {
//                            courseList.add(course);
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged();
//
//                if (courseList.isEmpty()) {
//                    Toast.makeText(EnrollCourseActivity.this, "No courses available to enroll", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(EnrollCourseActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    /**
//     * Sets up the SearchView to filter courses based on user input.
//     */
//    private void setupSearchView() {
//        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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
//
//    /**
//     * Sets up the Filter Spinner with options and applies filters accordingly.
//     */
//    private void setupFilterSpinner() {
//        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
//                R.array.filter_options, android.R.layout.simple_spinner_item);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        filterSpinner.setAdapter(spinnerAdapter);
//
//        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
//
//    /**
//     * Handles the filter selection based on the position of the spinner.
//     */
//    private void handleFilterSelection(int position) {
//        switch (position) {
//            case 0:
//                // All Courses
//                adapter.filter("");
//                break;
//
//            case 1:
//                // Enrolled Courses
//                loadEnrolledCourses(true);
//                break;
//
//            case 2:
//                // Not Enrolled Courses
//                loadEnrolledCourses(false);
//                break;
//        }
//    }
//
//    /**
//     * Loads the user's enrolled courses and filters them based on the selection.
//     *
//     * @param enrolled true for enrolled courses, false for not enrolled courses
//     */
//    private void loadEnrolledCourses(boolean enrolled) {
//        String userId = auth.getCurrentUser().getUid();
//        database.getReference().child("enrollments").child(userId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        List<String> enrolledCourseIds = new ArrayList<>();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            enrolledCourseIds.add(dataSnapshot.getKey());
//                        }
//                        adapter.filterByEnrollmentStatus(enrolled, enrolledCourseIds);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(EnrollCourseActivity.this, "Error loading enrollments", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}
//


package com.example.techlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.techlearn.Adapter.EnrollCourseAdapter;
import com.example.techlearn.Model.CourseModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EnrollCourseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EnrollCourseAdapter adapter;
    private ArrayList<CourseModel> courseList;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private androidx.appcompat.widget.SearchView searchView;
    private Spinner filterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_course);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Initialize Views
        recyclerView = findViewById(R.id.rvCourses);
        searchView = findViewById(R.id.searchView);
        filterSpinner = findViewById(R.id.filterSpinner);

        // Set up RecyclerView with GridLayoutManager
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        courseList = new ArrayList<>();
        adapter = new EnrollCourseAdapter(this, courseList);
        recyclerView.setAdapter(adapter);

        // Load courses from Firebase
        loadCourses();

        // Set up SearchView functionality
        setupSearchView();

        // Set up Filter Spinner functionality
        setupFilterSpinner();
    }

    private void loadCourses() {
        String currentUserId = auth.getUid();

        database.getReference().child("course").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CourseModel course = snapshot.getValue(CourseModel.class);

                    if (course != null) {
                        // Set the postId from the DataSnapshot key
                        course.setPostId(snapshot.getKey());

                        // Handle rating field dynamically (string or double)
                        Object ratingValue = snapshot.child("rating").getValue();
                        if (ratingValue instanceof Long) {
                            course.setRating(String.valueOf(ratingValue));
                        } else if (ratingValue instanceof Double) {
                            course.setRating(String.format("%.1f", ratingValue));
                        } else if (ratingValue instanceof String) {
                            course.setRating((String) ratingValue);
                        } else {
                            course.setRating("N/A");
                        }
                        // Exclude the current user's own courses
                        if (!course.getPostedBy().equals(currentUserId)) {
                            courseList.add(course);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

                if (courseList.isEmpty()) {
                    Toast.makeText(EnrollCourseActivity.this, "No courses available to enroll", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EnrollCourseActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupSearchView() {
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
    }

    private void setupFilterSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleFilterSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

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
                        Toast.makeText(EnrollCourseActivity.this, "Error loading enrollments", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
