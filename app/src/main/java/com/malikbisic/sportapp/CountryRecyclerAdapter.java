package com.malikbisic.sportapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 29.7.2017.
 */

public class CountryRecyclerAdapter extends RecyclerView.Adapter<CountryRecyclerAdapter.CountriesViewHolder> {
    ArrayList<CountryModel> countriesList ;


    public CountryRecyclerAdapter(ArrayList<CountryModel> countriesList) {
        this.countriesList = countriesList;

    }

    @Override
    public CountriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_countries, parent, false);

        return new CountriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CountriesViewHolder holder, int position) {
        CountryModel report = countriesList.get(position);
holder.updateUI(report);

    }

    @Override
    public int getItemCount() {
        return countriesList.size();
    }

    public static class CountriesViewHolder extends RecyclerView.ViewHolder {

        CircleImageView flag;
        TextView country_name;
        Context context;

        public CountriesViewHolder(View itemView) {
            super(itemView);
            country_name = (TextView) itemView.findViewById(R.id.country_name);
            flag = (CircleImageView) itemView.findViewById(R.id.country_image);

            context = itemView.getContext();


        }

        public void updateUI(CountryModel model) {
            country_name.setText(model.getCountryName());
            Picasso.with(context).load(model.getCountryImage()).into(flag);
        }
    }

}
