package com.malikbisic.sportapp.viewHolder.api;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.FixturesLeagueModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * Created by korisnik on 25/12/2017.
 */

public class FixtureLeagueViewHolder extends RecyclerView.ViewHolder {
    TextView leagueName;
    TextView timeStart;
    TextView localTeamNameTXT;
    TextView visitorTeamNameTXT;
    ImageView localTeamLogo;
    ImageView visitorTeamLogo;
    TextView league;

    public FixtureLeagueViewHolder(View itemView) {
        super(itemView);

        timeStart = (TextView) itemView.findViewById(R.id.timeStartMatch_league);
        localTeamNameTXT = (TextView) itemView.findViewById(R.id.localTeamName_league);
        visitorTeamNameTXT = (TextView) itemView.findViewById(R.id.visitorTeamName_league);
        localTeamLogo = (ImageView) itemView.findViewById(R.id.localTeamLogo_league);
        visitorTeamLogo = (ImageView) itemView.findViewById(R.id.visitorTeamLogo_league);
        league = (TextView) itemView.findViewById(R.id.league_league);
    }

    public void updateUI(FixturesLeagueModel model) {
        String status = model.getStatus();
        String leagueName = model.getLeagueName();

        localTeamNameTXT.setText(model.getLocalTeamName());
        visitorTeamNameTXT.setText(model.getVisitorTeamName());
        Picasso.with(localTeamLogo.getContext()).load(model.getLocalTeamLogo()).into(localTeamLogo);
        Picasso.with(visitorTeamLogo.getContext()).load(model.getVisitorTeamLogo()).into(visitorTeamLogo);



        if (leagueName.equals("")){
            league.setVisibility(View.GONE);
            league.setText("");

        } else {
            league.setVisibility(View.VISIBLE);
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy");
            String finalDate = timeFormat.format(model.getDate());
            league.setText(finalDate);
        }


        if (status.equals("FT") || status.equals("HT")) {
            timeStart.setText(model.getScore());
        } else if (status.equals("NS")) {
            timeStart.setText(model.getTimeStart().substring(0, 5));
        }

    }

}
