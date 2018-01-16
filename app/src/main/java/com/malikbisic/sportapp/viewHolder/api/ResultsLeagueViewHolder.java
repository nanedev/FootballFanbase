package com.malikbisic.sportapp.viewHolder.api;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.ResultsLeagueModel;
import com.squareup.picasso.Picasso;

/**
 * Created by malikbisic on 16/01/2018.
 */

public class ResultsLeagueViewHolder extends ViewHolder {

    TextView leagueName;
    TextView timeStart;
    TextView localTeamNameTXT;
    TextView visitorTeamNameTXT;
    ImageView localTeamLogo;
    ImageView visitorTeamLogo;
    public TextView league;

    public ResultsLeagueViewHolder(View itemView) {
        super(itemView);

        timeStart = (TextView) itemView.findViewById(R.id.gameTimeRes);
        localTeamNameTXT = (TextView) itemView.findViewById(R.id.localTeamId);
        visitorTeamNameTXT = (TextView) itemView.findViewById(R.id.visitorTeamId);
        localTeamLogo = (ImageView) itemView.findViewById(R.id.localLogoRes);
        visitorTeamLogo = (ImageView) itemView.findViewById(R.id.visitorLogoRes);
        league = (TextView) itemView.findViewById(R.id.leagueRes);
    }

    public void updateUI(ResultsLeagueModel model) {
        String status = model.getStatus();
        String leagueName = model.getLeagueName();

        localTeamNameTXT.setText(model.getLocalTeamName());
        visitorTeamNameTXT.setText(model.getVisitorTeamName());
        Picasso.with(localTeamLogo.getContext()).load(model.getLocalTeamLogo()).into(localTeamLogo);
        Picasso.with(visitorTeamLogo.getContext()).load(model.getVisitorTeamLogo()).into(visitorTeamLogo);


        if (model.getLeagueName().equals("isti datum")){
            league.setVisibility(View.GONE);
        } else if (!model.getLeagueName().equals("isti datum")){
            league.setVisibility(View.VISIBLE);
            league.setText(model.getLeagueName());
        }


        if (status.equals("FT") || status.equals("HT")) {
            timeStart.setText(model.getScore());
        } else if (status.equals("NS")) {
            timeStart.setText(model.getTimeStart().substring(0, 5));
        }

    }
}
