package com.example.techlearn;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.techlearn.Adapter.CourseAdapter;
import com.example.techlearn.Adapter.PlayListAdapter;
import com.example.techlearn.Model.CourseModel;
import com.example.techlearn.Model.PlayListModel;
import com.example.techlearn.databinding.ActivityUploadCourseBinding;
import com.example.techlearn.databinding.ActivityUploadPlayListBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class UploadPlayListActivity extends AppCompatActivity {
    ActivityUploadPlayListBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri, videoUri;
    Dialog loadingDialog;
    private String postId;
    PlayListAdapter adapter;
    ArrayList<PlayListModel>list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUploadPlayListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        postId = getIntent().getStringExtra("postId");
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        loadingDialog = new Dialog(UploadPlayListActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog);

        if(loadingDialog.getWindow() != null){
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);

        }

        loadingDialog.show();

        list = new ArrayList<>();
        
        loadPlayList();

        //upload intro video
        binding.uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            }
        });

        binding.uploadPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = binding.vTitle.getText().toString();

                if(videoUri == null){

                    Toast.makeText(UploadPlayListActivity.this, "Upload Video", Toast.LENGTH_SHORT).show();
                    
                } else if (title.isEmpty()) {

                    binding.vTitle.setError("Enter title");
                    
                } else {
                    uploadPlayList(title);
                }

            }
        });

    }

    private void loadPlayList() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(UploadPlayListActivity.this);
        binding.rvPlayList.setLayoutManager(layoutManager);

        adapter = new PlayListAdapter( UploadPlayListActivity.this, list);
        binding.rvPlayList.setAdapter(adapter);

        database.getReference().child("playlist").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            list.clear();

                            binding.nodataUploadPlayList.setVisibility(View.GONE);

                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                                PlayListModel model = dataSnapshot.getValue(PlayListModel.class);
                                model.setKey(dataSnapshot.getKey());
                                list.add(model);

                            }
                            adapter.notifyDataSetChanged();
                            loadingDialog.dismiss();
                        }
                        else{
                            binding.nodataUploadPlayList.setText("No PlayLists Uploaded");
                            binding.nodataUploadPlayList.setVisibility(View.VISIBLE);
                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UploadPlayListActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

    }

    private void uploadPlayList(String title) {

        loadingDialog.show();

        StorageReference reference = storage.getReference().child("play_list").child(new Date().getTime()+"");
        reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        PlayListModel model = new PlayListModel();
                        model.setTitle(title);
                        model.setVideoUrl(uri.toString());
                        model.setEnabled("false");

                        database.getReference().child("playlist").child(postId)
                                .push()
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        loadingDialog.dismiss();
                                        Toast.makeText(UploadPlayListActivity.this, "Course Uploaded", Toast.LENGTH_SHORT).show();
                                        binding.vTitle.setText("");
                                        // Restore the original image after upload
                                        binding.uploadVideo.setImageResource(R.drawable.upload_image);
                                    }
                                });

                    }
                });
            }
        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        //Upload thumbnail image
//        if(requestCode == 1){
//
//            if(data != null){
//
//                videoUri = data.getData();
//            }
//
//        }
//
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from selecting a video
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            if (videoUri != null) {
                // Show a preview or update the UI as needed
                binding.uploadVideo.setImageURI(videoUri); // Optional: show thumbnail of the video
            } else {
                Toast.makeText(this, "Failed to select video, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

}