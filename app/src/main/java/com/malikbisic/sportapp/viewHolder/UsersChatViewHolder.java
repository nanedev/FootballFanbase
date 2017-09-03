package com.malikbisic.sportapp.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.malikbisic.sportapp.R;
import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 03/09/2017.
 */

public class UsersChatViewHolder extends ChildViewHolder {

    View view;
    TextView usernameUser;
    ImageView flagUser;
    CircleImageView profileImageUser;

    public UsersChatViewHolder(View itemView) {
        super(itemView);

        view = itemView;
        usernameUser = (TextView) view.findViewById(R.id.usernameUsers);
        flagUser = (ImageView) view.findViewById(R.id.flagUsers);
        profileImageUser = (CircleImageView) view.findViewById(R.id.profileUsers);
    }

    public void setUsername (String username){
        usernameUser.setText(username);
    }

    public void setFlag(Context ctx, String flag){
        Picasso.with(ctx).load(flag).into(flagUser);
    }

    public void setProfileImage(Context ctx, String proifleImage){
        Picasso.with(ctx).load(proifleImage).into(profileImageUser);
    }
}
