package com.malikbisic.sportapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVGParseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 29.7.2017.
 */

public class CountryRecyclerAdapter extends RecyclerView.Adapter<SearchableCountry.CountriesViewHolder> {
    ArrayList<CountryModel> countriesList;
    Context context;
    Activity activity;


    public CountryRecyclerAdapter(ArrayList<CountryModel> countriesList, Context context, Activity activity) {
        this.countriesList = countriesList;
        this.context = context;
        this.activity = activity;

    }
//nsestovd
    @Override
    public SearchableCountry.CountriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_countries, parent, false);

        return new SearchableCountry.CountriesViewHolder(view, context, countriesList, activity);
    }

    @Override
    public void onBindViewHolder(SearchableCountry.CountriesViewHolder holder, int position) {
        CountryModel report = countriesList.get(position);
        try {
            holder.updateUI(report);
        } catch (SVGParseException e) {
            Log.e("error", e.getLocalizedMessage());
        }

    }

    @Override
    public int getItemCount() {
        return countriesList.size();
    }

    public void setFilter(ArrayList<CountryModel> newList) {
        countriesList = new ArrayList<>();
        countriesList.addAll(newList);
        notifyDataSetChanged();
    }


}
