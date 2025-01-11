package com.example.techlearn;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techlearn.Adapter.EnrolledMembersAdapter;
import com.example.techlearn.Model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EnrolledMembers extends AppCompatActivity {

    private RecyclerView enrolledMembersRecyclerView;
    private EnrolledMembersAdapter adapter;
    private List<UserModel> enrolledMembersList;
    private TextView noEnrolledMembersTextView;

    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_members);

        // Initialize Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Enrolled Members");
        }

        // Initialize Views
        enrolledMembersRecyclerView = findViewById(R.id.enrolledMembersRecyclerView);
        noEnrolledMembersTextView = findViewById(R.id.noEnrolledMembersTextView);
        enrolledMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        enrolledMembersList = new ArrayList<>();
        adapter = new EnrolledMembersAdapter(this, enrolledMembersList);
        enrolledMembersRecyclerView.setAdapter(adapter);

        // Get postId from Intent
        postId = getIntent().getStringExtra("postId");

        // Load enrolled members from Firebase
        loadEnrolledMembers();
    }

    private void loadEnrolledMembers() {
        FirebaseDatabase.getInstance().getReference("enrollments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                enrolledMembersList.clear();
                boolean hasMembers = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    if (userSnapshot.hasChild(postId)) {
                        loadUserDetails(userId);
                        hasMembers = true;
                    }
                }
                if (!hasMembers) {
                    noEnrolledMembersTextView.setVisibility(View.VISIBLE);
                    enrolledMembersRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EnrolledMembers.this, "Error loading enrolled members: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserDetails(String userId) {
        FirebaseDatabase.getInstance().getReference("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null) {
                    enrolledMembersList.add(user);
                    adapter.notifyDataSetChanged();
                }
                if (!enrolledMembersList.isEmpty()) {
                    noEnrolledMembersTextView.setVisibility(View.GONE);
                    enrolledMembersRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EnrolledMembers.this, "Error loading user details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle Back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
