package com.example.techlearn.Login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.techlearn.MainActivity;
import com.example.techlearn.R;
import com.example.techlearn.UserMainActivity;
import com.example.techlearn.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadindDalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadindDalog = new Dialog(SignInActivity.this);
        loadindDalog.setContentView(R.layout.loading_dialog);

        if (loadindDalog.getWindow() != null) {
            loadindDalog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadindDalog.setCancelable(false);
        }

        // Check if the user is already logged in
        if (auth.getCurrentUser() != null) {
            loadindDalog.show();

            // Fetch the user's role from Firebase
            String userId = auth.getCurrentUser().getUid();
            database.getReference("user_details").child(userId).child("role")
                    .get().addOnCompleteListener(task -> {
                        loadindDalog.dismiss();
                        if (task.isSuccessful() && task.getResult().exists()) {
                            String role = task.getResult().getValue(String.class);

                            Intent intent;
                            if ("Admin".equals(role)) {
                                intent = new Intent(SignInActivity.this, MainActivity.class);
                            } else {
                                intent = new Intent(SignInActivity.this, UserMainActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignInActivity.this, "Failed to fetch user role", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        binding.btnSignIn.setOnClickListener(view -> {
            String email = binding.edtEmail.getText().toString();
            String password = binding.edtPassword.getText().toString();

            if (email.isEmpty()) {
                binding.edtEmail.setError("Enter your email");
            } else if (password.isEmpty()) {
                binding.edtPassword.setError("Enter your password");
            } else {
                signIn(email, password);
            }
        });

        binding.createAccount.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        binding.forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void signIn(String email, String password) {

        loadindDalog.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            if(auth.getCurrentUser().isEmailVerified()){

                                String userId = auth.getCurrentUser().getUid();
                                database.getReference("user_details").child(userId).child("role")
                                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> roleTask) {
                                                loadindDalog.dismiss();
                                                if (roleTask.isSuccessful() && roleTask.getResult().exists()) {
                                                    String role = roleTask.getResult().getValue(String.class);

                                                    Intent intent;
                                                    if ("Admin".equals(role)) {
                                                        intent = new Intent(SignInActivity.this, MainActivity.class);
                                                    } else {
                                                        intent = new Intent(SignInActivity.this, UserMainActivity.class);
                                                    }
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignInActivity.this, "Error fetching user role", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            else{
                                loadindDalog.dismiss();
                                Toast.makeText(SignInActivity.this, "Your email is not verified. Please verify to login", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            loadindDalog.dismiss();
                            Toast.makeText(SignInActivity.this, "Please check your email and password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}