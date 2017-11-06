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

public class TeamAdapterDef extends RecyclerView.Adapter<TeamSquadViewHolder> {
    Context context;
    Activity activity;
    ArrayList<TeamModel> teamList;

    public TeamAdapterDef() {
    }

    public TeamAdapterDef(Context context, Activity activity, ArrayList<TeamModel> teamList) {
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
                intent.putExtra("playerBirthDate",model.getBirthDate());
                intent.putExtra("playerBirthPlace",model.getBirthPlace());
                intent.putExtra("commonName",model.getCommonName());
                intent.putExtra("firstName",model.getFirstName());
                intent.putExtra("lastName",model.getLastName());
                intent.putExtra("fullName",model.getFullName());
                intent.putExtra("playerPosition",model.getPositionName());
                intent.putExtra("playerHeight",model.getHeight());
                intent.putExtra("playerWeight",model.getWeight());
                intent.putExtra("shirtNumber",String.valueOf(model.getNumberId()));
                intent.putExtra("minutes",String.valueOf(model.getMinutes()));
                intent.putExtra("appearances",String.valueOf(model.getAppearances()));
                intent.putExtra("goals",String.valueOf(model.getGoals()));
                intent.putExtra("assists",String.valueOf(model.getAssists()));
                intent.putExtra("yellowCards",String.valueOf(model.getYellowCards()));
                intent.putExtra("redCards",String.valueOf(model.getRedCards()));
                intent.putExtra("playerInjured",String.valueOf(model.isInjured()));
                intent.putExtra("lineups",String.valueOf(model.getLineups()));
                intent.putExtra("substituteIn",String.valueOf(model.getSubstituteIn()));
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
