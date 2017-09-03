package com.malikbisic.sportapp.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

/**
 * Created by korisnik on 03/09/2017.
 */

public class ClubNameViewHolder extends GroupViewHolder {

    View view;
    TextView clubName2;

    public ClubNameViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        clubName2 = (TextView) view.findViewById(R.id.clubName_Users);
    }

    public void setClubName(String clubName){
        clubName2.setText(clubName);
    }
}
