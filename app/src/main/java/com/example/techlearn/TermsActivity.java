package com.example.techlearn;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TermsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // Enable the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Terms and Conditions");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Handle back button click
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
