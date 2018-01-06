package com.malikbisic.sportapp.adapter.api;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.FixturesLeagueModel;
import com.malikbisic.sportapp.viewHolder.api.FixtureLeagueViewHolder;

import java.util.ArrayList;

/**
 * Created by korisnik on 25/12/2017.
 */

public class FixturesLeagueAdapter extends RecyclerView.Adapter<FixtureLeagueViewHolder> {
    private ArrayList<FixturesLeagueModel> leagueFixturesList;
    Activity activity;

    public FixturesLeagueAdapter(ArrayList<FixturesLeagueModel> leagueFixturesList, Activity activity) {
        this.leagueFixturesList = leagueFixturesList;
        this.activity = activity;
    }

    @Override
    public FixtureLeagueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fixture_league_item, parent, false);
        return new FixtureLeagueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FixtureLeagueViewHolder holder, int position) {
        FixturesLeagueModel model = leagueFixturesList.get(position);
        holder.updateUI(model);
    }

    @Override
    public int getItemCount() {
        return leagueFixturesList.size();
    }
}
