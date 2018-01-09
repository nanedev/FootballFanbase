package com.malikbisic.sportapp.model;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.application.Application;
import com.malikbisic.sportapp.model.api.PlayerModel;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by korisnik on 06/01/2018.
 */

public class FootballPlayer {

    private static final String STORAGE = "player";

    public static FootballPlayer get() {
        return new FootballPlayer();
    }

    private SharedPreferences storage;

    String playName;
    String playerImage;
    int id = 0;
    ArrayList<PlayerModel> list = new ArrayList<>();

    private FootballPlayer() {
        storage = Application.getInstance().getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
    }

    public List<PlayerModel> getData() {
        PlayerModel model = new PlayerModel();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Query documentReference = db.collection("PlayerPoints").orderBy("playerPoints", Query.Direction.DESCENDING);
        documentReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    if (documentSnapshot.exists()) {
                        id++;
                        playName = documentSnapshot.getString("playerName");
                        playerImage = documentSnapshot.getString("playerImage");
                       // list.add(new PlayerModel(id, playName, playerImage));

                    }
                }
            }
        });
        return list;
    }
}
