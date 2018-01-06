package com.malikbisic.sportapp.viewHolder.api;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.malikbisic.sportapp.R;

/**
 * Created by Nane on 5.1.2018.
 */

public class PlayersInProfileViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;

    public PlayersInProfileViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image);
    }
}
