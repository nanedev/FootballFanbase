package com.malikbisic.sportapp.adapter.api;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.ClubFixturesModel;
import com.malikbisic.sportapp.viewHolder.api.ClubFixturesViewHolder;

import java.util.ArrayList;

/**
 * Created by korisnik on 09/11/2017.
 */

public class ClubFixturesAdapter extends RecyclerView.Adapter<ClubFixturesViewHolder> {

    ArrayList<ClubFixturesModel> clubFixturesModelsList;
    Activity activity;

    public ClubFixturesAdapter(ArrayList<ClubFixturesModel> clubFixturesModelsList, Activity activity) {
        this.clubFixturesModelsList = clubFixturesModelsList;
        this.activity = activity;
    }

    @Override
    public ClubFixturesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fixtures_item_club, parent, false);

        return new ClubFixturesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ClubFixturesViewHolder holder, int position) {
        ClubFixturesModel model = clubFixturesModelsList.get(position);
        holder.updateUI(model);

    }

    @Override
    public int getItemCount() {
        return clubFixturesModelsList.size();
    }
}
