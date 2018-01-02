package com.malikbisic.sportapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.TopScorerModel;
import com.malikbisic.sportapp.viewHolder.TopScorerViewHolder;

import java.util.ArrayList;

/**
 * Created by korisnik on 02/01/2018.
 */

public class TopScorerAdapter extends RecyclerView.Adapter<TopScorerViewHolder> {

    ArrayList<TopScorerModel> topScorerModelArrayList;

    public TopScorerAdapter(ArrayList<TopScorerModel> topScorerModelArrayList) {
        this.topScorerModelArrayList = topScorerModelArrayList;
    }

    @Override
    public TopScorerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_scores_item, parent, false);

        return new TopScorerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopScorerViewHolder holder, int position) {
        TopScorerModel model = topScorerModelArrayList.get(position);
        holder.updateUI(model);
    }

    @Override
    public int getItemCount() {
        return topScorerModelArrayList.size();
    }
}
