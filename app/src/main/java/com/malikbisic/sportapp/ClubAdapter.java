package com.malikbisic.sportapp;

import android.content.Context;
import android.content.Intent;
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
    Context ctx;

    public ClubAdapter(ArrayList<ClubModel> clubModelArrayList, Context ctx) {
        this.clubModelArrayList = clubModelArrayList;
        this.ctx = ctx;
    }



    @Override
    public SelectClubActivity.ClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_item, parent, false);

        return new SelectClubActivity.ClubViewHolder(card_view);
    }

    @Override
    public void onBindViewHolder(final SelectClubActivity.ClubViewHolder holder, int position) {
        ClubModel clubModel = clubModelArrayList.get(position);
        holder.updateUI(clubModel);

        holder.vm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendClub = new Intent(ctx, EnterUsernameForApp.class);
                sendClub.putExtra("clubName", holder.clubName.getText());
                sendClub.putExtra("clubLogo", (String) holder.clubLogo.getTag());
                sendClub.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(sendClub);

            }
        });
    }

    @Override
    public int getItemCount() {
        return clubModelArrayList.size();
    }
}
