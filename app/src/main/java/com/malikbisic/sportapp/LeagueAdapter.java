package com.malikbisic.sportapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by korisnik on 01/08/2017.
 */

public class LeagueAdapter extends RecyclerView.Adapter<SelectLeagueActivity.LeagueViewHolder> implements PreferenceManager.OnActivityResultListener {

    ArrayList<LeagueModel> leagueModelArrayList = new ArrayList<>();
    Context ctx;
    Activity activity;
    static int OPEN_CLUB = 345;

    public LeagueAdapter(ArrayList<LeagueModel> leagueModelArrayList, Context ctx, Activity activity) {
        this.leagueModelArrayList = leagueModelArrayList;
        this.ctx = ctx;
        this.activity = activity;
    }


    @Override
    public SelectLeagueActivity.LeagueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.league_item, parent, false);

        return new SelectLeagueActivity.LeagueViewHolder(card_view, leagueModelArrayList);
    }

    @Override
    public void onBindViewHolder(final SelectLeagueActivity.LeagueViewHolder holder, int position) {
        final LeagueModel leagueModel = leagueModelArrayList.get(position);
        holder.updateUI(leagueModel);

        holder.vm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LEAGUE", "ID: " + leagueModel.getCurrent_season_id());

                Intent openClub = new Intent(ctx, SelectClubActivity.class);
                openClub.putExtra("leagueID", leagueModel.getCurrent_season_id());
                openClub.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivityForResult(openClub, OPEN_CLUB);
            }
        });
    }


    @Override
    public int getItemCount() {
        return leagueModelArrayList.size();
    }

    @Override
    public boolean onActivityResult(int requestData, int resultCode, Intent data) {





        return true;
    }
}
