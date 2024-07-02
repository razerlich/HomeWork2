package com.example.homework2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewUserPhoto;
        TextView textViewName;
        TextView textViewLocation;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUserPhoto = itemView.findViewById(R.id.imageViewUserPhoto);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
        }

        void bind(User user) {
            textViewName.setText(user.getFirstName() + " " + user.getLastName());
            textViewLocation.setText(user.getCity() + ", " + user.getCountry());

            Glide.with(itemView.getContext())
                    .load(user.getImageUrl())
                    .circleCrop()
                    .into(imageViewUserPhoto);
        }
    }
}