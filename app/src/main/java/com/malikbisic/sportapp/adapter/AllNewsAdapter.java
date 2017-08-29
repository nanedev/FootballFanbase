package com.malikbisic.sportapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.FragmentAllNews;
import com.malikbisic.sportapp.model.AllNewsModel;

import java.util.ArrayList;

/**
 * Created by Nane on 27.8.2017.
 */

public class AllNewsAdapter extends RecyclerView.Adapter<FragmentAllNews.AllNewsViewHolder> {

    private ArrayList<AllNewsModel> allNewsList;
    Context context;

    public AllNewsAdapter(ArrayList<AllNewsModel> allNewsList, Context context) {
        this.allNewsList = allNewsList;
        this.context = context;
    }

    @Override
    public FragmentAllNews.AllNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_news_row,parent,false);
        return new FragmentAllNews.AllNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FragmentAllNews.AllNewsViewHolder holder, int position) {
    AllNewsModel allNewsModel = allNewsList.get(position);
       holder.setHeadline(allNewsModel.getHeadline());
        holder.setThumbnail(context,allNewsModel.getThumbnail());
holder.setBodyText(allNewsModel.getBodyText());
    }

    @Override
    public int getItemCount() {
        return allNewsList.size();
    }
}
