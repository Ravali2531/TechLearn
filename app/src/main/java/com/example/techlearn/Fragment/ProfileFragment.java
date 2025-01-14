package com.example.techlearn.Fragment;

import static android.app.Activity.RESULT_OK;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.techlearn.EditProfile;
import com.example.techlearn.Login.SignInActivity;
import com.example.techlearn.Model.UserModel;
import com.example.techlearn.R;
import com.example.techlearn.ShareActivity;
import com.example.techlearn.TermsActivity;
import com.example.techlearn.databinding.FragmentProfileBinding;
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
    UserModel currentUser;

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

        loadProfile();

        // Open Edit Profile activity
        binding.fabEditProfile.setOnClickListener(view -> {
            if (currentUser != null) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                intent.putExtra("name", currentUser.getName());
                intent.putExtra("email", currentUser.getEmail());
                intent.putExtra("profile", currentUser.getProfile());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout functionality
        binding.cardLogout.setOnClickListener(view -> {
            auth.signOut();
            Intent intent = new Intent(getContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // Choose profile image
        binding.profileImage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        binding.cardTerms.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), TermsActivity.class);
            startActivity(intent);
        });

        binding.cardShare.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ShareActivity.class);
            startActivityForResult(intent, 100);
        });

        return binding.getRoot();
    }

    private void loadProfile() {
        database.getReference().child("user_details").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentUser = snapshot.getValue(UserModel.class);
                            if (currentUser != null) {
                                binding.userName.setText(currentUser.getName() != null ? currentUser.getName() : "User");
                                binding.userEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "example@gmail.com");

                                String profileUrl = currentUser.getProfile();
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
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Toast.makeText(getContext(), "Email sent successfully!", Toast.LENGTH_SHORT).show();
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
