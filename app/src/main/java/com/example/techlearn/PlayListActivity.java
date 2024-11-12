package com.example.techlearn;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.techlearn.databinding.ActivityPlayListBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayListActivity extends AppCompatActivity {

    ActivityPlayListBinding binding;
    private String postId, postedByName, introUrl, title, price, duration, rating, description;
    private SimpleExoPlayer simpleExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        postId = getIntent().getStringExtra("postId");
        postedByName = getIntent().getStringExtra("name");
        introUrl = getIntent().getStringExtra("introUrl");
        title = getIntent().getStringExtra("title");
        price = getIntent().getStringExtra("price");
        duration = getIntent().getStringExtra("duration");
        rating = getIntent().getStringExtra("rate");
        description = getIntent().getStringExtra("desc");

        if (introUrl != null && !introUrl.isEmpty()) {
            try {
                simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
                binding.exoplayer2.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(introUrl);
                simpleExoPlayer.setMediaItem(mediaItem);
                simpleExoPlayer.prepare();
                simpleExoPlayer.play();
                binding.exoplayer2.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
                binding.exoplayer2.setControllerShowTimeoutMs(2000);
            } catch (Exception e) {
                Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No introduction video available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }
}
