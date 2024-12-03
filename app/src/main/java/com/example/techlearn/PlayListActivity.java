package com.example.techlearn;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.techlearn.Adapter.PlayListAdapter;
import com.example.techlearn.Adapter.PlayListUserAdapter;
import com.example.techlearn.Model.PlayListModel;
import com.example.techlearn.databinding.ActivityPlayListBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
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

import javax.crypto.SecretKey;

public class PlayListActivity extends AppCompatActivity {

    ActivityPlayListBinding binding;
    private String postId, postedByName, introUrl, title, duration, rating, description;
    private long price;
    private SimpleExoPlayer simpleExoPlayer;
    ArrayList<PlayListModel>list;
    PlayListUserAdapter adapter;
    private Dialog loadingDialog;
    Button enrollNow;
    String publishableKey = "pk_test_51QLXl2L0kLdfcs5yhjcDuc0WAnDoZgIu1Ts88JhU7ZpGDDmkZ8X6mkhAnRuFuhYQLePpmWrcKXJby0qtvMiw6FVc00DTOLaHK5";
    String secretKey = "sk_test_51QLXl2L0kLdfcs5y98DW4YbvElpIN2OwmA8WEsc7Di4clKVgLGObRBuZQcpfIvZNQ6PsyOrFQk3sRraZI82ksy2n00g7TEFBKn";
    String customerId;
    String ephericalKey;
    String clientSecret;
    PaymentSheet paymentSheet;

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

        PaymentConfiguration.init(this, publishableKey);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);

        });

        binding.btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                paymentFlow();

            }
        });

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerId = object.getString("id");

                            Toast.makeText(PlayListActivity.this, customerId + " customer id", Toast.LENGTH_SHORT).show();
                            getEmphericalKey();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PlayListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+ secretKey);

                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void paymentFlow() {

        paymentSheet.presentWithPaymentIntent(clientSecret, new PaymentSheet.Configuration("TechLearn", new PaymentSheet.CustomerConfiguration(
                customerId,
                ephericalKey
        )));

    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if(paymentSheetResult instanceof PaymentSheetResult.Completed){

            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();

        }

    }

    private void getEmphericalKey() {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ephericalKey = object.getString("id");

                            Toast.makeText(PlayListActivity.this, ephericalKey + " epherical key", Toast.LENGTH_SHORT).show();

                            getClientSecret(customerId, ephericalKey);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PlayListActivity.this, error.getLocalizedMessage() + "can't gert", Toast.LENGTH_SHORT).show();


            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+ secretKey);
                header.put("Stripe-Version", "2024-10-28.acacia");

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void getClientSecret(String customerId, String ephericalKey) {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            clientSecret = object.getString("client_secret");

                            Toast.makeText(PlayListActivity.this, clientSecret + " client_secret", Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PlayListActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+ secretKey);

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                params.put("amount", String.valueOf(price));
                params.put("currency","usd");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
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