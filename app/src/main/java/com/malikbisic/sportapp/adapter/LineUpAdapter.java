package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.PlayerInfoActivity;
import com.malikbisic.sportapp.model.LineUpModel;
import com.malikbisic.sportapp.viewHolder.LineUpViewHolder;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by korisnik on 04/11/2017.
 */

public class LineUpAdapter extends RecyclerView.Adapter<LineUpViewHolder> {

    ArrayList<LineUpModel> modelArrayList;
    Activity activity;

    public LineUpAdapter(ArrayList<LineUpModel> modelArrayList, Activity activity) {
        this.modelArrayList = modelArrayList;
        this.activity = activity;
    }

    @Override
    public LineUpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lineup_item, null);
        return new LineUpViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LineUpViewHolder holder, int position) {
        final LineUpModel model = modelArrayList.get(position);
        holder.updateUI(model);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infPlayer = new Intent(activity.getApplicationContext(), PlayerInfoActivity.class);
                infPlayer.putExtra("playerID", model.getPlayerID());
                infPlayer.putExtra("openMatchInfo", true);
                infPlayer.putExtra("numberShirt", model.getNumber());
                activity.startActivity(infPlayer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
}
