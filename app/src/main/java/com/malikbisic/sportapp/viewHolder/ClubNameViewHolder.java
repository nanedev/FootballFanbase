package com.malikbisic.sportapp.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.UserChatGroup;
import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 03/09/2017.
 */

public class ClubNameViewHolder extends GroupViewHolder {

    View view;
    TextView clubName2;
    CircleImageView clubLogoImg;
    TextView onlineTexview;
    FirebaseDatabase mDatabase;

    public ClubNameViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        clubName2 = (TextView) view.findViewById(R.id.clubName_Users);
        clubLogoImg = (CircleImageView) view.findViewById(R.id.parent_club_logo);
        onlineTexview = (TextView) view.findViewById(R.id.parent_online_text);
    }

    public void setClubTitle(ExpandableGroup club) {
        if (club instanceof UserChatGroup) {
            clubName2.setText(club.getTitle());

            Picasso.with(itemView.getContext()).load(((UserChatGroup) club).getClubLogo()).into(clubLogoImg);
            onlineTexview.setText(((UserChatGroup) club).getOnline());
        }

    }
}
