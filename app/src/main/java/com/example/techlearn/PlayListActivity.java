package com.example.techlearn;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.techlearn.Adapter.PlayListAdapter;
import com.example.techlearn.Adapter.PlayListUserAdapter;
import com.example.techlearn.Model.PlayListModel;
import com.example.techlearn.databinding.ActivityPlayListBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {

    ActivityPlayListBinding binding;
    private String postId, postedByName, introUrl, title, duration, rating, description;
    private long price;
    private SimpleExoPlayer simpleExoPlayer;
    ArrayList<PlayListModel>list;
    PlayListUserAdapter adapter;
    private Dialog loadingDialog;

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

        list = new ArrayList<>();

        loadingDialog = new Dialog(PlayListActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog);

        if(loadingDialog.getWindow() != null){
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);

        }

        loadingDialog.show();

        postId = getIntent().getStringExtra("postId");
        postedByName = getIntent().getStringExtra("name");
        introUrl = getIntent().getStringExtra("introUrl");
        title = getIntent().getStringExtra("title");
        price = getIntent().getLongExtra("price", 0);
        duration = getIntent().getStringExtra("duration");
        rating = getIntent().getStringExtra("rate");
        description = getIntent().getStringExtra("desc");

        binding.title.setText(title);
        binding.createdBy.setText(postedByName);
        binding.rating.setText(rating);
        binding.duration.setText(duration);
        binding.price.setText(price+"");

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

        binding.txtDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.rvPlayList.setVisibility(View.GONE);
                binding.description.setVisibility(View.VISIBLE);

            }
        });

        binding.btnPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.rvPlayList.setVisibility(View.VISIBLE);
                binding.description.setVisibility(View.GONE);

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvPlayList.setLayoutManager(layoutManager);

        adapter = new PlayListUserAdapter(this, list, new PlayListUserAdapter.videoListener() {
            @Override
            public void onClick(int position, String key, String videoUrl, int size) {

                playVideo(videoUrl);
            }

        });

        binding.rvPlayList.setAdapter(adapter);
        loadPlayList();

    }

    private void loadPlayList() {


        FirebaseDatabase.getInstance().getReference().child("course").child(postId).child("playlist")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            list.clear();

                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                                PlayListModel model = dataSnapshot.getValue(PlayListModel.class);
                                model.setKey(dataSnapshot.getKey());
                                list.add(model);

                            }
                            adapter.notifyDataSetChanged();
                            loadingDialog.dismiss();
                        }
                        else{
                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PlayListActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

    }

    private void playVideo(String videoUrl) {

        if (introUrl != null && !introUrl.isEmpty()) {
            try {
                simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
                binding.exoplayer2.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
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
    public void onBackPressed() {
        super.onBackPressed();
        simpleExoPlayer.pause();
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
