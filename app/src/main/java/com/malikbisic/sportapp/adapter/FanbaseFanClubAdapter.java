package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.FanbaseFanClubTable;
import com.malikbisic.sportapp.model.ClubTable;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by korisnik on 10/12/2017.
 */

public class FanbaseFanClubAdapter extends RecyclerView.Adapter<FanbaseFanClubTable.ClubTableViewHolder> {

    List<ClubTable> listClub;
    Activity activity;
    Context ctx;
    int pos = 0;

    public FanbaseFanClubAdapter(List<ClubTable> listClub, Activity activity, Context ctx) {
        this.listClub = listClub;
        this.activity = activity;
        this.ctx = ctx;
    }

    @Override
    public FanbaseFanClubTable.ClubTableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fanclabfans_row, parent, false);
        return new FanbaseFanClubTable.ClubTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FanbaseFanClubTable.ClubTableViewHolder viewHolder, int position) {
        ClubTable model = listClub.get(position);
        pos++;
        viewHolder.positionClub.setText("" +pos);
        viewHolder.clubName.setText(model.getClubName());
        Picasso.with(ctx).load(model.getClubLogo()).into(viewHolder.clubLogo);
        viewHolder.numberFans.setText("" + model.getNumberClubFan());
    }

    @Override
    public int getItemCount() {
        return listClub.size();
    }
}
