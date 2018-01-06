package com.malikbisic.sportapp.adapter.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.listener.OnLoadMoreListener;
import com.malikbisic.sportapp.activity.api.SingleFixtureActivity;
import com.malikbisic.sportapp.model.api.LeagueModel;
import com.malikbisic.sportapp.viewHolder.api.AllFixturesViewHolder;

import java.util.ArrayList;

/**
 * Created by korisnik on 23/10/2017.
 */

public class AllFixturesAdapter extends RecyclerView.Adapter<AllFixturesViewHolder> {

    private ArrayList<LeagueModel> allFixturesList;
    Activity activity;
    Context context;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    boolean isLoading;
    boolean isAllLoaded = false;
    private OnLoadMoreListener onLoadMoreListener;
    public AllFixturesAdapter(ArrayList<LeagueModel> allFixturesList, Activity activity,RecyclerView recyclerView) {
        this.allFixturesList = allFixturesList;
        this.activity = activity;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                lastVisibleItem = llm.findLastVisibleItemPosition();
                totalItemCount = llm.getItemCount();

                Log.i("paginacijaLASTVISIBLE", String.valueOf(lastVisibleItem));
                Log.i("paginacijaTOTAL", String.valueOf(totalItemCount));



                if(!isLoading  && totalItemCount <= (lastVisibleItem + visibleThreshold))
                {
                    if(onLoadMoreListener != null)
                    {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading =true;
                }

            }
        });
    }

    public void setOnLoadMore(OnLoadMoreListener onLoadMore)
    {
        onLoadMoreListener = onLoadMore;
    }

    public void setIsLoading(boolean param)
    {
        isLoading = param;
    }

    public void isFullLoaded(boolean param){
        isAllLoaded = param;
    }

    @Override
    public AllFixturesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View allfixturesCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_fixtures_item,parent,false);


        return new AllFixturesViewHolder(allfixturesCard);
    }

    @Override
    public void onBindViewHolder(AllFixturesViewHolder holder, int position) {
    final LeagueModel model = allFixturesList.get(position);
    holder.setFixtureId(model.getFixtureId());

    holder.updateUI(model);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, SingleFixtureActivity.class);
          intent.putExtra("countryName",model.getCountry_name());
          intent.putExtra("leagueName",model.getName());
          intent.putExtra("leagueID",model.getLeague_id());
          intent.putExtra("seasonId",model.getCurrent_season_id());
          intent.putExtra("datum", model.getDate());
          activity.startActivity(intent);

          Log.i("tag", String.valueOf(model.getFixtureId()));
        }
    });


    }

    @Override
    public int getItemCount() {
        return allFixturesList.size();
    }
}
