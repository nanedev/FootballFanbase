package com.malikbisic.sportapp.adapter.api;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.FixturesLeagueModel;
import com.malikbisic.sportapp.model.api.ResultsLeagueModel;
import com.malikbisic.sportapp.viewHolder.api.FixtureLeagueViewHolder;
import com.malikbisic.sportapp.viewHolder.api.ResultsLeagueViewHolder;

import java.util.ArrayList;

/**
 * Created by malikbisic on 16/01/2018.
 */

public class ResultsLeagueAdapter extends RecyclerView.Adapter<ResultsLeagueViewHolder> {

    private ArrayList<ResultsLeagueModel> leagueFixturesList;
    Activity activity;

    public ResultsLeagueAdapter(ArrayList<ResultsLeagueModel> leagueFixturesList, Activity activity) {
        this.leagueFixturesList = leagueFixturesList;
        this.activity = activity;
    }

    @Override
    public ResultsLeagueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_league_item, parent, false);
        return new ResultsLeagueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultsLeagueViewHolder holder, int position) {
        ResultsLeagueModel model = leagueFixturesList.get(position);
        holder.updateUI(model);

    }

    @Override
    public int getItemCount() {
        return leagueFixturesList.size();
    }
}
