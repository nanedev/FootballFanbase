package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.CommentaryMatchModel;
import com.malikbisic.sportapp.viewHolder.CommentsMatchViewHolder;

import java.util.ArrayList;

/**
 * Created by korisnik on 30/12/2017.
 */

public class CommentsMatchAdapter extends RecyclerView.Adapter<CommentsMatchViewHolder> {

    ArrayList<CommentaryMatchModel> commentaryMatchModelArrayList;
    Activity activity;

    public CommentsMatchAdapter(ArrayList<CommentaryMatchModel> commentaryMatchModelArrayList, Activity activity) {
        this.commentaryMatchModelArrayList = commentaryMatchModelArrayList;
        this.activity = activity;
    }

    @Override
    public CommentsMatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentary_item, parent, false);
        return new CommentsMatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentsMatchViewHolder holder, int position) {
        CommentaryMatchModel model = commentaryMatchModelArrayList.get(position);
        holder.updateUI(model, activity);
    }

    @Override
    public int getItemCount() {
        return commentaryMatchModelArrayList.size();
    }
}
