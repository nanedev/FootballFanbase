package com.malikbisic.sportapp.fragment.firebase;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.PlayersRankingMonthAdapter;
import com.malikbisic.sportapp.model.api.PlayerModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nane on 26.1.2018.
 */

public class RankingMonthFragment extends Fragment {

    RelativeLayout playerRankLayout;
    RelativeLayout clubRankLayout;
    RelativeLayout fansRankLayout;
    TextView playerRankText1;
    TextView playerRankText2;
    TextView clubRankText1;
    TextView clubRankText2;
    TextView fansRankText1;
    TextView fansRankText2;
    View linija;
    String playerID;
    int pos = 1;
    int numbeR;
    RecyclerView playerRankingRecyclerView;
    PlayersRankingMonthAdapter adapter;
    ArrayList<PlayerModel> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ranking_month_fragment, container, false);
        playerRankLayout = (RelativeLayout) view.findViewById(R.id.rankingsLayoutplayer);
        clubRankLayout = (RelativeLayout) view.findViewById(R.id.clubrankinglayout);
        fansRankLayout = (RelativeLayout) view.findViewById(R.id.fansrankiniglayout);
        playerRankText1 = (TextView) view.findViewById(R.id.playersText);
        playerRankText2 = (TextView) view.findViewById(R.id.playerRankingText);

        playerRankingRecyclerView = (RecyclerView) view.findViewById(R.id.playersRankingrecyclerview);
        list = new ArrayList<>();

        playerRankingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlayersRankingMonthAdapter(list, getActivity());

        playerRankingRecyclerView.setAdapter(adapter);


        playerRankLayout.setActivated(true);
        clubRankLayout.setActivated(false);
        fansRankLayout.setActivated(false);
        playerRankText1.setTextColor(Color.parseColor("#000000"));
        playerRankText2.setTextColor(Color.parseColor("#000000"));

        updateListPlayer();

        return view;
    }


    public void updateListPlayer() {
        DateFormat currentDateFormat = new SimpleDateFormat("MMMM");
        final Date currentDate = new Date();

        final String currentMonth = currentDateFormat.format(currentDate);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Query documentReference = db.collection("PlayerPoints").document(currentMonth).collection("player-id").orderBy("playerPoints", Query.Direction.DESCENDING);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@Nullable Task<QuerySnapshot> task) {
                String playName;
                String playerImage;
                long playerPoints;

                int id = 0;
                for (final DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (documentSnapshot.exists()) {
                        id++;
                        playerID = documentSnapshot.getId();
                        playName = documentSnapshot.getString("playerName");
                        playerImage = documentSnapshot.getString("playerImage");
                        playerPoints = documentSnapshot.getLong("playerPoints");

                        list.add(new PlayerModel(id, playName,playerImage, playerPoints,  playerID));
                        pos++;
                        adapter.notifyDataSetChanged();
                    }


                }
            }
        });
    }
}
