package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.model.LeagueModel;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.SelectClubActivity;
import com.malikbisic.sportapp.activity.SelectLeagueActivity;

import java.util.ArrayList;

/**
 * Created by korisnik on 01/08/2017.
 */

public class LeagueAdapter extends RecyclerView.Adapter<SelectLeagueActivity.LeagueViewHolder> implements PreferenceManager.OnActivityResultListener {

    ArrayList<LeagueModel> leagueModelArrayList = new ArrayList<>();
    Context ctx;
    Activity activity;
    public static int OPEN_CLUB = 345;
    boolean isOpened = true;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openClub = new Intent(ctx, SelectClubActivity.class);
                openClub.putExtra("leagueID", leagueModel.getCurrent_season_id());
             activity.startActivity(openClub);
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
