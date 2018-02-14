package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.ChatMessageActivity;
import com.malikbisic.sportapp.model.firebase.GalleryImageModel;
import com.malikbisic.sportapp.utils.RoundedTransformation;
import com.malikbisic.sportapp.viewHolder.firebase.GalleryMessageViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by malikbisic on 14/02/2018.
 */

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryMessageViewHolder> {

    ArrayList<String> galleryImageModelArrayList;
    Activity activity;

    public GalleryImageAdapter(ArrayList<String> galleryImageModelArrayList, Activity activity) {
        this.galleryImageModelArrayList = galleryImageModelArrayList;
        this.activity = activity;
    }

    @Override
    public GalleryMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent,false);
        return new GalleryMessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GalleryMessageViewHolder holder, int position) {
        Picasso.with(activity).setIndicatorsEnabled(false);
        Picasso.with(activity).load(galleryImageModelArrayList.get(position)).into(holder.galleryImage);

    }

    @Override
    public int getItemCount() {
        return galleryImageModelArrayList.size();
    }
}
