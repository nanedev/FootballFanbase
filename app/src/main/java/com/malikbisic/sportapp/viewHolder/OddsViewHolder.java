package com.malikbisic.sportapp.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.OddsModel;

/**
 * Created by korisnik on 29/10/2017.
 */

public class OddsViewHolder extends RecyclerView.ViewHolder {

    TextView companyName;
    TextView value1;
    TextView valueX;
    TextView value2;

    public OddsViewHolder(View itemView) {
        super(itemView);

        companyName = (TextView) itemView.findViewById(R.id.companyName);
        value1 = (TextView) itemView.findViewById(R.id.value1);
        valueX = (TextView) itemView.findViewById(R.id.xValue);
        value2 = (TextView) itemView.findViewById(R.id.value2);
    }

    public void updateUI(OddsModel model){
        companyName.setText(model.getCompanyName());
        value1.setText(model.getValue1());
        valueX.setText(model.getValueX());
        value2.setText(model.getValue2());
    }
}
