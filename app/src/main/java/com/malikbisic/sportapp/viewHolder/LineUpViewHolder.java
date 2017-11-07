package com.malikbisic.sportapp.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.LineUpModel;

/**
 * Created by korisnik on 04/11/2017.
 */

public class LineUpViewHolder extends RecyclerView.ViewHolder {

    TextView number;
    TextView name;

    public LineUpViewHolder(View itemView) {
        super(itemView);

        number = (TextView) itemView.findViewById(R.id.numberKits);
        name = (TextView) itemView.findViewById(R.id.playerName);
    }

    public void updateUI(LineUpModel model){
        number.setText("" + model.getNumber());
        name.setText(model.getName());
    }
}
