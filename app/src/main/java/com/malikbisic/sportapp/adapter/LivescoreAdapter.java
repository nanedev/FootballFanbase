package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.LivescoreMatchInfo;
import com.malikbisic.sportapp.model.LivescoreModel;
import com.malikbisic.sportapp.model.TableModel;
import com.malikbisic.sportapp.viewHolder.LivescoreViewHolder;
import com.malikbisic.sportapp.viewHolder.TableViewHolder;

import java.util.ArrayList;

/**
 * Created by korisnik on 21/10/2017.
 */

public class LivescoreAdapter extends RecyclerView.Adapter<LivescoreViewHolder> {
    private ArrayList<LivescoreModel> livescoreList;
    Activity activity;

    public LivescoreAdapter(ArrayList<LivescoreModel> livescoreList, Activity activity) {
        this.livescoreList = livescoreList;
        this.activity = activity;
    }

    @Override
    public LivescoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View livescoreCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.livescore_item,parent,false);


        return new LivescoreViewHolder(livescoreCard);
    }

    @Override
    public void onBindViewHolder(LivescoreViewHolder holder, int position) {
        final LivescoreModel model = livescoreList.get(position);
        holder.updateUI(model);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMatchInfo = new Intent(activity, LivescoreMatchInfo.class);
                openMatchInfo.putExtra("localTeamName", model.getLocalTeam());
                openMatchInfo.putExtra("localTeamLogo", model.getLocalTeamLogo());
                openMatchInfo.putExtra("visitorTeamName", model.getVisitorTeam());
                openMatchInfo.putExtra("visitorTeamLogo", model.getVisitorTeamLogo());
                openMatchInfo.putExtra("leagueName", model.getLeagueName());
                openMatchInfo.putExtra("startTime", model.getTimeStart());
                activity.startActivity(openMatchInfo);
            }
        });

    }

    @Override
    public int getItemCount() {
        return livescoreList.size();
    }
}
