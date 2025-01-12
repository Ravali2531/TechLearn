package com.example.techlearn;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techlearn.Adapter.CommentAdapter;
import com.example.techlearn.Model.CommentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DisplayCommentsActivity extends AppCompatActivity {
    RecyclerView rvComments;
    ArrayList<CommentModel> commentList;
    CommentAdapter commentAdapter;
    HashMap<String, String> userNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comments);

        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        rvComments.setAdapter(commentAdapter);

        String postId = getIntent().getStringExtra("postId");

        if (postId != null) {
            fetchUserDetails();
            fetchComments(postId);
        } else {
            Toast.makeText(this, "Post ID is missing!", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch user details from Firebase
    private void fetchUserDetails() {
        FirebaseDatabase.getInstance().getReference("user_details")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String userId = dataSnapshot.getKey();
                            if (userId != null) {
                                String userName = dataSnapshot.child("name").getValue(String.class);
                                userNames.put(userId, userName != null ? userName : "Anonymous User");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DisplayCommentsActivity.this, "Error loading user details", Toast.LENGTH_SHORT).show();
                        Log.e("DisplayCommentsActivity", error.getMessage());
                    }
                });
    }

    // Fetch comments from Firebase
    private void fetchComments(String postId) {
        FirebaseDatabase.getInstance().getReference("course_reviews").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CommentModel comment = dataSnapshot.getValue(CommentModel.class);
                            if (comment != null) {
                                String userId = dataSnapshot.getKey();
                                comment.setUserId(userId);

                                // Set username from userNames HashMap
                                String userName = userNames.get(userId);
                                if (userName == null) {
                                    userName = "Anonymous User";
                                }

                                comment.setUserName(userName);
                                commentList.add(comment);
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DisplayCommentsActivity.this, "Error loading comments", Toast.LENGTH_SHORT).show();
                        Log.e("DisplayCommentsActivity", error.getMessage());
                    }
                });
    }
}
