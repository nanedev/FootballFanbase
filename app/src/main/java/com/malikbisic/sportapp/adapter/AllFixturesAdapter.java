package com.malikbisic.sportapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.AllFixturesModel;
import com.malikbisic.sportapp.model.LivescoreModel;
import com.malikbisic.sportapp.viewHolder.AllFixturesViewHolder;
import com.malikbisic.sportapp.viewHolder.LivescoreViewHolder;

import java.util.ArrayList;

/**
 * Created by korisnik on 23/10/2017.
 */

public class AllFixturesAdapter extends RecyclerView.Adapter<AllFixturesViewHolder> {

    private ArrayList<AllFixturesModel> allFixturesList;

    public AllFixturesAdapter(ArrayList<AllFixturesModel> allFixturesList) {
        this.allFixturesList = allFixturesList;
    }

    @Override
    public AllFixturesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View allfixturesCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_fixtures_item,parent,false);


        return new AllFixturesViewHolder(allfixturesCard);
    }

    @Override
    public void onBindViewHolder(AllFixturesViewHolder holder, int position) {
    AllFixturesModel model = allFixturesList.get(position);
    holder.updateUI(model);
    }

    @Override
    public int getItemCount() {
        return allFixturesList.size();
    }
}
