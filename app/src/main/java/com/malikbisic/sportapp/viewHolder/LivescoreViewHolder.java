package com.malikbisic.sportapp.viewHolder;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.LivescoreModel;
import com.squareup.picasso.Picasso;

/**
 * Created by korisnik on 21/10/2017.
 */

public class LivescoreViewHolder extends RecyclerView.ViewHolder {

    TextView localTeam;
    TextView visitorTeam;
    ImageView localTeamLogo;
    ImageView visitorTeamLogo;
    TextView score;
    TextView league;

    public LivescoreViewHolder(View itemView) {
        super(itemView);

        localTeam = (TextView) itemView.findViewById(R.id.localTeam);
        visitorTeam = (TextView) itemView.findViewById(R.id.visitorTeam);
        localTeamLogo = (ImageView) itemView.findViewById(R.id.localTeamLogo);
        visitorTeamLogo = (ImageView) itemView.findViewById(R.id.visitorTeamLogo);
        score = (TextView) itemView.findViewById(R.id.livescore_txt);
        league = (TextView) itemView.findViewById(R.id.league);
    }

    @SuppressLint("ResourceAsColor")
    public void updateUI(LivescoreModel model){
        String statusTXT = model.getStatus();
        String scoreTXT = model.getScore();
        String leagueName = model.getLeagueName();
        String time = model.getTimeStart();
        String correctTime =  time.substring(0, 5);
        localTeam.setText(model.getLocalTeam());
        visitorTeam.setText(model.getVisitorTeam());
        Picasso.with(localTeamLogo.getContext()).load(model.getLocalTeamLogo()).into(localTeamLogo);
        Picasso.with(visitorTeamLogo.getContext()).load(model.getVisitorTeamLogo()).into(visitorTeamLogo);

        if (leagueName.equals("")){
            league.setVisibility(View.GONE);
            league.setText("");

        } else {
            league.setVisibility(View.VISIBLE);
            league.setText(leagueName);
        }

        if (statusTXT.equals("NS")){
            score.setText(correctTime);
        } else if (statusTXT.equals("HT") || statusTXT.equals("LIVE")) {
            score.setText(model.getScore());
            score.setBackgroundColor(R.color.jumbo);
        } else if (statusTXT.equals("FT")){
            score.setText(model.getScore());
            score.setBackgroundColor(R.color.oil);
        }
    }
}
