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

import com.example.techlearn.R;
import com.example.techlearn.databinding.ActivityForgotPasswordBinding;
import com.example.techlearn.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    FirebaseAuth auth;
    Dialog loadindDalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        loadindDalog = new Dialog(ForgotPasswordActivity.this);
        loadindDalog.setContentView(R.layout.loading_dialog);

        if(loadindDalog.getWindow() != null){
            loadindDalog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadindDalog.setCancelable(false);
        }

        binding.btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.email.getText().toString();

                if(email.isEmpty()){
                    binding.email.setError("Enter email");
                }
                else{
                    forgotPassword(email);
                }
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void forgotPassword(String email) {

        loadindDalog.show();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            loadindDalog.dismiss();
                            Toast.makeText(ForgotPasswordActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        else{
                            loadindDalog.dismiss();
                            Toast.makeText(ForgotPasswordActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }
}