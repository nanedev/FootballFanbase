package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.SeeUsersVotesActivity;
import com.malikbisic.sportapp.activity.firebase.UserVotesActivity;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.viewHolder.firebase.PlayerAllTimeRankingViewHolder;
import com.malikbisic.sportapp.viewHolder.firebase.PlayerRankingViewHolder;

import java.util.List;

/**
 * Created by Nane on 28.1.2018.
 */

public class PlayersRankingAllTimeAdapter extends RecyclerView.Adapter<PlayerAllTimeRankingViewHolder> {
    List<PlayerModel> list;
    Activity activity;
    FirebaseAuth mAuth;
    long myPointsVote;
    long playerPoints;


    public PlayersRankingAllTimeAdapter(List<PlayerModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public PlayerAllTimeRankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_month_item, parent, false);

        return new PlayerAllTimeRankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerAllTimeRankingViewHolder holder, int position) {
        final PlayerModel model = list.get(position);
        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImage())
                .into(holder.playerImageRankingMonth);
        holder.playerPointsRankingMonth.setText(list.get(position).getPoints() + " pts");
        holder.playerPositionRankingMonth.setText(model.getId() + ".");
        holder.playerName.setText(model.getName());
        holder.setNumbervotes(model);


        holder.seeVotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, UserVotesActivity.class);
                intent.putExtra("fromAllTimeRanking",true);
                intent.putExtra("playerID",model.getPlayerID());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
