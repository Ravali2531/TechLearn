package com.example.techlearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.techlearn.Adapter.CourseUserAdapter;
import com.example.techlearn.Model.CourseModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EnrollCourseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseUserAdapter adapter;
    private ArrayList<CourseModel> courseList;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_course);

        recyclerView = findViewById(R.id.rvCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseList = new ArrayList<>();
        adapter = new CourseUserAdapter(this, courseList);
        recyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadCourses();
    }

    private void loadCourses() {
        String currentUserId = auth.getUid();

        database.getReference().child("course").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courseList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CourseModel course = snapshot.getValue(CourseModel.class);

                    if (course != null&& !course.getPostedBy().equals(currentUserId)) {
                        courseList.add(course);
                    }
                }
                adapter.notifyDataSetChanged();

                if (courseList.isEmpty()) {
                    Toast.makeText(EnrollCourseActivity.this, "No courses available to enroll", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EnrollCourseActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
