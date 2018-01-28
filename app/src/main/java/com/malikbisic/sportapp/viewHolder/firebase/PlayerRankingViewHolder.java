package com.malikbisic.sportapp.viewHolder.firebase;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.PlayerModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 27.1.2018.
 */

public class PlayerRankingViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView playerImageRankingMonth;
    public TextView playerPositionRankingMonth;
    public TextView playerPointsRankingMonth;
    public TextView numberVotesRankingMonth;
    public TextView playerName;

int number;

    public PlayerRankingViewHolder(View itemView) {

        super(itemView);

        playerImageRankingMonth = (CircleImageView) itemView.findViewById(R.id.playerRankingImage);
        playerPositionRankingMonth = (TextView) itemView.findViewById(R.id.playerRankingposition);
        playerPointsRankingMonth = (TextView) itemView.findViewById(R.id.playerRankingPints);
        numberVotesRankingMonth = (TextView) itemView.findViewById(R.id.playerRAnkingFansVotedNmbr);
        playerName = (TextView) itemView.findViewById(R.id.playerRankingName);


    }


    public void setNumbervotes(PlayerModel model) {
        DateFormat currentDateFormat = new SimpleDateFormat("MMMM");
        final Date currentDate = new Date();

        final String currentMonth = currentDateFormat.format(currentDate);
        FirebaseFirestore.getInstance().collection("PlayerPoints").document(currentMonth).collection("player-id").document(model.getPlayerID()).collection("usersVote").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                number = documentSnapshots.size();
                numberVotesRankingMonth.setText(String.valueOf(number));
            }
        });
    }

}
