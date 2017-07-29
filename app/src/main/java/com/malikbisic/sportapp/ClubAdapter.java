package com.malikbisic.sportapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by korisnik on 29/07/2017.
 */

public class ClubAdapter extends RecyclerView.Adapter<SelectClubActivity.ClubViewHolder> {

    ArrayList<ClubModel> clubModelArrayList = new ArrayList<>();

    public ClubAdapter(ArrayList<ClubModel> clubModelArrayList) {
        this.clubModelArrayList = clubModelArrayList;
    }



    @Override
    public SelectClubActivity.ClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_item, parent, false);

        return new SelectClubActivity.ClubViewHolder(card_view);
    }

    @Override
    public void onBindViewHolder(SelectClubActivity.ClubViewHolder holder, int position) {
        ClubModel clubModel = clubModelArrayList.get(position);
        holder.updateUI(clubModel);
    }

    @Override
    public int getItemCount() {
        return clubModelArrayList.size();
    }
}
