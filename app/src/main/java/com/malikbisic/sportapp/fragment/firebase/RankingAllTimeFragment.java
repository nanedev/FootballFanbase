package com.malikbisic.sportapp.fragment.firebase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.PlayersRankingAllTimeAdapter;
import com.malikbisic.sportapp.adapter.firebase.PlayersRankingMonthAdapter;
import com.malikbisic.sportapp.model.api.PlayerModel;

import java.util.ArrayList;

/**
 * Created by Nane on 26.1.2018.
 */

public class RankingAllTimeFragment extends Fragment {
    RecyclerView playerRankingRecyclerView;
  PlayersRankingAllTimeAdapter adapter;
    ArrayList<PlayerModel> list;
    String playerID;
    int pos = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.ranking_all_time_fragment,container,false);
playerRankingRecyclerView = (RecyclerView) view.findViewById(R.id.alltimeplayers);
        list = new ArrayList<>();
        playerRankingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlayersRankingAllTimeAdapter(list,getActivity());

        playerRankingRecyclerView.setAdapter(adapter);


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


       return view;
    }
}
