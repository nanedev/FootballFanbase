package com.malikbisic.sportapp.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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
    }

    public void updateUi(AllFixturesModel model){



    }
}
