package com.malikbisic.sportapp.viewHolder.firebase;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.malikbisic.sportapp.R;

/**
 * Created by malikbisic on 14/02/2018.
 */

public class GalleryMessageViewHolder extends RecyclerView.ViewHolder {

    public ImageView galleryImage;

    public GalleryMessageViewHolder(View itemView) {
        super(itemView);

        galleryImage = (ImageView) itemView.findViewById(R.id.galleryImage);
    }
}
