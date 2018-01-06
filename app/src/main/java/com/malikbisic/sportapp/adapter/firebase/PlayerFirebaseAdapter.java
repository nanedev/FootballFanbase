package com.malikbisic.sportapp.adapter.firebase;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.FootballPlayer;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.viewHolder.api.PlayersInProfileViewHolder;

import java.util.List;

/**
 * Created by korisnik on 06/01/2018.
 */

public class PlayerFirebaseAdapter extends RecyclerView.Adapter<PlayersInProfileViewHolder> {

    List<PlayerModel> list;

    public PlayerFirebaseAdapter(List<PlayerModel> list) {
        this.list = list;
    }

    @Override
    public PlayersInProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_profileplayer, parent, false);
        return new PlayersInProfileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlayersInProfileViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImage())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
