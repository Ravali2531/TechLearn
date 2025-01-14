//package com.example.techlearn;
//
//import android.app.Dialog;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.techlearn.Model.CourseModel;
//import com.example.techlearn.databinding.ActivityUploadCourseBinding;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.util.Date;
//
//public class UploadCourseActivity extends AppCompatActivity {
//
//    ActivityUploadCourseBinding binding;
//    FirebaseAuth auth;
//    FirebaseDatabase database;
//    FirebaseStorage storage;
//    Uri imageUri, videoUri;
//    Dialog loadingDialog;
//    private static final int CAMERA_REQUEST_CODE = 100;
//    private static final int GALLERY_REQUEST_CODE = 200;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        binding = ActivityUploadCourseBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        storage = FirebaseStorage.getInstance();
//
//        loadingDialog = new Dialog(UploadCourseActivity.this);
//        loadingDialog.setContentView(R.layout.loading_dialog);
//
//        if(loadingDialog.getWindow() != null){
//            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            loadingDialog.setCancelable(false);
//
//        }
//
//        //Upload thumbnail image
//        binding.uploadThumb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, 1);
//            }
//        });
//
//        //upload intro video
//        binding.uploadIntrov.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("video/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, 2);
//            }
//        });
//
//        binding.btnUploadCourse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String title = binding.edtTitle.getText().toString();
//                String price = binding.edtPrice.getText().toString();
//                String duration = binding.edtDuration.getText().toString();
//                String rating = "0";
//                String description = binding.edtDescription.getText().toString();
//
//                if(imageUri == null){
//                    Toast.makeText(UploadCourseActivity.this, "Select thumbnail image", Toast.LENGTH_SHORT).show();
//                }
//                else if (videoUri == null) {
//                    Toast.makeText(UploadCourseActivity.this, "Select intro video", Toast.LENGTH_SHORT).show();
//                }
//                else if(title.isEmpty()){
//
//                    binding.edtTitle.setError("Enter title");
//
//                } else if(price.isEmpty()){
//
//                    binding.edtPrice.setError("Enter price");
//
//                }
//                else if(duration.isEmpty()){
//
//                    binding.edtDuration.setError("Enter Duration");
//
//                } else if(description.isEmpty()){
//
//                    binding.edtDescription.setError("Enter description");
//
//                } else {
//                        // Validate price and duration for negative values
//                        try {
//                            long priceValue = Long.parseLong(price);
//                            if (priceValue < 0) {
//                                binding.edtPrice.setError("Price cannot be negative");
//                                return;
//                            }
//
//                            long durationValue = Long.parseLong(duration);
//                            if (durationValue < 0) {
//                                binding.edtDuration.setError("Duration cannot be negative");
//                                return;
//                            }
//
//                            // Proceed to upload the course if all validations pass
//                            uploadCourse(title, price, duration, rating, description, imageUri);
//
//                        } catch (NumberFormatException e) {
//                            Toast.makeText(UploadCourseActivity.this, "Enter valid numbers for price and duration", Toast.LENGTH_SHORT).show();
//                        }
//                    }
////                    uploadCourse(title, price, duration, rating, description, imageUri);
//                }
//        });
//
//    }
//
//    private void uploadCourse(String title, String price, String duration, String rating, String description, Uri imageUri) {
//
//        loadingDialog.show();
//
//        StorageReference reference = storage.getReference().child("thumbnail").child(new Date().getTime()+"");
//        reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//
//                        CourseModel model = new CourseModel();
//                        model.setTitle(title);
//                        model.setPrice(Long.parseLong(price));
//                        model.setDuration(duration);
//                        model.setRating(rating);
//                        model.setDescription(description);
//                        model.setThumbnail(imageUri.toString());
//                        model.setIntroVideo(uri.toString());
//                        model.setPostedBy(auth.getUid());
//                        model.setEnable("false");
//
//
//                        database.getReference().child("course")
//                                .push()
//                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        loadingDialog.dismiss();
//                                        Toast.makeText(UploadCourseActivity.this, "Course Uploaded", Toast.LENGTH_SHORT).show();
//                                        onBackPressed();
//                                    }
//                                });
//
//                    }
//                });
//            }
//        });
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        //Upload thumbnail image
//        if(requestCode == 1){
//
//            if(data != null){
//                uploadThumbnail(data.getData());
//            }
//
//        }
//        //upload intro video
//        else{
//            videoUri = data.getData();
//        }
//
//    }
//
//    private void uploadThumbnail(Uri data) {
//        loadingDialog.show();
//
//        StorageReference reference = storage.getReference().child("thumbnail").child(new Date().getTime()+"");
//        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        imageUri = uri;
//                        loadingDialog.dismiss();
//                    }
//                });
//            }
//        });
//    }
//
//}
package com.example.techlearn;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.techlearn.Model.CourseModel;
import com.example.techlearn.databinding.ActivityUploadCourseBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class UploadCourseActivity extends AppCompatActivity {

    ActivityUploadCourseBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri, videoUri;
    Dialog loadingDialog;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int VIDEO_RECORD_REQUEST_CODE = 400;
    private static final int VIDEO_PICK_GALLERY_CODE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUploadCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        loadingDialog = new Dialog(UploadCourseActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog);

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        // Upload thumbnail image
        binding.uploadThumb.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            } else {
                openCameraOrGallery();
            }
        });

        // Upload intro video
        binding.uploadIntrov.setOnClickListener(view -> {
            String[] options = {"Record Video", "Choose from Gallery"};
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Select an Option")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, VIDEO_RECORD_REQUEST_CODE);
                            } else {
                                recordVideo();
                            }
                        } else {
                            pickVideoFromGallery();
                        }
                    })
                    .show();
        });

        binding.btnUploadCourse.setOnClickListener(view -> {

            String title = binding.edtTitle.getText().toString();
            String price = binding.edtPrice.getText().toString();
            String duration = binding.edtDuration.getText().toString();
            String rating = "0";
            String description = binding.edtDescription.getText().toString();

            if (imageUri == null) {
                Toast.makeText(UploadCourseActivity.this, "Select thumbnail image", Toast.LENGTH_SHORT).show();
            } else if (videoUri == null) {
                Toast.makeText(UploadCourseActivity.this, "Select intro video", Toast.LENGTH_SHORT).show();
            } else if (title.isEmpty()) {

                binding.edtTitle.setError("Enter title");

            } else if (price.isEmpty()) {

                binding.edtPrice.setError("Enter price");

            } else if (duration.isEmpty()) {

                binding.edtDuration.setError("Enter Duration");

            } else if (description.isEmpty()) {

                binding.edtDescription.setError("Enter description");

            } else {
                try {
                    long priceValue = Long.parseLong(price);
                    if (priceValue < 0) {
                        binding.edtPrice.setError("Price cannot be negative");
                        return;
                    }

                    long durationValue = Long.parseLong(duration);
                    if (durationValue < 0) {
                        binding.edtDuration.setError("Duration cannot be negative");
                        return;
                    }

                    uploadCourse(title, price, duration, rating, description, imageUri);

                } catch (NumberFormatException e) {
                    Toast.makeText(UploadCourseActivity.this, "Enter valid numbers for price and duration", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openCameraOrGallery() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select an Option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                    }
                })
                .show();
    }

    private void recordVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(videoIntent, VIDEO_RECORD_REQUEST_CODE);
    }

    private void pickVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, VIDEO_PICK_GALLERY_CODE);
    }

    private void uploadCourse(String title, String price, String duration, String rating, String description, Uri imageUri) {

        loadingDialog.show();

        StorageReference reference = storage.getReference().child("thumbnail").child(new Date().getTime() + "");
        reference.putFile(videoUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {

            CourseModel model = new CourseModel();
            model.setTitle(title);
            model.setPrice(Long.parseLong(price));
            model.setDuration(duration);
            model.setRating(rating);
            model.setDescription(description);
            model.setThumbnail(imageUri.toString());
            model.setIntroVideo(uri.toString());
            model.setPostedBy(auth.getUid());
            model.setEnable("false");

            database.getReference().child("course")
                    .push()
                    .setValue(model).addOnSuccessListener(unused -> {
                        loadingDialog.dismiss();
                        Toast.makeText(UploadCourseActivity.this, "Course Uploaded", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    });

        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                uploadThumbnail(data.getData());
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                handleCapturedImage(photo);
            } else if (requestCode == VIDEO_RECORD_REQUEST_CODE && data != null) {
                videoUri = data.getData();
                Toast.makeText(this, "Video recorded successfully!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == VIDEO_PICK_GALLERY_CODE && data != null) {
                videoUri = data.getData();
                Toast.makeText(this, "Video selected from gallery!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleCapturedImage(Bitmap photo) {
        loadingDialog.show();

        // Convert Bitmap to Uri
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] imageData = bytes.toByteArray();

        StorageReference reference = storage.getReference().child("thumbnail").child(new Date().getTime() + ".jpg");
        UploadTask uploadTask = reference.putBytes(imageData);

        uploadTask.addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
            imageUri = uri;
            loadingDialog.dismiss();
            Toast.makeText(UploadCourseActivity.this, "Thumbnail uploaded from camera!", Toast.LENGTH_SHORT).show();
        })).addOnFailureListener(e -> {
            loadingDialog.dismiss();
            Toast.makeText(UploadCourseActivity.this, "Failed to upload thumbnail", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadThumbnail(Uri data) {
        loadingDialog.show();

        StorageReference reference = storage.getReference().child("thumbnail").child(new Date().getTime() + "");
        reference.putFile(data).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
            imageUri = uri;
            loadingDialog.dismiss();
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE || requestCode == VIDEO_RECORD_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == CAMERA_REQUEST_CODE) {
                    openCameraOrGallery();
                } else {
                    recordVideo();
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
