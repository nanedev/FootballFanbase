package com.malikbisic.sportapp.adapter.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.api.AboutFootballClub;
import com.malikbisic.sportapp.fragment.api.FragmentLeagueInfoFixtures;
import com.malikbisic.sportapp.fragment.api.FragmentLeagueInfoResults;
import com.malikbisic.sportapp.fragment.api.FragmentLeagueInfoStandings;
import com.malikbisic.sportapp.model.api.TableModel;
import com.malikbisic.sportapp.viewHolder.api.TableViewHolder;

import java.util.ArrayList;

/**
 * Created by Nane on 20.10.2017.
 */

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {
    Context context;
    private ArrayList<TableModel> tableList;
    Activity activity;

    public TableAdapter(ArrayList<TableModel> tableList, Context context, Activity activity) {
        this.tableList = tableList;
        this.context = context;
        this.activity = activity;
    }


    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View table_card = LayoutInflater.from(parent.getContext()).inflate(R.layout.league_table_row, parent, false);


        return new TableViewHolder(table_card);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        final TableModel model = tableList.get(position);
        holder.updateUI(model, context);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AboutFootballClub.class);
                intent.putExtra("leagueName", FragmentLeagueInfoStandings.leagueName);
                intent.putExtra("teamId", String.valueOf(model.getTeamId()));
                intent.putExtra("teamLogo", model.getClubLogo());
                intent.putExtra("teamName", model.getTeamName());
                intent.putExtra("countryId", model.getCountryId());
                intent.putExtra("countryName", model.getCountryName());
                intent.putExtra("seasonID", FragmentLeagueInfoStandings.currentSeasonId);
                intent.putExtra("leagueID", FragmentLeagueInfoResults.URL_LEAGUE_ID);

                activity.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }
}
