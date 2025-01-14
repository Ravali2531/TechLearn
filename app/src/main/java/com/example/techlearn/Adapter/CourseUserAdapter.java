//package com.example.techlearn.Adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.techlearn.Model.CourseModel;
//import com.example.techlearn.Model.UserModel;
//import com.example.techlearn.PlayListActivity;
//import com.example.techlearn.R;
//import com.example.techlearn.databinding.RvCourseDesignBinding;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//
//public class CourseUserAdapter extends RecyclerView.Adapter<CourseUserAdapter.viewHolder> {
//
//    Context context;
//    ArrayList<CourseModel> list;
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private String postedByName;
//
//    public CourseUserAdapter(Context context, ArrayList<CourseModel> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    @NonNull
//    @Override
//    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.rv_course_design, parent, false);
//        return new viewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
//        CourseModel model = list.get(position);
//
//        // Load course thumbnail if available
//        if (model.getThumbnail() != null && !model.getThumbnail().isEmpty()) {
//            Picasso.get().load(model.getThumbnail())
//                    .placeholder(R.drawable.placeholder)
//                    .into(holder.binding.courseImage);
//        } else {
//            holder.binding.courseImage.setImageResource(R.drawable.placeholder);
//        }
//
//        holder.binding.courseTitle.setText(model.getTitle());
//        holder.binding.coursePrice.setText(String.valueOf(model.getPrice()));
//
//        // Load postedBy details
//        if (model.getPostedBy() != null) {
//            database.getReference().child("admin_details").child(model.getPostedBy())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                UserModel userModel = snapshot.getValue(UserModel.class);
//                                if (userModel != null) {
//                                    postedByName = userModel.getName();
//                                    holder.binding.name.setText(postedByName);
//                                    if (userModel.getProfile() != null && !userModel.getProfile().isEmpty()) {
//                                        Picasso.get().load(userModel.getProfile())
//                                                .placeholder(R.drawable.placeholder)
//                                                .into(holder.binding.postedByProfile);
//                                    } else {
//                                        holder.binding.postedByProfile.setImageResource(R.drawable.placeholder);
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Toast.makeText(context, "Error loading user details", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//
//        holder.itemView.setOnClickListener(view -> {
//            Intent intent = new Intent(context, PlayListActivity.class);
//            intent.putExtra("postId", model.getPostId());
//            intent.putExtra("name", postedByName);
//            intent.putExtra("introUrl", model.getIntroVideo());
//            intent.putExtra("title", model.getTitle());
//            intent.putExtra("price", model.getPrice());
//            intent.putExtra("rate", model.getRating());
//            intent.putExtra("duration", model.getDuration());
//            intent.putExtra("description", model.getDescription());
//            context.startActivity(intent);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public class viewHolder extends RecyclerView.ViewHolder {
//        RvCourseDesignBinding binding;
//
//        public viewHolder(@NonNull View itemView) {
//            super(itemView);
//            binding = RvCourseDesignBinding.bind(itemView);
//        }
//    }
//}


package com.example.techlearn.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techlearn.Model.CourseModel;
import com.example.techlearn.Model.UserModel;
import com.example.techlearn.PlayListActivity;
import com.example.techlearn.R;
import com.example.techlearn.databinding.RvCourseDesignBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CourseUserAdapter extends RecyclerView.Adapter<CourseUserAdapter.viewHolder> {

    private Context context;
    private List<CourseModel> originalList;
    private List<CourseModel> filteredList;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String postedByName;

    public CourseUserAdapter(Context context, ArrayList<CourseModel> list) {
        this.context = context;
        this.originalList = list;
        this.filteredList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_course_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CourseModel model = filteredList.get(position);

        // Load course thumbnail if available
        if (model.getThumbnail() != null && !model.getThumbnail().isEmpty()) {
            Picasso.get().load(model.getThumbnail())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.courseImage);
        } else {
            holder.binding.courseImage.setImageResource(R.drawable.placeholder);
        }

        holder.binding.courseTitle.setText(model.getTitle());
        holder.binding.coursePrice.setText(String.valueOf(model.getPrice()));

        // Load postedBy details
        if (model.getPostedBy() != null) {
            database.getReference().child("user_details").child(model.getPostedBy())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                UserModel userModel = snapshot.getValue(UserModel.class);
                                if (userModel != null) {
                                    postedByName = userModel.getName();
                                    holder.binding.name.setText(postedByName);
                                    if (userModel.getProfile() != null && !userModel.getProfile().isEmpty()) {
                                        Picasso.get().load(userModel.getProfile())
                                                .placeholder(R.drawable.placeholder)
                                                .into(holder.binding.postedByProfile);
                                    } else {
                                        holder.binding.postedByProfile.setImageResource(R.drawable.placeholder);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Error loading user details", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PlayListActivity.class);
            intent.putExtra("postId", model.getPostId());
            intent.putExtra("name", postedByName);
            intent.putExtra("introUrl", model.getIntroVideo());
            intent.putExtra("title", model.getTitle());
            intent.putExtra("price", model.getPrice());
            intent.putExtra("rate", model.getRating());
            intent.putExtra("duration", model.getDuration());
            intent.putExtra("description", model.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // Method to filter courses based on search query
    public void filter(String query) {
        if (query.isEmpty()) {
            filteredList.clear();
            filteredList.addAll(originalList);
        } else {
            List<CourseModel> filtered = new ArrayList<>();
            for (CourseModel model : originalList) {
                if (model.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(model);
                }
            }
            filteredList.clear();
            filteredList.addAll(filtered);
        }
        notifyDataSetChanged();
    }

    // Method to filter courses by enrollment status
    public void filterByEnrollmentStatus(boolean enrolled, List<String> enrolledCourseIds) {
        filteredList.clear();

        if (enrolled) {
            for (CourseModel model : originalList) {
                if (enrolledCourseIds.contains(model.getPostId())) {
                    filteredList.add(model);
                }
            }
        } else {
            for (CourseModel model : originalList) {
                if (!enrolledCourseIds.contains(model.getPostId())) {
                    filteredList.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        RvCourseDesignBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvCourseDesignBinding.bind(itemView);
        }
    }
}
