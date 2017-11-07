package com.malikbisic.sportapp.adapter;

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
import com.malikbisic.sportapp.viewHolder.LivescoreViewHolder;

import java.util.ArrayList;

/**
 * Created by korisnik on 23/10/2017.
 */

public class AllFixturesAdapter extends RecyclerView.Adapter<AllFixturesViewHolder> {

    private ArrayList<AllFixturesModel> allFixturesList;
    Activity activity;

    public AllFixturesAdapter(ArrayList<AllFixturesModel> allFixturesList, Activity activity) {
        this.allFixturesList = allFixturesList;
        this.activity = activity;
    }

    @Override
    public AllFixturesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View allfixturesCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_fixtures_item,parent,false);


        return new AllFixturesViewHolder(allfixturesCard);
    }

    @Override
    public void onBindViewHolder(AllFixturesViewHolder holder, int position) {
    final AllFixturesModel model = allFixturesList.get(position);
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

                Log.i("id", model.getIdFixtures());
                activity.startActivity(openMatchInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allFixturesList.size();
    }
}
