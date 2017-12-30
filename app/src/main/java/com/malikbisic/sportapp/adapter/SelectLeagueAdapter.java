package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.FragmentAllMatches;
import com.malikbisic.sportapp.activity.LeagueInfoActivity;
import com.malikbisic.sportapp.activity.OnLoadMoreListener;
import com.malikbisic.sportapp.model.LeagueModel;

import java.util.ArrayList;

/**
 * Created by Nane on 20.10.2017.
 */

public class SelectLeagueAdapter extends RecyclerView.Adapter {
    ArrayList<LeagueModel> leagueModelArrayList = new ArrayList<>();
    Context ctx;
    Activity activity;
    boolean isOpened = true;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    String key;
    boolean isLoading;
    boolean isAllLoaded = false;

    private static final int ITEM_VIEW = 0;
    private static final int ITEM_LOADING = 1;
    public SelectLeagueAdapter(ArrayList<LeagueModel> leagueModelArrayList, Context ctx, Activity activity, RecyclerView recyclerView) {
        this.leagueModelArrayList = leagueModelArrayList;
        this.ctx = ctx;
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

    @Override
    public int getItemViewType(int position) {
        return leagueModelArrayList.get(position) != null ? ITEM_VIEW : ITEM_LOADING;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_league_item, parent, false);

            return new FragmentAllMatches.SelectLeagueViewHolder(view, leagueModelArrayList);
        } else if (viewType == ITEM_LOADING){
            View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false);
            return new MainPageAdapter.ProgressViewHolder(v1);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int getViewType = holder.getItemViewType();
        final LeagueModel leagueModel = leagueModelArrayList.get(position);
        if (getViewType == ITEM_VIEW) {
            ((FragmentAllMatches.SelectLeagueViewHolder) holder).updateUI(leagueModel);

            ((FragmentAllMatches.SelectLeagueViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openClub = new Intent(ctx, LeagueInfoActivity.class);
                    openClub.putExtra("seasonId", leagueModel.getCurrent_season_id());
                    openClub.putExtra("leagueName", leagueModel.getName());
                    openClub.putExtra("league_id", leagueModel.getLeague_id());
                    activity.startActivity(openClub);

                }
            });

        } else if (getViewType == ITEM_LOADING) {


            if (isLoading) {

                ((SelectLeagueAdapter.ProgressViewHolder) holder).progressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.spinnerLoad),
                        android.graphics.PorterDuff.Mode.MULTIPLY);
                ((SelectLeagueAdapter.ProgressViewHolder) holder).progressBar.setVisibility(
                        View.VISIBLE);
                ((SelectLeagueAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            } else if (!isLoading){
                ((SelectLeagueAdapter.ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return leagueModelArrayList.size();
    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}
