//package com.example.techlearn;
//
//import android.app.Dialog;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.StringRes;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.techlearn.Adapter.PlayListAdapter;
//import com.example.techlearn.Adapter.PlayListUserAdapter;
//import com.example.techlearn.Model.PlayListModel;
//import com.example.techlearn.databinding.ActivityPlayListBinding;
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.MediaItem;
//import com.google.android.exoplayer2.PlaybackException;
//import com.google.android.exoplayer2.Player;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.ui.PlayerView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.stripe.android.PaymentConfiguration;
//import com.stripe.android.paymentsheet.PaymentSheet;
//import com.stripe.android.paymentsheet.PaymentSheetResult;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.crypto.SecretKey;
//
//public class PlayListActivity extends AppCompatActivity {
//
//    ActivityPlayListBinding binding;
//    private String postId, postedByName, introUrl, title, duration, rating, description;
//    private long price;
//    private SimpleExoPlayer simpleExoPlayer;
//    ArrayList<PlayListModel>list;
//    PlayListUserAdapter adapter;
//    private Dialog loadingDialog;
//    Button enrollNow;
//    String publishableKey = "pk_test_51QLXl2L0kLdfcs5yhjcDuc0WAnDoZgIu1Ts88JhU7ZpGDDmkZ8X6mkhAnRuFuhYQLePpmWrcKXJby0qtvMiw6FVc00DTOLaHK5";
//    String secretKey = "sk_test_51QLXl2L0kLdfcs5y98DW4YbvElpIN2OwmA8WEsc7Di4clKVgLGObRBuZQcpfIvZNQ6PsyOrFQk3sRraZI82ksy2n00g7TEFBKn";
//    String customerId;
//    String ephericalKey;
//    String clientSecret;
//    PaymentSheet paymentSheet;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityPlayListBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//
//
//        list = new ArrayList<>();
//
//        loadingDialog = new Dialog(PlayListActivity.this);
//        loadingDialog.setContentView(R.layout.loading_dialog);
//
//        if(loadingDialog.getWindow() != null){
//            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            loadingDialog.setCancelable(false);
//
//        }
//
//        loadingDialog.show();
//
//        postId = getIntent().getStringExtra("postId");
//        postedByName = getIntent().getStringExtra("name");
//        introUrl = getIntent().getStringExtra("introUrl");
//        title = getIntent().getStringExtra("title");
//        price = getIntent().getLongExtra("price", 0);
//        duration = getIntent().getStringExtra("duration");
//        rating = getIntent().getStringExtra("rate");
//        description = getIntent().getStringExtra("desc");
//
//        binding.title.setText(title);
//        binding.createdBy.setText(postedByName);
//        binding.rating.setText(rating);
//        binding.duration.setText(duration);
//        binding.price.setText(price+"");
//
//        if (introUrl != null && !introUrl.isEmpty()) {
//            try {
//                simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
//                binding.exoplayer2.setPlayer(simpleExoPlayer);
//                MediaItem mediaItem = MediaItem.fromUri(introUrl);
//                simpleExoPlayer.setMediaItem(mediaItem);
//                simpleExoPlayer.prepare();
//                simpleExoPlayer.play();
//                binding.exoplayer2.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
//                binding.exoplayer2.setControllerShowTimeoutMs(2000);
//            } catch (Exception e) {
//                Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "No introduction video available", Toast.LENGTH_SHORT).show();
//        }
//
//        binding.txtDescription.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                binding.rvPlayList.setVisibility(View.GONE);
//                binding.description.setVisibility(View.VISIBLE);
//
//            }
//        });
//
//        binding.btnPlayList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                binding.rvPlayList.setVisibility(View.VISIBLE);
//                binding.description.setVisibility(View.GONE);
//
//            }
//        });
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        binding.rvPlayList.setLayoutManager(layoutManager);
//
//        checkEnrollmentStatus();
//
//        adapter = new PlayListUserAdapter(this, list, new PlayListUserAdapter.videoListener() {
//            @Override
//            public void onClick(int position, String key, String videoUrl, int size) {
//
//                playVideo(videoUrl);
//            }
//
//        });
//
//        binding.rvPlayList.setAdapter(adapter);
//        loadPlayList();
//
//        PaymentConfiguration.init(this, publishableKey);
//
//        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
//
//            onPaymentResult(paymentSheetResult);
//
//        });
//
//        binding.btnEnroll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (clientSecret != null && !clientSecret.isEmpty() &&
//                        customerId != null && !customerId.isEmpty() &&
//                        ephericalKey != null && !ephericalKey.isEmpty()) {
//                    paymentFlow();
//                } else {
//                    Toast.makeText(PlayListActivity.this,
//                            "Error initializing payment. Please try again.",
//                            Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            customerId = object.getString("id");
//
////                            Toast.makeText(PlayListActivity.this, customerId + " customer id", Toast.LENGTH_SHORT).show();
//                            getEmphericalKey();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Toast.makeText(PlayListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//
//
//            }
//        }){
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                Map<String, String> header = new HashMap<>();
//                header.put("Authorization", "Bearer "+ secretKey);
//
//                return header;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(request);
//
//    }
//
//    private void getEmphericalKey() {
//
//        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            ephericalKey = object.getString("id");
//
////                            Toast.makeText(PlayListActivity.this, ephericalKey + " epherical key", Toast.LENGTH_SHORT).show();
//
//                            getClientSecret(customerId, ephericalKey);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Toast.makeText(PlayListActivity.this, error.getLocalizedMessage() + "can't gert", Toast.LENGTH_SHORT).show();
//
//
//            }
//        }){
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                Map<String, String> header = new HashMap<>();
//                header.put("Authorization", "Bearer "+ secretKey);
//                header.put("Stripe-Version", "2024-10-28.acacia");
//
//                return header;
//            }
//
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                Map<String, String> params = new HashMap<>();
//                params.put("customer", customerId);
//
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(request);
//
//    }
//
//    private void getClientSecret(String customerId, String ephericalKey) {
//
//        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            clientSecret = object.getString("client_secret");
//
////                            Toast.makeText(PlayListActivity.this, clientSecret + " client_secret", Toast.LENGTH_SHORT).show();
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Toast.makeText(PlayListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//
//
//            }
//        }){
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                Map<String, String> header = new HashMap<>();
//                header.put("Authorization", "Bearer "+ secretKey);
//
//                return header;
//            }
//
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("customer", customerId);
//
//                // Convert the price to the smallest currency unit (e.g., cents for CAD)
//                String amountInCents = String.valueOf(price * 100);
//                params.put("amount", amountInCents);
//
//                params.put("currency", "CAD");
//                params.put("automatic_payment_methods[enabled]", "true");
//                return params;
//            }
//
////            protected Map<String, String> getParams() throws AuthFailureError {
////
////                Map<String, String> params = new HashMap<>();
////                params.put("customer", customerId);
////                params.put("amount", "100"+"00");
////                params.put("currency","CAD");
////                params.put("automatic_payment_methods[enabled]", "true");
////                return params;
////            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(request);
//    }
//
//    private void loadPlayList() {
//
//
//        FirebaseDatabase.getInstance().getReference().child("course").child(postId).child("playlist")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        if(snapshot.exists()){
//                            list.clear();
//
//                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//
//                                PlayListModel model = dataSnapshot.getValue(PlayListModel.class);
//                                model.setKey(dataSnapshot.getKey());
//                                list.add(model);
//
//                            }
//                            adapter.notifyDataSetChanged();
//                            loadingDialog.dismiss();
//                        }
//                        else{
//                            loadingDialog.dismiss();
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(PlayListActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                        loadingDialog.dismiss();
//                    }
//                });
//
//    }
//
////    private void playVideo(String videoUrl) {
////
////        if (introUrl != null && !introUrl.isEmpty()) {
////            try {
////                simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
////                binding.exoplayer2.setPlayer(simpleExoPlayer);
////                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
////                simpleExoPlayer.setMediaItem(mediaItem);
////                simpleExoPlayer.prepare();
////                simpleExoPlayer.play();
////                binding.exoplayer2.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
////                binding.exoplayer2.setControllerShowTimeoutMs(2000);
////            } catch (Exception e) {
////                Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
////            }
////        } else {
////            Toast.makeText(this, "No introduction video available", Toast.LENGTH_SHORT).show();
////        }
////
////    }
//
//    private void playVideo(String videoUrl) {
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        FirebaseDatabase.getInstance().getReference("enrollments")
//                .child(userId)
//                .child(postId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            // User is enrolled, proceed with playback
//                            try {
//                                if (simpleExoPlayer != null) {
//                                    simpleExoPlayer.release(); // Release the previous player if any
//                                }
//
//                                simpleExoPlayer = new SimpleExoPlayer.Builder(PlayListActivity.this).build();
//                                binding.exoplayer2.setPlayer(simpleExoPlayer);
//
//                                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
//                                simpleExoPlayer.setMediaItem(mediaItem);
//                                simpleExoPlayer.prepare();
//                                simpleExoPlayer.play();
//
//                                binding.exoplayer2.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
//                                binding.exoplayer2.setControllerShowTimeoutMs(2000);
//                            } catch (Exception e) {
//                                Log.e("PlayListActivity", "Error loading video: " + e.getMessage());
//                                Toast.makeText(PlayListActivity.this, "Error loading video", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            // User is not enrolled
//                            Toast.makeText(PlayListActivity.this, "You need to enroll to watch this video.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("PlayListActivity", "Database error: " + error.getMessage());
//                        Toast.makeText(PlayListActivity.this, "Failed to check enrollment status.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//
//
//    private void paymentFlow() {
//
//        if (clientSecret != null && !clientSecret.isEmpty() && ephericalKey != null) {
//            paymentSheet.presentWithPaymentIntent(
//                    clientSecret,
//                    new PaymentSheet.Configuration(
//                            "TechLearn",
//                            new PaymentSheet.CustomerConfiguration(customerId, ephericalKey)
//                    )
//            );
//        } else {
//            Log.e("PaymentFlow", "Missing clientSecret or ephericalKey");
//            Toast.makeText(this, "Payment parameters are invalid", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
//
////    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
////
////        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
////
////            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
////
////        }
////
////    }
//
//    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
//        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
//            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
//
//            // Save enrollment to Firebase
//            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            FirebaseDatabase.getInstance().getReference("enrollments")
//                    .child(userId)
//                    .child(postId)
//                    .setValue(true) // Mark as enrolled
//                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(PlayListActivity.this, "Enrollment Successful!", Toast.LENGTH_SHORT).show();
//
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(PlayListActivity.this, "Failed to save enrollment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//
//            binding.btnEnroll.setEnabled(false);
//            binding.btnEnroll.setText("Enrolled");
//        }
//    }
//
//    private void checkEnrollmentStatus() {
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        FirebaseDatabase.getInstance().getReference("enrollments")
//                .child(userId)
//                .child(postId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (!snapshot.exists()) {
//                            binding.btnEnroll.setVisibility(View.VISIBLE);
////                            Toast.makeText(PlayListActivity.this, "Please enroll to access the course content.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            binding.rvPlayList.setVisibility(View.VISIBLE);
//                            binding.btnEnroll.setEnabled(false);
//                            binding.btnEnroll.setText("Enrolled");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("PlayListActivity", "Error checking enrollment: " + error.getMessage());
//                    }
//                });
//    }
//
//
//
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        simpleExoPlayer.pause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (simpleExoPlayer != null) {
//            simpleExoPlayer.release();
//            simpleExoPlayer = null;
//        }
//    }
//}

package com.example.techlearn;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.techlearn.Adapter.PlayListUserAdapter;
import com.example.techlearn.Model.PlayListModel;
import com.example.techlearn.databinding.ActivityPlayListBinding;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.toolbox.JsonObjectRequest;


public class PlayListActivity extends AppCompatActivity {

    ActivityPlayListBinding binding;
    private String postId, introUrl, title, duration, rating;
    private long price;
    private SimpleExoPlayer simpleExoPlayer;
    ArrayList<PlayListModel> list;
    PlayListUserAdapter adapter;
    private Dialog loadingDialog;
    private PaymentSheet paymentSheet;
    private String clientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize variables
        list = new ArrayList<>();
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        // Get data from Intent
        postId = getIntent().getStringExtra("postId");
        introUrl = getIntent().getStringExtra("introUrl");
        title = getIntent().getStringExtra("title");
        price = getIntent().getLongExtra("price", 0);
        duration = getIntent().getStringExtra("duration");
        rating = getIntent().getStringExtra("rate");

        // Set data to UI
        binding.title.setText(title);
//        binding.rating.setText(rating);
        // Handle rating display
        if (rating != null && !rating.isEmpty()) {
            binding.rating.setText(rating);
        } else {
            binding.rating.setText("N/A");
        }
        binding.duration.setText(duration);
        binding.price.setText(price+"");

        // Load video
        loadVideo(introUrl);

        binding.txtDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.rvPlayList.setVisibility(View.GONE);
                binding.description.setVisibility(View.VISIBLE);

                // Change colors
                binding.txtDescription.setBackgroundColor(getResources().getColor(R.color.background)); // Primary color
                binding.txtDescription.setTextColor(getResources().getColor(android.R.color.black));

                binding.btnPlayList.setBackgroundColor(getResources().getColor(android.R.color.white)); // White color
                binding.btnPlayList.setTextColor(getResources().getColor(android.R.color.black));

            }
        });

        binding.btnPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.rvPlayList.setVisibility(View.VISIBLE);
                binding.description.setVisibility(View.GONE);
                binding.btnPlayList.setBackgroundColor(getResources().getColor(R.color.background)); // Primary color
                binding.btnPlayList.setTextColor(getResources().getColor(android.R.color.black));

                binding.txtDescription.setBackgroundColor(getResources().getColor(android.R.color.white)); // White color
                binding.txtDescription.setTextColor(getResources().getColor(android.R.color.black));


            }
        });

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvPlayList.setLayoutManager(layoutManager);

        adapter = new PlayListUserAdapter(this, list, this::playVideo);
        binding.rvPlayList.setAdapter(adapter);
        loadPlayList();

        // Initialize Stripe Payment
        PaymentConfiguration.init(this, "pk_test_51QLXl2L0kLdfcs5yhjcDuc0WAnDoZgIu1Ts88JhU7ZpGDDmkZ8X6mkhAnRuFuhYQLePpmWrcKXJby0qtvMiw6FVc00DTOLaHK5");
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        binding.btnViewComments.setOnClickListener(view -> {
            Intent intent = new Intent(PlayListActivity.this, DisplayCommentsActivity.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });


        checkEnrollmentStatus();


        // Enroll Button Click Listener
        binding.btnEnroll.setOnClickListener(view -> createPaymentIntent());
    }

    private void loadVideo(String videoUrl) {
        if (videoUrl != null && !videoUrl.isEmpty()) {
            try {
                simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
                binding.exoplayer2.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                simpleExoPlayer.setMediaItem(mediaItem);
                simpleExoPlayer.prepare();
                simpleExoPlayer.play();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No introduction video available", Toast.LENGTH_SHORT).show();
        }
    }


    private void createPaymentIntent() {
        if (price <= 0) {
            Toast.makeText(this, "Invalid price amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert price to cents
        long amountInCents = price * 100;

        // Create a JSON object to send in the request
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("amount", amountInCents);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request body", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the request body for debugging
        Log.d("PaymentDebug", "Request Body: " + requestBody.toString());

        // Create a JsonObjectRequest
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://b724-70-26-192-21.ngrok-free.app/create-payment-intent",
                requestBody,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response.toString());
                        clientSecret = object.getString("clientSecret");

                        if (clientSecret != null && !clientSecret.isEmpty()) {
                            paymentFlow();
                        } else {
                            Toast.makeText(this, "Failed to retrieve payment information", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Connection failed: ";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data);
                            JSONObject errorJson = new JSONObject(responseBody);
                            errorMessage += errorJson.optString("error", "Unknown error");
                        } catch (Exception e) {
                            errorMessage += "Unknown error";
                        }
                    } else {
                        errorMessage += error.getMessage();
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private void paymentFlow() {
        if (clientSecret != null) {
            paymentSheet.presentWithPaymentIntent(clientSecret, new PaymentSheet.Configuration("TechLearn", null));
        }
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
            enrollUser();
        } else {
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void enrollUser() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("enrollments")
                .child(userId)
                .child(postId)
                .setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Enrolled Successfully!", Toast.LENGTH_SHORT).show();
                    binding.btnEnroll.setEnabled(false);
                    binding.btnEnroll.setText("Enrolled");
                    binding.btnRateAndComment.setVisibility(View.VISIBLE);
                    binding.btnViewComments.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Enrollment Failed", Toast.LENGTH_SHORT).show());
    }

    private void loadPlayList() {
        FirebaseDatabase.getInstance().getReference("playlist").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            PlayListModel model = dataSnapshot.getValue(PlayListModel.class);
                            if (model != null) {
                                model.setKey(dataSnapshot.getKey());
                                list.add(model);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PlayListActivity.this, "Error loading playlist", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
    }

    private void playVideo(int position, String key, String videoUrl, int size) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("enrollments")
                .child(userId)
                .child(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // User is enrolled, proceed with playback
                            try {
                                if (simpleExoPlayer != null) {
                                    simpleExoPlayer.release(); // Release the previous player if any
                                }

                                simpleExoPlayer = new SimpleExoPlayer.Builder(PlayListActivity.this).build();
                                binding.exoplayer2.setPlayer(simpleExoPlayer);

                                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                                simpleExoPlayer.setMediaItem(mediaItem);
                                simpleExoPlayer.prepare();
                                simpleExoPlayer.play();

                                binding.exoplayer2.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
                                binding.exoplayer2.setControllerShowTimeoutMs(2000);
                            } catch (Exception e) {
                                Log.e("PlayListActivity", "Error loading video: " + e.getMessage());
                                Toast.makeText(PlayListActivity.this, "Error loading video", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // User is not enrolled
                            Toast.makeText(PlayListActivity.this, "You need to enroll to watch this video.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PlayListActivity", "Database error: " + error.getMessage());
                    }
                });
    }

    private void checkEnrollmentStatus() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check in the Firebase "enrollments" node if the user has already enrolled in this course
        FirebaseDatabase.getInstance().getReference("enrollments")
                .child(userId)
                .child(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // User is already enrolled
                            binding.btnEnroll.setText("Enrolled");
                            binding.btnEnroll.setEnabled(false);
                            binding.rvPlayList.setVisibility(View.VISIBLE);  // Show the playlist
                            binding.btnRateAndComment.setVisibility(View.VISIBLE);
                            binding.btnRateAndComment.setOnClickListener(view -> {
                                Intent intent = new Intent(PlayListActivity.this, RateAndCommentActivity.class);
                                intent.putExtra("postId", postId);
                                startActivity(intent);
                            });
                        } else {
                            // User is not enrolled
                            binding.btnEnroll.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PlayListActivity", "Error checking enrollment: " + error.getMessage());
                    }
                });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUpdatedRating();
    }
    private void fetchUpdatedRating() {
        FirebaseDatabase.getInstance().getReference("course")
                .child(postId)
                .child("rating")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Object ratingValue = snapshot.getValue();

                            double updatedRating = 0.0;

                            if (ratingValue instanceof Double) {
                                updatedRating = (Double) ratingValue;
                            } else if (ratingValue instanceof Long) {
                                updatedRating = ((Long) ratingValue).doubleValue();
                            } else if (ratingValue instanceof String) {
                                try {
                                    updatedRating = Double.parseDouble((String) ratingValue);
                                } catch (NumberFormatException e) {
                                    updatedRating = 0.0;
                                }
                            }

                            // Display the rating

                            if (updatedRating > 0) {
                                binding.rating.setText(String.format("%.1f", updatedRating));
                            } else {
                                binding.rating.setText("0.0");
                            }
                        } else {
                            binding.rating.setText("0.0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PlayListActivity.this, "Failed to update rating", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}