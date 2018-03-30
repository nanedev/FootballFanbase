package com.malikbisic.sportapp.fragment.firebase;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
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
import com.malikbisic.sportapp.adapter.firebase.MainPageAdapter;
import com.malikbisic.sportapp.adapter.firebase.PlayersRankingMonthAdapter;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.firebase.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Nane on 26.1.2018.
 */

public class RankingMonthFragment extends Fragment {

    RelativeLayout playerRankLayout;
    RelativeLayout topPostsLayout;

    TextView playerRankText1;
    TextView playerRankText2;
    TextView topPostText1;
    TextView topPostText2;


    String playerID;
    int pos = 1;
    int numbeR;
    RecyclerView playerRankingRecyclerView;
    RecyclerView topPostsRecView;
    PlayersRankingMonthAdapter adapter;
    MainPageAdapter postAdapter;
    ArrayList<PlayerModel> list;
    ArrayList<Post> postArrayList;

    int numberLikes;
    int numberDisliks;
    int totalLikes, totalDislikes, pointsTotal;

    FirebaseAuth mAuth;
    String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ranking_month_fragment, container, false);
        playerRankLayout = (RelativeLayout) view.findViewById(R.id.rankingsLayoutplayer);
        topPostsLayout = (RelativeLayout) view.findViewById(R.id.postsRanking);
        Intent closeAPP = new Intent(getContext(), StopAppServices.class);
        getActivity().startService(closeAPP);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }

        playerRankText1 = (TextView) view.findViewById(R.id.playersText);
        playerRankText2 = (TextView) view.findViewById(R.id.playerRankingText);
        topPostText1 = (TextView) view.findViewById(R.id.postRankingText);
        topPostText2 = (TextView) view.findViewById(R.id.postsText);

        playerRankingRecyclerView = (RecyclerView) view.findViewById(R.id.playersRankingrecyclerview);
        topPostsRecView = (RecyclerView) view.findViewById(R.id.topPostsRecylerview);
        list = new ArrayList<>();
        postArrayList = new ArrayList<>();

        playerRankingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        topPostsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new PlayersRankingMonthAdapter(list, getActivity());
        postAdapter = new MainPageAdapter(postArrayList, getContext(), getActivity(), topPostsRecView, null);

        playerRankingRecyclerView.setAdapter(adapter);
        topPostsRecView.setAdapter(postAdapter);


        playerRankLayout.setActivated(true);
        topPostsLayout.setActivated(false);
        playerRankText1.setTextColor(Color.parseColor("#000000"));
        playerRankText2.setTextColor(Color.parseColor("#000000"));
        playerRankingRecyclerView.setVisibility(View.VISIBLE);
        topPostsRecView.setVisibility(View.GONE);

        topPostText1.setTextColor(Color.parseColor("#ffffff"));
        topPostText2.setTextColor(Color.parseColor("#ffffff"));

        updateListPlayer();

        playerRankLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postArrayList.clear();
                playerRankLayout.setActivated(true);
                topPostsLayout.setActivated(false);
                playerRankText1.setTextColor(Color.parseColor("#000000"));
                playerRankText2.setTextColor(Color.parseColor("#000000"));
                playerRankingRecyclerView.setVisibility(View.VISIBLE);
                topPostsRecView.setVisibility(View.GONE);

                topPostText1.setTextColor(Color.parseColor("#ffffff"));
                topPostText2.setTextColor(Color.parseColor("#ffffff"));

                updateListPlayer();
            }
        });


        topPostsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                playerRankLayout.setActivated(false);
                topPostsLayout.setActivated(true);
                playerRankText1.setTextColor(Color.parseColor("#ffffff"));
                playerRankText2.setTextColor(Color.parseColor("#ffffff"));
                playerRankingRecyclerView.setVisibility(View.GONE);
                topPostsRecView.setVisibility(View.VISIBLE);

                topPostText1.setTextColor(Color.parseColor("#000000"));
                topPostText2.setTextColor(Color.parseColor("#000000"));

                topPostMonth();
            }
        });


        return view;
    }

    public void topPostMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -30);
        final Date d = c.getTime();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference dbPost = FirebaseFirestore.getInstance().collection("Posting");

        dbPost.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).endBefore(d).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (final DocumentSnapshot snapshotPost : task.getResult()) {
                    final String postID = snapshotPost.getId();

                    Log.i("postID", postID);

                    CollectionReference likeNumber = db.collection("Likes").document(postID).collection("like-id");
                    likeNumber.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<QuerySnapshot> task2) {
                            if (task2.getException() == null) {

                                for (final DocumentSnapshot snapshot1 : task2.getResult()) {

                                    db.collection("Posting").document(postID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {



                                            numberLikes = task2.getResult().size();


                                        }
                                    });


                                }


                                CollectionReference dislikeNumber = db.collection("Dislikes").document(postID).collection("dislike-id");
                                dislikeNumber.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task3) {

                                        if (task3.getException() == null) {

                                            numberDisliks = task3.getResult().getDocuments().size();

                                            pointsTotal = numberLikes - numberDisliks;

                                        }

                                        Post model = snapshotPost.toObject(Post.class).withId(postID).setTotalPosints(pointsTotal);
                                        postArrayList.add(model);

                                        Log.i("Like post: " + postID, String.valueOf(pointsTotal));

                                        Collections.sort(postArrayList, new Comparator<Post>() {
                                            public int compare(Post s1, Post s2) {
                                                // notice the cast to (Integer) to invoke compareTo
                                                return ((Integer) s1.getTotalPoints()).compareTo(s2.getTotalPoints());
                                            }
                                        });

                                        postAdapter.notifyDataSetChanged();
                                    }

                                });


                            }
                        }

                    });


                }

            }
        });


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
                        list.add(new PlayerModel(id, playName, playerImage, playerPoints, playerID));
                        pos++;
                        adapter.notifyDataSetChanged();

                    }


                }
            }
        });
    }
}
