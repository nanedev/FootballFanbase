package com.malikbisic.sportapp.viewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.Transfers;

/**
 * Created by Nane on 9.11.2017.
 */

public class TransfersViewHolder extends RecyclerView.ViewHolder {

    TextView from_team;
    TextView to_team;
    TextView date_textview;
    TextView amount_textview;

    public TransfersViewHolder(View itemView) {
        super(itemView);

        from_team = (TextView) itemView.findViewById(R.id.from_team);
        to_team = (TextView) itemView.findViewById(R.id.to_team);
        date_textview = (TextView) itemView.findViewById(R.id.date_transfer);
        amount_textview = (TextView) itemView.findViewById(R.id.amount);




    }


    public void updateUI (Transfers transfers, Context context) {

from_team.setText(String.valueOf(transfers.getFromTeamName()));
     to_team.setText(String.valueOf(transfers.getToTeamName()));
    }
}
