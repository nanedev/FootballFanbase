package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.OddsModel;
import com.malikbisic.sportapp.viewHolder.OddsViewHolder;

import java.util.ArrayList;


/**
 * Created by korisnik on 29/10/2017.
 */

public class OddsAdapter extends RecyclerView.Adapter<OddsViewHolder> {

    Activity activity;
    ArrayList<OddsModel> oddsList;

    public OddsAdapter(Activity activity, ArrayList<OddsModel> oddsList) {
        this.activity = activity;
        this.oddsList = oddsList;
    }

    @Override
    public OddsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.odds_item, parent, false);
        return new OddsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OddsViewHolder holder, int position) {
        OddsModel model = oddsList.get(position);
        holder.updateUI(model);
    }

    @Override
    public int getItemCount() {
        return oddsList.size();
    }
}
