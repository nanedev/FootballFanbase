package com.malikbisic.sportapp.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.AboutFootballClub;
import com.malikbisic.sportapp.model.TableModel;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 20.10.2017.
 */

public class TableViewHolder extends RecyclerView.ViewHolder {
    TextView positionTextview;
    CircleImageView logoClubImage;
    TextView teamNameTextview;
    TextView playedTextview;
    TextView goalDifTextview;
    TextView pointsTextview;
    String teamId;
    RelativeLayout posLay;
View view;
String result;

    public TableViewHolder(final View itemView) {
        super(itemView);

        positionTextview = (TextView) itemView.findViewById(R.id.position_number);
        logoClubImage = (CircleImageView) itemView.findViewById(R.id.club_logo_league_info);
        playedTextview = (TextView) itemView.findViewById(R.id.played_row);
        goalDifTextview = (TextView) itemView.findViewById(R.id.goal_difference_row);
        pointsTextview = (TextView) itemView.findViewById(R.id.points_row);
        teamNameTextview = (TextView) itemView.findViewById(R.id.club_name_league_info);
        posLay = (RelativeLayout) itemView.findViewById(R.id.positionLAyout);

    }

    public void updateUI (TableModel table, Context context){
        positionTextview.setText(String.valueOf(table.getPosition() + "."));
        teamNameTextview.setText(table.getTeamName());
        playedTextview.setText(String.valueOf(table.getPlayed()));
        goalDifTextview.setText(String.valueOf(table.getGoalScored() + ":" + table.getGoalConcided()));
        pointsTextview.setText(String.valueOf(table.getPoints()));
        Picasso.with(context).load(table.getClubLogo()).into(logoClubImage);
        teamId = String.valueOf(table.getTeamId());
        String example = table.getResult();
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(example);
        while(m.find()) {
            System.out.println(m.group(1));

            if (m.group(1).equals("Championship Group")){

                posLay.setBackgroundColor(Color.parseColor("#0d47a1"));
                positionTextview.setTextColor(Color.parseColor("#ffffff"));
            }
            if (m.group(1).equals("Relegation Group")){
                posLay.setBackgroundColor(Color.parseColor("#b71c1c"));
                positionTextview.setTextColor(Color.parseColor("#ffffff"));
            }
        }






    }
}
