package com.example.techlearn.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.techlearn.Adapter.CourseUserAdapter;
import com.example.techlearn.Model.CourseModel;
import com.example.techlearn.R;
import com.example.techlearn.databinding.FragmentUserHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserHomeFragment extends Fragment {

    private FragmentUserHomeBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Dialog loadingDialog;
    private ArrayList<CourseModel> courseList;
    private CourseUserAdapter adapter;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserHomeBinding.inflate(inflater, container, false);

        // Ensure the placeholder is shown automatically
//        binding.searchView.setIconified(false);
        binding.searchView.setQueryHint("Search by course name...");

        // Initialize Firebase and Dialog
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        setupLoadingDialog();

        // Initialize RecyclerView
        courseList = new ArrayList<>();
        setupRecyclerView();

        // Load courses from Firebase
        loadCoursesFromFirebase();

        // Set up SearchView functionality
        setupSearchView();

        // Set up Spinner for filtering
        setupFilterSpinner();

        return binding.getRoot();
    }

    /**
     * Initializes the loading dialog.
     */
    private void setupLoadingDialog() {
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    /**
     * Sets up the RecyclerView with a GridLayoutManager and adapter.
     */
    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvCourse.setLayoutManager(layoutManager);
        adapter = new CourseUserAdapter(getContext(), courseList);
        binding.rvCourse.setAdapter(adapter);
    }

    /**
     * Loads courses from Firebase Realtime Database.
     */
    private void loadCoursesFromFirebase() {
        String currentUserId = auth.getUid();
        database.getReference().child("course")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CourseModel model = dataSnapshot.getValue(CourseModel.class);
                            if (model != null) {
                                model.setPostId(dataSnapshot.getKey());
                                if(!model.getPostedBy().equals(currentUserId)){
                                    courseList.add(model);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
    }

//    private void loadCoursesFromFirebase() {
//        String currentUserId = auth.getUid();
//
//        database.getReference().child("course")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        courseList.clear();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            CourseModel model = dataSnapshot.getValue(CourseModel.class);
//                            if (model != null) {
//                                model.setPostId(dataSnapshot.getKey());
//
//                                // Check if the user is an admin
//                                if (auth.getCurrentUser().getEmail().equals("admin@example.com")) {
//                                    // Exclude admin's own courses
//                                    if (!model.getPostedBy().equals(currentUserId)) {
//                                        courseList.add(model);
//                                    }
//                                } else {
//                                    // Regular user: add all courses
//                                    courseList.add(model);
//                                }
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                        loadingDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                        loadingDialog.dismiss();
//                    }
//                });
//    }


    /**
     * Sets up the SearchView to filter courses based on user input.
     */
    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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

    /**
     * Sets up the filter spinner to filter courses based on enrollment status.
     */
    private void setupFilterSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.filterSpinner.setAdapter(spinnerAdapter);

        binding.filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    /**
     * Handles the selection from the filter spinner and applies the filter accordingly.
     */
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

    /**
     * Loads the user's enrolled courses and filters them based on the selected option.
     *
     * @param enrolled true to show enrolled courses, false to show not enrolled courses
     */
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
                        Toast.makeText(getContext(), "Error loading enrollments", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}