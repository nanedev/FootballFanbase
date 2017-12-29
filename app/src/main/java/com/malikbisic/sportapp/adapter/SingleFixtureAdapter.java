package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.LivescoreMatchInfo;
import com.malikbisic.sportapp.model.AllFixturesModel;
import com.malikbisic.sportapp.viewHolder.SingleFixtureViewHolder;

import java.util.ArrayList;

/**
 * Created by Nane on 28.12.2017.
 */

public class SingleFixtureAdapter extends RecyclerView.Adapter<SingleFixtureViewHolder> {
    ArrayList<AllFixturesModel> fixtureList;
    Activity activity;
    Context context;

    public SingleFixtureAdapter(ArrayList<AllFixturesModel> fixtureList, Activity activity, Context context) {
        this.fixtureList = fixtureList;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public SingleFixtureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_fixture_item,parent,false);

        return new SingleFixtureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SingleFixtureViewHolder holder, int position) {
        final AllFixturesModel model = fixtureList.get(position);
        holder.updateUi(model);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMatchInfo = new Intent(activity, LivescoreMatchInfo.class);
                openMatchInfo.putExtra("localTeamName", model.getLocalTeamName());
                openMatchInfo.putExtra("localTeamLogo", model.getLocalTeamLogo());
                openMatchInfo.putExtra("visitorTeamName", model.getVisitorTeamName());
                openMatchInfo.putExtra("visitorTeamLogo", model.getVisitorTeamLogo());
                openMatchInfo.putExtra("leagueName", model.getLeagueName());
                openMatchInfo.putExtra("startTime", model.getTimeStart());
                openMatchInfo.putExtra("score", model.getScore());
                openMatchInfo.putExtra("status", model.getStatus());
                openMatchInfo.putExtra("idFixtures", model.getIdFixtures());
                openMatchInfo.putExtra("localTeamId", model.getLocalTeamId());
                openMatchInfo.putExtra("visitorTeamId", model.getVisitorTeamId());
                openMatchInfo.putExtra("dateMatch", model.getDate());
                activity.startActivity(openMatchInfo);
            }

        });
    }

    @Override
    public int getItemCount() {
        return fixtureList.size();
    }
}
