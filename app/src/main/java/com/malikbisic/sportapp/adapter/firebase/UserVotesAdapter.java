package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.malikbisic.sportapp.viewHolder.firebase.UserVotesViewHolder;

import java.util.List;

/**
 * Created by Nane on 20.2.2018.
 */

public class UserVotesAdapter extends RecyclerView.Adapter<UserVotesViewHolder> {
    List<UsersModel> list;
    Activity activity;


    public UserVotesAdapter(List<UsersModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public UserVotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_votes_item,parent,false);

        return new UserVotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserVotesViewHolder holder, int position) {
UsersModel model  = list.get(position);

        Glide.with(holder.itemView.getContext()).load(model.getProfileImage()).into(holder.userProfileImage);

        holder.username.setText(model.getUsername());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
