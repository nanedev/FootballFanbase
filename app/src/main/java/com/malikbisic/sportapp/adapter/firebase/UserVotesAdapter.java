package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.malikbisic.sportapp.viewHolder.firebase.UserVotesViewHolder;

import java.util.List;

/**
 * Created by Nane on 20.2.2018.
 */

public class UserVotesAdapter extends RecyclerView.Adapter<UserVotesViewHolder> {
    List<PlayerModel> list;
    Activity activity;


    public UserVotesAdapter(List<PlayerModel> list, Activity activity) {
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
PlayerModel model  = list.get(position);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
