package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.LeagueInfoActivity;
import com.malikbisic.sportapp.activity.LivescoreMatchInfo;
import com.malikbisic.sportapp.model.AllFixturesModel;
import com.malikbisic.sportapp.viewHolder.SingleFixtureViewHolder;

import java.util.ArrayList;

/**
 * Created by Nane on 28.12.2017.
 */

public class SingleFixtureAdapter extends RecyclerView.Adapter {
    ArrayList<AllFixturesModel> fixtureList;
    Activity activity;
    Context context;
    private static final int SINGLE_FOXTURE_VIEW = 0;
    private static final int SEE_TABLE_VIEW = 1;
    public SingleFixtureAdapter(ArrayList<AllFixturesModel> fixtureList, Activity activity, Context context) {
        this.fixtureList = fixtureList;
        this.activity = activity;
        this.context = context;
    }
/*
    @Override
    public SingleFixtureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == SINGLE_FOXTURE_VIEW ) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_fixture_item, parent, false);

            return new SingleFixtureViewHolder(view);
        }else if (viewType == SEE_TABLE_VIEW){
            View seeTableView = LayoutInflater.from(parent.getContext()).inflate(R.layout.see_table_item_layout,parent,false);
            return new
        }
    }*/

    @Override
    public int getItemViewType(int position) {
        return fixtureList.get(position) != null ? SINGLE_FOXTURE_VIEW : SEE_TABLE_VIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == SINGLE_FOXTURE_VIEW){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_fixture_item,parent,false);
            return new SingleFixtureViewHolder(view);

        }else if (viewType == SEE_TABLE_VIEW){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.see_table_item_layout,parent,false);
            return new SeeTableViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int getViewType = holder.getItemViewType();
        final AllFixturesModel model = fixtureList.get(position);
        if (getViewType == SINGLE_FOXTURE_VIEW){
            ((SingleFixtureViewHolder) holder).updateUi(model);

            if (position == getItemCount() -1){
                ((SingleFixtureViewHolder) holder).seeTable.setVisibility(View.VISIBLE);
            } else {
                ((SingleFixtureViewHolder) holder).seeTable.setVisibility(View.GONE);
            }

            ((SingleFixtureViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

            ((SingleFixtureViewHolder) holder).seeTable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, LeagueInfoActivity.class);
                    intent.putExtra("seasonId",String.valueOf(model.getSeasonId()));
                    intent.putExtra("league_id",model.getLeagueId());
                    intent.putExtra("leagueName",model.getLeagueName());
                    intent.putExtra("countryName",model.getCountryName());
                    activity.startActivity(intent);
                }
            });


        }else  if (getViewType == SEE_TABLE_VIEW){

            ((SeeTableViewHolder) holder).seeTable.setVisibility(View.VISIBLE);
            ((SeeTableViewHolder) holder).dfss.setText("radi li ovo");

        }


    }

/*    @Override
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
    }*/

    @Override
    public int getItemCount() {
        return fixtureList.size();
    }


    public class SeeTableViewHolder extends RecyclerView.ViewHolder{

       public RelativeLayout seeTable;
        TextView dfss;

        public SeeTableViewHolder(View itemView) {
            super(itemView);

            seeTable = (RelativeLayout) itemView.findViewById(R.id.seeTableLay);
            dfss = (TextView) itemView.findViewById(R.id.seetabletext);
        }
    }
}
