package com.malikbisic.sportapp.viewHolder.firebase;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.PlayerModel;
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


}
