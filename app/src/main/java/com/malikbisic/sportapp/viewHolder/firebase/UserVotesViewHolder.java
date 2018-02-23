package com.malikbisic.sportapp.viewHolder.firebase;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.model.firebase.UserVoteModel;
import com.malikbisic.sportapp.model.firebase.UsersModel;

import org.joda.time.DateTime;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 20.2.2018.
 */

public class UserVotesViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView userProfileImage;
    public TextView username;
    public CircleImageView playerImage;
    public TextView playerName;
    public TextView points;

    public UserVotesViewHolder(View itemView) {
        super(itemView);

        userProfileImage = (CircleImageView) itemView.findViewById(R.id.userProfileImage);
        username = (TextView) itemView.findViewById(R.id.userusername);
        playerImage = (CircleImageView) itemView.findViewById(R.id.playerImage);
        playerName = (TextView) itemView.findViewById(R.id.playerName);
        points = (TextView) itemView.findViewById(R.id.pointsTekst);

    }

    public void updateUI(UserVoteModel model){
        Glide.with(userProfileImage.getContext()).load(model.getUserProfileImage()).into(userProfileImage);
        username.setText(model.getUsername());
        Glide.with(playerImage.getContext()).load(model.getPlayerImage()).into(playerImage);
        playerName.setText(model.getPlayerName());
        points.setText(model.getPoints());
    }

}
