package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.model.ClubModel;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.SelectClubActivity;
import com.malikbisic.sportapp.activity.SelectLeagueActivity;

import java.util.ArrayList;

/**
 * Created by korisnik on 29/07/2017.
 */

public class ClubAdapter extends RecyclerView.Adapter<SelectClubActivity.ClubViewHolder> {

    ArrayList<ClubModel> clubModelArrayList = new ArrayList<>();
    Context ctx;
    Activity activity;

    public ClubAdapter(ArrayList<ClubModel> clubModelArrayList, Context ctx, Activity activity) {
        this.clubModelArrayList = clubModelArrayList;
        this.ctx = ctx;
        this.activity = activity;
    }



    @Override
    public SelectClubActivity.ClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_item, parent, false);

        return new SelectClubActivity.ClubViewHolder(card_view, clubModelArrayList);
    }

    @Override
    public void onBindViewHolder(final SelectClubActivity.ClubViewHolder holder, int position) {
        final ClubModel clubModel = clubModelArrayList.get(position);
        holder.updateUI(clubModel);

        holder.vm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendClub = new Intent(ctx, SelectLeagueActivity.class);
                sendClub.putExtra("clubNameLeague", clubModel.getName());
                sendClub.putExtra("clubLogoLeague", clubModel.getLogo_path());
                activity.setResult(Activity.RESULT_OK, sendClub);
                activity.finish();

            }
        });
    }



    @Override
    public int getItemCount() {
        return clubModelArrayList.size();
    }
}
