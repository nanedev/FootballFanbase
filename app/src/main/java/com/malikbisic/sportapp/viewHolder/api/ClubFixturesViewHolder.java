package com.malikbisic.sportapp.viewHolder.api;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.ClubFixturesModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by korisnik on 09/11/2017.
 */

public class ClubFixturesViewHolder extends RecyclerView.ViewHolder {

    TextView leagueName;
    TextView timeStart;
    TextView localTeamNameTXT;
    TextView visitorTeamNameTXT;
    ImageView localTeamLogo;
    ImageView visitorTeamLogo;
    TextView league;
    TextView dateStart;

    public ClubFixturesViewHolder(View itemView) {
        super(itemView);

        timeStart = (TextView) itemView.findViewById(R.id.gameTimeClub);
        localTeamNameTXT = (TextView) itemView.findViewById(R.id.localTeamId);
        visitorTeamNameTXT = (TextView) itemView.findViewById(R.id.visitorTeamId);
        localTeamLogo = (ImageView) itemView.findViewById(R.id.localLogoClub);
        visitorTeamLogo = (ImageView) itemView.findViewById(R.id.visitorLogoClub);
        league = (TextView) itemView.findViewById(R.id.leagueClub);
    }

    public void updateUI(ClubFixturesModel model) {
        String status = model.getStatus();
        String leagueName = model.getLeagueName();

        localTeamNameTXT.setText(model.getLocalTeamName());
        visitorTeamNameTXT.setText(model.getVisitorTeamName());
        Picasso.with(localTeamLogo.getContext()).load(model.getLocalTeamLogo()).into(localTeamLogo);
        Picasso.with(visitorTeamLogo.getContext()).load(model.getVisitorTeamLogo()).into(visitorTeamLogo);


       // league.setText(leagueName);

        timeStart.setText(model.getTimeStart().substring(0, 5));

        String mytime= model.getDateStart();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(mytime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy");
        String finalDate = timeFormat.format(myDate);
        league.setText(finalDate);


    }

}
