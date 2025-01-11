package com.example.techlearn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techlearn.Model.UserModel;
import com.example.techlearn.R;

import java.util.List;

public class EnrolledMembersAdapter extends RecyclerView.Adapter<EnrolledMembersAdapter.EnrolledMemberViewHolder> {

    private Context context;
    private List<UserModel> enrolledMembersList;

    public EnrolledMembersAdapter(Context context, List<UserModel> enrolledMembersList) {
        this.context = context;
        this.enrolledMembersList = enrolledMembersList;
    }

    @NonNull
    @Override
    public EnrolledMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_enrolled_member, parent, false);
        return new EnrolledMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnrolledMemberViewHolder holder, int position) {
        UserModel user = enrolledMembersList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.emailTextView.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return enrolledMembersList.size();
    }

    public static class EnrolledMemberViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, emailTextView;

        public EnrolledMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
        }
    }
}
