package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.PlayerInfoActivity;
import com.malikbisic.sportapp.model.TeamModel;
import com.malikbisic.sportapp.viewHolder.TeamSquadViewHolder;

import java.util.ArrayList;

/**
 * Created by Nane on 24.10.2017.
 */

public class TeamAdapter extends RecyclerView.Adapter<TeamSquadViewHolder> {
    Context context;
    Activity activity;
    ArrayList<TeamModel> teamList;

    public TeamAdapter() {
    }

    public TeamAdapter(Context context, Activity activity, ArrayList<TeamModel> teamList) {
        this.context = context;
        this.activity = activity;
        this.teamList = teamList;
    }

    @Override
    public TeamSquadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item, parent, false);


        return new TeamSquadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamSquadViewHolder holder, int position) {
        final TeamModel model = teamList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerInfoActivity.class);
                intent.putExtra("playerName",model.getFullName());
                intent.putExtra("playerImage",model.getPlayerImage());
                activity.startActivity(intent);
            }
        });

       holder.updateUI(model,context);

    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }
}
