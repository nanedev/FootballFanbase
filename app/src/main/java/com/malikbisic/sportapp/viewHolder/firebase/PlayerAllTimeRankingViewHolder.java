package com.malikbisic.sportapp.viewHolder.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.PlayerModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 28.1.2018.
 */

public class PlayerAllTimeRankingViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView playerImageRankingMonth;
    public TextView playerPositionRankingMonth;
    public TextView playerPointsRankingMonth;
    public TextView numberVotesRankingMonth;
    public TextView playerName;
    public RelativeLayout seeVotes;

    int number;

    public PlayerAllTimeRankingViewHolder(View itemView) {

        super(itemView);

        playerImageRankingMonth = (CircleImageView) itemView.findViewById(R.id.playerRankingImage);
        playerPositionRankingMonth = (TextView) itemView.findViewById(R.id.playerRankingposition);
        playerPointsRankingMonth = (TextView) itemView.findViewById(R.id.playerRankingPints);
        numberVotesRankingMonth = (TextView) itemView.findViewById(R.id.playerRAnkingFansVotedNmbr);
        playerName = (TextView) itemView.findViewById(R.id.playerRankingName);
        seeVotes = (RelativeLayout) itemView.findViewById(R.id.nekilayout31);


    }


    public void setNumbervotes(final PlayerModel model) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PlayerPoints").document("AllTime").collection("player-id").document(model.getPlayerID()).collection("usersVote").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                number = 0;
                for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()) {
                    number++;
                    numberVotesRankingMonth.setText(String.valueOf(number));
                }
            }
        });



    }
}
