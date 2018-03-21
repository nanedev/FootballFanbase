package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.AddPhotoOrVideo;
import com.malikbisic.sportapp.activity.firebase.MainPage;

import java.util.ArrayList;

/**
 * Created by malikbisic on 20/03/2018.
 */

public class SelectImagePostAdapter extends RecyclerView.Adapter<SelectImagePostAdapter.SelectImagePostViewHolder> {
    private Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    String username, profileImage, country, clubHeader;

    public SelectImagePostAdapter(Activity _activity, ArrayList<String> _filePaths, String username, String profileImage, String country, String clubHeader) {
        this._activity = _activity;
        this._filePaths = _filePaths;
        this.username = username;
        this.profileImage = profileImage;
        this.country = country;
        this.clubHeader = clubHeader;
    }

    @Override
    public SelectImagePostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_image_post_item, parent, false);
        return new SelectImagePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectImagePostViewHolder holder, final int position) {
        String image = _filePaths.get(position);
        Glide.with(_activity)
                .load(image)
                .override(250, 250)
                .centerCrop()

                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddPhotoOrVideo = new Intent(_activity, AddPhotoOrVideo.class);
                goToAddPhotoOrVideo.putExtra("imageURI", _filePaths.get(position));
                goToAddPhotoOrVideo.putExtra("username", username);
                goToAddPhotoOrVideo.putExtra("profileImage", profileImage);
                goToAddPhotoOrVideo.putExtra("country", country);
                goToAddPhotoOrVideo.putExtra("clubheader", clubHeader);
                MainPage.photoSelected = true;
                _activity.startActivity(goToAddPhotoOrVideo);

            }
        });

    }

    @Override
    public int getItemCount() {
        return _filePaths.size();
    }

    public static class SelectImagePostViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public SelectImagePostViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image_post);
        }
    }

}