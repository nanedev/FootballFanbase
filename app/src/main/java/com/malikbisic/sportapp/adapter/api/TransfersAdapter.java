package com.malikbisic.sportapp.adapter.api;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.Transfers;
import com.malikbisic.sportapp.viewHolder.api.TransfersViewHolder;

import java.util.ArrayList;

/**
 * Created by Nane on 9.11.2017.
 */

public class TransfersAdapter extends RecyclerView.Adapter<TransfersViewHolder> {
   ArrayList<Transfers> transfersList;
    Activity activity;
    Context context;

    public TransfersAdapter(ArrayList<Transfers> transfersList, Activity activity, Context context) {
       this.transfersList = transfersList;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public TransfersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transfer_row, parent, false);

        return new TransfersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransfersViewHolder holder, int position) {
        Transfers transfers = transfersList.get(position);
        holder.updateUI(transfers, context);


    }

    @Override
    public int getItemCount() {
       return transfersList.size();
    }
}
