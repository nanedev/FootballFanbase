//package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.LivescoreMatchInfo;
import com.malikbisic.sportapp.model.AllFixturesModel;
import com.malikbisic.sportapp.model.LivescoreModel;
import com.malikbisic.sportapp.viewHolder.AllFixturesViewHolder;
import com.malikbisic.sportapp.viewHolder.LeagueNameViewHolder;
import com.malikbisic.sportapp.viewHolder.LivescoreViewHolder;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by korisnik on 23/10/2017.
 */
/*
public class AllFixturesAdapter extends ExpandableRecyclerViewAdapter<LeagueNameViewHolder, AllFixturesViewHolder> {


    Activity activity;

    public AllFixturesAdapter(List<? extends ExpandableGroup> groups, Activity activity) {
        super(groups);

        this.activity = activity;
    }

    @Override
    public LeagueNameViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View allfixturesCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_all_fixtures_item,parent,false);

        return new LeagueNameViewHolder(allfixturesCard);
    }

    @Override
    public AllFixturesViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View allfixturesCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_fixtures_item,parent,false);

        return new AllFixturesViewHolder(allfixturesCard);
    }

    @Override
    public void onBindChildViewHolder(AllFixturesViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final AllFixturesModel model = (AllFixturesModel) group.getItems().get(childIndex);
        holder.updateUI(model);

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

                Log.i("id", model.getIdFixtures());
                activity.startActivity(openMatchInfo);
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(LeagueNameViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setLeagueName(group);
        holder.setFlag(group);
        holder.collapse();
    }
} */
