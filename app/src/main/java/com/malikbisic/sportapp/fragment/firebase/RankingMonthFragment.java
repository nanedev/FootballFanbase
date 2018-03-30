package com.malikbisic.sportapp.fragment.firebase;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
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
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.activity.api.SearchableCountry;
import com.malikbisic.sportapp.activity.firebase.MainPage;
import com.malikbisic.sportapp.adapter.firebase.MainPageAdapter;
import com.malikbisic.sportapp.adapter.firebase.PlayersRankingMonthAdapter;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.firebase.Post;
import com.malikbisic.sportapp.model.firebase.UsersModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Nane on 26.1.2018.
 */

public class RankingMonthFragment extends Fragment {

    RelativeLayout playerRankLayout;

    TextView playerRankText1;
    TextView playerRankText2;



    String playerID;
    int pos = 1;
    int numbeR;
    RecyclerView playerRankingRecyclerView;
    PlayersRankingMonthAdapter adapter;
    ArrayList<PlayerModel> list;


    MainPageAdapter topPostsAdapter;
    ArrayList <Post> postsList;
    RecyclerView topPostsRec;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ranking_month_fragment, container, false);
        playerRankLayout = (RelativeLayout) view.findViewById(R.id.rankingsLayoutplayer);
        Intent closeAPP = new Intent(getContext(), StopAppServices.class);
        getActivity().startService(closeAPP);

        playerRankText1 = (TextView) view.findViewById(R.id.playersText);
        playerRankText2 = (TextView) view.findViewById(R.id.playerRankingText);

        playerRankingRecyclerView = (RecyclerView) view.findViewById(R.id.playersRankingrecyclerview);
        list = new ArrayList<>();

        playerRankingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlayersRankingMonthAdapter(list, getActivity());

        playerRankingRecyclerView.setAdapter(adapter);


        playerRankLayout.setActivated(true);
        playerRankText1.setTextColor(Color.parseColor("#33691e"));
        playerRankText2.setTextColor(Color.parseColor("#33691e"));

        postsList = new ArrayList<>();
        topPostsRec = (RecyclerView) view.findViewById(R.id.topPostsRecyclerview);
        topPostsAdapter = new MainPageAdapter(postsList, getApplicationContext(), getActivity(), topPostsRec, MainPage.postKey);



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
