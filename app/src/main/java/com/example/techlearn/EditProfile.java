package com.example.techlearn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techlearn.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private EditText editName, editEmail;
    private CircleImageView editProfileImage;
    private Button btnUpdateProfile;

    private Uri profileImageUri;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    private String userId, profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editProfileImage = findViewById(R.id.edit_profile_image);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("user_details").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures").child(userId);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");

        // Fetch and display existing details
        Intent intent = getIntent();
        editName.setText(intent.getStringExtra("name"));
        editEmail.setText(intent.getStringExtra("email"));
        profileUrl = intent.getStringExtra("profile");

        if (profileUrl != null && !profileUrl.isEmpty()) {
            Picasso.get().load(profileUrl)
                    .placeholder(R.drawable.user_profile)
                    .error(R.drawable.user_profile)
                    .into(editProfileImage);
        } else {
            editProfileImage.setImageResource(R.drawable.user_profile);
        }

        // Handle profile image change
        editProfileImage.setOnClickListener(v -> chooseProfileImage());

        // Handle update button click
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void chooseProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            profileImageUri = data.getData();
            editProfileImage.setImageURI(profileImageUri);
        }
    }

    private void updateProfile() {
        progressDialog.show();

        String name = editName.getText().toString();
        String email = editEmail.getText().toString();

        if (profileImageUri != null) {
            storageReference.putFile(profileImageUri).addOnSuccessListener(taskSnapshot ->
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String newProfileUrl = uri.toString();
                        updateDatabase(name, email, newProfileUrl);
                    })
            );
        } else {
            updateDatabase(name, email, profileUrl);
        }
    }

    private void updateDatabase(String name, String email, String profileUrl) {
        databaseReference.child("name").setValue(name);
        databaseReference.child("email").setValue(email);
        databaseReference.child("profile").setValue(profileUrl);

        progressDialog.dismiss();
        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
