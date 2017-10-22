package com.malikbisic.sportapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.LivescoreModel;
import com.malikbisic.sportapp.model.TableModel;
import com.malikbisic.sportapp.viewHolder.LivescoreViewHolder;
import com.malikbisic.sportapp.viewHolder.TableViewHolder;

import java.util.ArrayList;

/**
 * Created by korisnik on 21/10/2017.
 */

public class LivescoreAdapter extends RecyclerView.Adapter<LivescoreViewHolder> {
    private ArrayList<LivescoreModel> livescoreList;

    public LivescoreAdapter(ArrayList<LivescoreModel> livescoreList) {
        this.livescoreList = livescoreList;
    }

    @Override
    public LivescoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View livescoreCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.livescore_item,parent,false);


        return new LivescoreViewHolder(livescoreCard);
    }

    @Override
    public void onBindViewHolder(LivescoreViewHolder holder, int position) {
        LivescoreModel model = livescoreList.get(position);
        holder.updateUI(model);

    }

    @Override
    public int getItemCount() {
        return livescoreList.size();
    }
}
