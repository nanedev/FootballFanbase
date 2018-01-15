package com.malikbisic.sportapp.adapter.api;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.FixturesLeagueModel;
import com.malikbisic.sportapp.viewHolder.api.FixtureLeagueViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by korisnik on 25/12/2017.
 */

public class FixturesLeagueAdapter extends RecyclerView.Adapter {
    private ArrayList<FixturesLeagueModel> leagueFixturesList;
    private ArrayList<String> datePrint;
    Activity activity;
    String prevLeaguename;

    public final static int ITEM_VIEW = 1;
    public final static int ITEM_LEAGUE = 2;

    public FixturesLeagueAdapter(ArrayList<FixturesLeagueModel> leagueFixturesList, Activity activity, ArrayList<String> datePrint) {
        this.leagueFixturesList = leagueFixturesList;
        this.activity = activity;
        this.datePrint = datePrint;
    }

    @Override
    public int getItemViewType(int position) {
        if (leagueFixturesList.get(position) instanceof FixturesLeagueModel) {
            return ITEM_VIEW;
        } else if (datePrint.get(position) instanceof String)  {
            return ITEM_LEAGUE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fixture_league_item, parent, false);
            return new FixtureLeagueViewHolder(view);
        } else if (viewType == ITEM_LEAGUE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaguename_item, parent, false);
            return new LeguenameViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FixturesLeagueModel model = leagueFixturesList.get(position);
        int getViewType = holder.getItemViewType();
        if (getViewType == ITEM_VIEW) {
            ((FixtureLeagueViewHolder)holder).updateUI(model);
            //((LeguenameViewHolder)holder).leagueName.setVisibility(View.GONE);

        } else {
            ((LeguenameViewHolder)holder).leagueName.setVisibility(View.VISIBLE);
            ((LeguenameViewHolder)holder).leagueName.setText(model.getDate().toString());
        }



    }

    @Override
    public int getItemCount() {
        return leagueFixturesList.size();
    }

    public static class LeguenameViewHolder extends RecyclerView.ViewHolder {
        public TextView leagueName;

        public LeguenameViewHolder(View v) {
            super(v);
            leagueName = (TextView) v.findViewById(R.id.league_league);
        }
    }
}
