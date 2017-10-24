package com.malikbisic.sportapp.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.AllFixturesModel;
import com.squareup.picasso.Picasso;

/**
 * Created by korisnik on 23/10/2017.
 */

public class AllFixturesViewHolder extends RecyclerView.ViewHolder {

    TextView leagueName;
    TextView timeStart;
    TextView localTeamNameTXT;
    TextView visitorTeamNameTXT;
    ImageView localTeamLogo;
    ImageView visitorTeamLogo;
    TextView league;

    public AllFixturesViewHolder(View itemView) {
        super(itemView);

        timeStart = (TextView) itemView.findViewById(R.id.timeStartMatch);
        localTeamNameTXT = (TextView) itemView.findViewById(R.id.localTeamName);
        visitorTeamNameTXT = (TextView) itemView.findViewById(R.id.visitorTeamName);
        localTeamLogo = (ImageView) itemView.findViewById(R.id.localTeamLogo);
        visitorTeamLogo = (ImageView) itemView.findViewById(R.id.visitorTeamLogo);
        league = (TextView) itemView.findViewById(R.id.league);
    }

    public void updateUI(AllFixturesModel model) {
        String status = model.getStatus();
        String leagueName = model.getLeagueName();

        localTeamNameTXT.setText(model.getLocalTeamName());
        visitorTeamNameTXT.setText(model.getVisitorTeamName());
        Picasso.with(localTeamLogo.getContext()).load(model.getLocalTeamLogo()).into(localTeamLogo);
        Picasso.with(visitorTeamLogo.getContext()).load(model.getVisitorTeamLogo()).into(visitorTeamLogo);

        String dtLabel = league.getText().toString();

        if (leagueName.equals("")){
            league.setVisibility(View.GONE);
            league.setText("");

        } else {
            league.setVisibility(View.VISIBLE);
            league.setText(leagueName);
        }


        if (status.equals("FT") || status.equals("HT")) {
            timeStart.setText(model.getScore());
        } else if (status.equals("NS")) {
            timeStart.setText(model.getTimeStart().substring(0, 5));
        }

    }

}
