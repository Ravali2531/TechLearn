package com.example.techlearn.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class UserHomeFragment extends Fragment {

    FragmentUserHomeBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingDialog;
    ArrayList<CourseModel> list;
    CourseUserAdapter adapter;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserHomeBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        list = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvCourse.setLayoutManager(layoutManager);

        adapter = new CourseUserAdapter(getContext(), list);
        binding.rvCourse.setAdapter(adapter);

        database.getReference().child("course")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                CourseModel model = dataSnapshot.getValue(CourseModel.class);
                                if (model != null) {
                                    model.setPostId(dataSnapshot.getKey());
                                    list.add(model);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No courses available", Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

        return binding.getRoot();
    }
}
