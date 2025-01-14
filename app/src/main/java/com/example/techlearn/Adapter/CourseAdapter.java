package com.example.techlearn.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techlearn.CourseDetailActivity;
import com.example.techlearn.Model.CourseModel;
import com.example.techlearn.Model.UserModel;
import com.example.techlearn.R;
import com.example.techlearn.UploadPlayListActivity;
import com.example.techlearn.databinding.RvCourseDesignBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.viewHolder>{

    Context context;
    ArrayList<CourseModel>list;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public CourseAdapter(Context context, ArrayList<CourseModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_course_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        CourseModel model = list.get(position);

        Picasso.get().load(model.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.courseImage);

        holder.binding.courseTitle.setText(model.getTitle());
        holder.binding.coursePrice.setText("$ " + model.getPrice());
//        holder.binding.name.setText(model.getPostedBy());

        database.getReference().child("user_details").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    UserModel userModel = new UserModel();

                    Picasso.get().load(model.getThumbnail())
                            .placeholder(R.drawable.placeholder)
                            .into(holder.binding.postedByProfile);

                    holder.binding.name.setText(userModel.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CourseDetailActivity.class);
                intent.putExtra("postId", model.getPostId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder{

        RvCourseDesignBinding binding;

        public viewHolder(@NonNull View itemView){
            super(itemView);
            binding = RvCourseDesignBinding.bind(itemView);
        }
    }

    public void updateList(ArrayList<CourseModel> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}
