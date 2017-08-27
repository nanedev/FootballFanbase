package com.malikbisic.sportapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.FragmentMyClubNews;
import com.malikbisic.sportapp.model.MyClubNews;

import java.util.ArrayList;

/**
 * Created by korisnik on 27/08/2017.
 */

public class MyClubsNewsAdapter extends RecyclerView.Adapter<FragmentMyClubNews.MyClubNewsViewHolder> {

    public ArrayList<MyClubNews> myClubNewsesReports;
    Context ctx;

    public MyClubsNewsAdapter(ArrayList<MyClubNews> myClubNewsesReports, Context ctx) {
        this.myClubNewsesReports = myClubNewsesReports;
        this.ctx = ctx;
    }

    @Override
    public FragmentMyClubNews.MyClubNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myclubnews_row, parent, false);

        return new FragmentMyClubNews.MyClubNewsViewHolder(card_view);
    }

    @Override
    public void onBindViewHolder(FragmentMyClubNews.MyClubNewsViewHolder holder, int position) {
        MyClubNews model = myClubNewsesReports.get(position);
        holder.updateUI(model, ctx);

    }

    @Override
    public int getItemCount() {
        return myClubNewsesReports.size();
    }
}
