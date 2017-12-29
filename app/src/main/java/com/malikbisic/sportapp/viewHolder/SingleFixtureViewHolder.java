package com.malikbisic.sportapp.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.AllFixturesModel;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 28.12.2017.
 */

public class SingleFixtureViewHolder extends RecyclerView.ViewHolder {
    CircleImageView localTeamClubLogo;
    CircleImageView visitorTeamClubLogo;
    TextView localTeamName;
    TextView visitorTeamName;
    TextView timeOfMatch;
    TextView minutes;
    TextView localTeamResult;
    TextView visitorTeamResult;
View viewResult;



    public SingleFixtureViewHolder(View itemView) {
        super(itemView);

        localTeamClubLogo = (CircleImageView) itemView.findViewById(R.id.localLogo);
        visitorTeamClubLogo = (CircleImageView) itemView.findViewById(R.id.visitorLogo);
        localTeamName = (TextView) itemView.findViewById(R.id.localTeamId);
        visitorTeamName = (TextView) itemView.findViewById(R.id.visitorTeamId);
        timeOfMatch = (TextView) itemView.findViewById(R.id.gameTime);
        minutes = (TextView) itemView.findViewById(R.id.singleFixtureMinutes);
        localTeamResult = (TextView) itemView.findViewById(R.id.localTeamResult);
        visitorTeamResult = (TextView) itemView.findViewById(R.id.visitorTeamResult);
        viewResult = (View) itemView.findViewById(R.id.viewforresult);
    }

    public void updateUi(AllFixturesModel model){
        localTeamName.setText(model.getLocalTeamName());
        visitorTeamName.setText(model.getVisitorTeamName());



        Glide.with(localTeamClubLogo.getContext()).load(model.getLocalTeamLogo()).into(localTeamClubLogo);
        Glide.with(visitorTeamClubLogo.getContext()).load(model.getVisitorTeamLogo()).into(visitorTeamClubLogo);

        String status = model.getStatus();

        if (status.equals("FT") || status.equals("HT")) {
            localTeamResult.setVisibility(View.VISIBLE);
            visitorTeamResult.setVisibility(View.VISIBLE);
            minutes.setVisibility(View.VISIBLE);
            timeOfMatch.setVisibility(View.INVISIBLE);
            viewResult.setVisibility(View.VISIBLE);

            minutes.setText("" + model.getMinutes());
            localTeamResult.setText(model.getScore().substring(0, 1));
            visitorTeamResult.setText(model.getScore().substring(3,5));
        } else if (status.equals("NS")) {
            localTeamResult.setVisibility(View.GONE);
            visitorTeamResult.setVisibility(View.GONE);
            minutes.setVisibility(View.VISIBLE);
            timeOfMatch.setVisibility(View.VISIBLE);

            minutes.setText("" + model.getMinutes());
            timeOfMatch.setText(model.getTimeStart().substring(0, 5));
        }
    }
}
