package com.example.techlearn.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techlearn.Model.CourseModel;
import com.example.techlearn.Model.PlayListModel;
import com.example.techlearn.Model.UserModel;
import com.example.techlearn.R;
import com.example.techlearn.UploadPlayListActivity;
import com.example.techlearn.databinding.RvCourseDesignBinding;
import com.example.techlearn.databinding.RvPlaylistDesignBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayListUserAdapter extends RecyclerView.Adapter<PlayListUserAdapter.viewHolder>{

    Context context;
    ArrayList<PlayListModel>list;
    videoListener listener;

    public PlayListUserAdapter(Context context, ArrayList<PlayListModel> list, videoListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_playlist_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        PlayListModel model = list.get(position);

        holder.binding.title.setText(model.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onClick(position, list.get(position).getKey(), model.getVideoUrl(), list.size());
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder{

        RvPlaylistDesignBinding binding;

        public viewHolder(@NonNull View itemView){
            super(itemView);
            binding = RvPlaylistDesignBinding.bind(itemView);
        }
    }

    public interface videoListener{

        public void onClick(int position, String key, String videoUrl, int size);
    }
}
