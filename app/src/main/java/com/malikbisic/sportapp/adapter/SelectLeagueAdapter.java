package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.FragmentAllMatches;
import com.malikbisic.sportapp.activity.LeagueInfoActivity;
import com.malikbisic.sportapp.activity.SelectClubActivity;
import com.malikbisic.sportapp.model.LeagueModel;

import java.util.ArrayList;

/**
 * Created by Nane on 20.10.2017.
 */

public class SelectLeagueAdapter extends RecyclerView.Adapter<FragmentAllMatches.SelectLeagueViewHolder> {
    ArrayList<LeagueModel> leagueModelArrayList = new ArrayList<>();
    Context ctx;
    Activity activity;
    boolean isOpened = true;
    public SelectLeagueAdapter(ArrayList<LeagueModel> leagueModelArrayList, Context ctx, Activity activity) {
        this.leagueModelArrayList = leagueModelArrayList;
        this.ctx = ctx;
        this.activity = activity;
    }

    @Override
    public FragmentAllMatches.SelectLeagueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_league_item,parent,false);


        return new FragmentAllMatches.SelectLeagueViewHolder(view,leagueModelArrayList);
    }

    @Override
    public void onBindViewHolder(final FragmentAllMatches.SelectLeagueViewHolder holder, int position) {
        final LeagueModel leagueModel = leagueModelArrayList.get(position);
        holder.updateUI(leagueModel);

        holder.leagueName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openClub = new Intent(ctx, LeagueInfoActivity.class);
                openClub.putExtra("leagueID", leagueModel.getCurrent_season_id());
                openClub.putExtra("leagueName",leagueModel.getName());
                activity.startActivity(openClub);

            }
        });
        holder.countryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (isOpened){
                    holder.leagueName.setVisibility(View.GONE);
                    isOpened = false;
                } else {
                    holder.leagueName.setVisibility(View.VISIBLE);
                    isOpened = true;
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return leagueModelArrayList.size();
    }
}
