package com.malikbisic.sportapp.viewHolder.api;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;

/**
 * Created by Nane on 5.1.2018.
 */

public class PlayersInProfileViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView pointPlayer;
    public Button voteButton;

    public PlayersInProfileViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image);
        pointPlayer = (TextView) itemView.findViewById(R.id.playerPoints);
        voteButton = (Button) itemView.findViewById(R.id.votebutton);
    }
}
