package com.malikbisic.sportapp.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.PlayerInfoActivity;
import com.malikbisic.sportapp.model.TeamModel;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 24.10.2017.
 */

public class TeamSquadViewHolder  extends RecyclerView.ViewHolder{
 TextView teamPositionTextview;
 CircleImageView playerImage;
 TextView playerNameTextview;
 TextView playerFromTextview;
 TextView shirtNumberPlayer;
 TextView positionNameTextview;
 Context context;





    public TeamSquadViewHolder(View itemView) {
        super(itemView);

positionNameTextview = (TextView) itemView.findViewById(R.id.team_position);
playerImage = (CircleImageView) itemView.findViewById(R.id.player_image);
playerNameTextview = (TextView) itemView.findViewById(R.id.player_name);
playerFromTextview = (TextView) itemView.findViewById(R.id.from_country);
shirtNumberPlayer = (TextView) itemView.findViewById(R.id.shirt_number);
context = itemView.getContext();




    }


    public void updateUI (TeamModel team, Context context) {


        Picasso.with(context).load(team.getPlayerImage()).into(playerImage);
        playerNameTextview.setText(team.getCommonName());
        playerFromTextview.setText(team.getNationality());
        shirtNumberPlayer.setText(String.valueOf(team.getNumberId()));
        positionNameTextview.setText(team.getPositionName());
    }
}
