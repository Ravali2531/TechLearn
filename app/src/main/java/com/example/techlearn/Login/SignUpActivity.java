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

import com.example.techlearn.Model.UserModel;
import com.example.techlearn.R;
import com.example.techlearn.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadindDalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadindDalog = new Dialog(SignUpActivity.this);
        loadindDalog.setContentView(R.layout.loading_dialog);

        if(loadindDalog.getWindow() != null){
            loadindDalog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadindDalog.setCancelable(false);
        }

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.edtName.getText().toString();
                String email = binding.edtEmail.getText().toString();
                String password = binding.edtPassword.getText().toString();
                String role = binding.spinnerRole.getSelectedItem().toString();

                if(name.isEmpty()){
                    binding.edtName.setError("Enter your name");
                }
                else if(email.isEmpty()){
                    binding.edtEmail.setError("Enter your email");
                }
                else if(password.isEmpty()){
                    binding.edtPassword.setError("Enter your password");
                }else if(role.equals("Choose One:")){
                    Toast.makeText(SignUpActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
                }
                else{
                    signup(name, email, password, role);
                }
            }
        });

        binding.alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });


    }

    private void signup(String name, String email, String password, String role) {
        loadindDalog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadindDalog.dismiss();
                        if (task.isSuccessful()) {
                            // Successful registration
                            String userId = task.getResult().getUser().getUid();

                            UserModel userModel = new UserModel(name, email, password, "https://firebasestorage.googleapis.com/v0/b/your-app-id.appspot.com/o/default_profile.png?alt=media", role);

                            database.getReference().child("user_details").child(userId)
                                    .setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                auth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(SignUpActivity.this, "Registered successfully. Please verify your email.", Toast.LENGTH_SHORT).show();

                                                                auth.signOut();
                                                                onBackPressed();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Registration failed
                            String errorMessage;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                errorMessage = "This email is already registered.";
                            } catch (Exception e) {
                                errorMessage = "Registration failed: " + e.getMessage();
                            }
                            Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}