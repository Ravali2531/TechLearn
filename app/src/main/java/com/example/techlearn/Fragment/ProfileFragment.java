package com.example.techlearn.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.techlearn.Login.SignInActivity;
import com.example.techlearn.Model.UserModel;
import com.example.techlearn.R;
import com.example.techlearn.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Dialog loadingDialog;
    Uri profileUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_dialog);

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        loadProfileImage();

        // Choose a profile image
        binding.profileImage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        // Terms of Service
        binding.cardTerms.setOnClickListener(view ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/terms"))));

        // Rate App
        binding.cardRate.setOnClickListener(view ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getContext().getPackageName()))));

        // Logout
        binding.cardLogout.setOnClickListener(view -> {
            auth.signOut();
            Intent intent = new Intent(getContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    private void loadProfileImage() {
        database.getReference().child("user_details").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel model = snapshot.getValue(UserModel.class);
                            if (model != null) {
                                binding.userName.setText(model.getName() != null ? model.getName() : "User");
                                binding.userEmail.setText(model.getEmail() != null ? model.getEmail() : "example@gmail.com");

                                String profileUrl = model.getProfile();
                                if (profileUrl != null && !profileUrl.isEmpty()) {
                                    Picasso.get().load(profileUrl)
                                            .placeholder(R.drawable.user_profile)
                                            .error(R.drawable.user_profile)
                                            .into(binding.profileImage);
                                } else {
                                    binding.profileImage.setImageResource(R.drawable.user_profile);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            updateProfile(data.getData());
        }
    }

    private void updateProfile(Uri uri) {
        loadingDialog.show();

        StorageReference reference = storage.getReference().child("profile_image").child(auth.getCurrentUser().getUid());

        reference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("profile", downloadUri.toString());

                    database.getReference().child("user_details").child(auth.getUid())
                            .updateChildren(map)
                            .addOnSuccessListener(unused -> {
                                loadingDialog.dismiss();
                                Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }
}
