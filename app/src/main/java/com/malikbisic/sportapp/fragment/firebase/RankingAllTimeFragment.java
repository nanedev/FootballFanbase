package com.malikbisic.sportapp.fragment.firebase;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.FanbaseFanClubAdapter;
import com.malikbisic.sportapp.adapter.firebase.PlayersRankingAllTimeAdapter;
import com.malikbisic.sportapp.adapter.firebase.PlayersRankingMonthAdapter;
import com.malikbisic.sportapp.model.api.ClubTable;
import com.malikbisic.sportapp.model.api.PlayerModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nane on 26.1.2018.
 */

public class RankingAllTimeFragment extends Fragment {
    Toolbar likeToolbar;
    Intent myIntent;
    String openActivity = "";
    String myUid;
    Handler handler;

    int clubPos = 0;
    RecyclerView recFanClub;
    FirebaseFirestore db;
    com.google.firebase.firestore.Query clubReference;
    FanbaseFanClubAdapter clubAdapter;
    List<ClubTable> listClub;




    RecyclerView playerRankingRecyclerView;
  PlayersRankingAllTimeAdapter adapter;
    ArrayList<PlayerModel> list;
    String playerID;
    int pos = 1;

    RelativeLayout playersLayout;
    RelativeLayout clubLayout;
    TextView playerText;
    TextView clubText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.ranking_all_time_fragment,container,false);
playerRankingRecyclerView = (RecyclerView) view.findViewById(R.id.alltimeplayers);
        list = new ArrayList<>();
        playerRankingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlayersRankingAllTimeAdapter(list,getActivity());




        playerRankingRecyclerView.setAdapter(adapter);


        playersLayout = (RelativeLayout) view.findViewById(R.id.playersRankinglayoutalltime);
        playerText = (TextView) view.findViewById(R.id.playersalltimetext);


        playersLayout.setActivated(true);
        playerText.setTextColor(Color.parseColor("#000000"));

        recFanClub = (RecyclerView) view.findViewById(R.id.alltimeclubs);
        recFanClub.setLayoutManager(new LinearLayoutManager(getActivity()));
        recFanClub.setHasFixedSize(false);
        clubLayout = (RelativeLayout) view.findViewById(R.id.clubrankingalltimelayout);
        clubText = (TextView) view.findViewById(R.id.cluballtimetext);
        db = FirebaseFirestore.getInstance();
        listClub = new ArrayList<>();
        clubAdapter = new FanbaseFanClubAdapter(listClub, getActivity(), getContext());

        recFanClub.setLayoutManager(new LinearLayoutManager(getActivity()));
        recFanClub.setAdapter(clubAdapter);
        clubReference = db.collection("ClubTable");

        clubLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             playerRankingRecyclerView.setVisibility(View.GONE);
             clubLayout.setActivated(true);
             clubText.setTextColor(Color.parseColor("#000000"));
                playersLayout.setActivated(false);
                playerText.setTextColor(Color.parseColor("#ffffff"));
                recFanClub.setVisibility(View.VISIBLE);
                playerRankingRecyclerView.setVisibility(View.GONE);
            }
        });

        playersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerRankingRecyclerView.setVisibility(View.VISIBLE);
                clubLayout.setActivated(false);
                clubText.setTextColor(Color.parseColor("#ffffff"));
                playersLayout.setActivated(true);
                playerText.setTextColor(Color.parseColor("#000000"));
                playerRankingRecyclerView.setVisibility(View.VISIBLE);
                recFanClub.setVisibility(View.GONE);
            }
        });

        FirebaseFirestore.getInstance().collection("PlayerPoints").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                    Log.i("proba",documentSnapshot.getId());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final Query documentReference = db.collection("PlayerPoints").document(documentSnapshot.getId()).collection("player-id").orderBy("playerPoints", Query.Direction.DESCENDING);
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
        });


        final com.google.firebase.firestore.Query queryClub = clubReference.orderBy("numberClubFan", com.google.firebase.firestore.Query.Direction.DESCENDING);
        queryClub.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                    if (snapshot.exists()){
                        ClubTable model = snapshot.toObject(ClubTable.class);
                        listClub.add(model);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });




        return view;
    }
}
