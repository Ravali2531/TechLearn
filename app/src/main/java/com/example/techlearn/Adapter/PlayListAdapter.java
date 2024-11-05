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

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.viewHolder>{

    Context context;
    ArrayList<PlayListModel>list;


    public PlayListAdapter(Context context, ArrayList<PlayListModel> list) {
        this.context = context;
        this.list = list;
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
}
