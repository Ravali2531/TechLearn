package com.example.techlearn;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShareActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        sendButton = findViewById(R.id.btnSendEmail);

        // Set action for send button
        sendButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ShareActivity.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            } else {
                sendEmail(email);
            }
        });

        // Enable the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Share App");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Handle back button click
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Method to send an email
    private void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "This is a very knowledgeable app. Download it now and learn new things!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));

            // Set result and finish the activity
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(ShareActivity.this, "No email client found", Toast.LENGTH_SHORT).show();
        }
    }
}
