package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.viewHolder.firebase.PlayerRankingViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Nane on 27.1.2018.
 */

public class PlayersRankingMonthAdapter extends RecyclerView.Adapter<PlayerRankingViewHolder> {
    List<PlayerModel> list;
    Activity activity;
    FirebaseAuth mAuth;
    long myPointsVote;
    long playerPoints;

    public PlayersRankingMonthAdapter(List<PlayerModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public PlayerRankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_month_item, parent, false);

        return new PlayerRankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlayerRankingViewHolder holder, int position) {
        final PlayerModel model = list.get(position);
        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImage())
                .into(holder.playerImageRankingMonth);
        holder.playerPointsRankingMonth.setText(list.get(position).getPoints() + " pts");
        holder.playerPositionRankingMonth.setText(model.getId() + ".");
holder.playerName.setText(model.getName());
holder.setNumbervotes(model);


    }


    @Override
    public int getItemCount() {
        return list.size();
    }


}

