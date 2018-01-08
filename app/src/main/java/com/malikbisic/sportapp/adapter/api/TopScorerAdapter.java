package com.malikbisic.sportapp.adapter.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.api.TopScorerModel;
import com.malikbisic.sportapp.viewHolder.api.TopScorerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 02/01/2018.
 */

public class TopScorerAdapter extends RecyclerView.Adapter<TopScorerViewHolder> {

    ArrayList<TopScorerModel> topScorerModelArrayList;
    Activity activity;
    long myPointsVote;
    FirebaseAuth mAuth;

    public TopScorerAdapter(ArrayList<TopScorerModel> topScorerModelArrayList, Activity activity) {
        this.topScorerModelArrayList = topScorerModelArrayList;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public TopScorerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_scores_item, parent, false);

        return new TopScorerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopScorerViewHolder holder, int position) {
        final TopScorerModel model = topScorerModelArrayList.get(position);
        holder.updateUI(model);

        holder.vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                votePlayer(model);
            }
        });

    }

    public void votePlayer(final TopScorerModel model){

        AlertDialog.Builder playerVoteDialogBuilder = new AlertDialog.Builder(activity);
        View viewDialog = LayoutInflater.from(activity).inflate(R.layout.vote_player_dialog, null);
        playerVoteDialogBuilder.setView(viewDialog);

        TextView playerPoints = (TextView) viewDialog.findViewById(R.id.playerPointsVote);
        CircleImageView playerImage = (CircleImageView) viewDialog.findViewById(R.id.playerImageVote);
        TextView playerName = (TextView) viewDialog.findViewById(R.id.playerNameVote);
        final TextView myPoints = (TextView) viewDialog.findViewById(R.id.myPointsVote);
        final EditText enterPointsVote = (EditText) viewDialog.findViewById(R.id.enterPointsVote);

        playerPoints.setText("Player points: 50");
        Glide.with(playerImage.getContext()).load(model.getImagePlayer()).into(playerImage);
        playerName.setText(model.getName());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference myPointsGet = db.collection("Points").document(mAuth.getCurrentUser().getUid());
        myPointsGet.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                long totalNumber = task.getResult().getLong("prevMonthPoints.totalPoints");

                Log.i("totalNumber", String.valueOf(totalNumber));

                myPoints.setText("My points: " + totalNumber);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("err", e.getLocalizedMessage());
            }
        });

        playerVoteDialogBuilder.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enterPoint = enterPointsVote.getText().toString().trim();
                if (!TextUtils.isEmpty(enterPoint)) {
                    final long points = Long.parseLong(enterPoint);

                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference myPointsGet = db.collection("Points").document(mAuth.getCurrentUser().getUid());
                    myPointsGet.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            long totalNumber = task.getResult().getLong("prevMonthPoints.totalPoints");
                            if (totalNumber >= 0) {
                                myPointsVote = totalNumber - points;

                                db.collection("Points").document(mAuth.getCurrentUser().getUid()).update("prevMonthPoints.totalPoints", myPointsVote);



                                DocumentReference playerVote = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID()));
                                playerVote.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            long currentPointsPlayer = (long) task.getResult().get("playerPoints");
                                            long setPoints = currentPointsPlayer + points;
                                            Map<String, Object> playerInfo = new HashMap<>();
                                            playerInfo.put("playerName", model.getName());
                                            playerInfo.put("playerImage", model.getImagePlayer());
                                            playerInfo.put("playerPoints", setPoints);
                                            playerInfo.put("timestamp", FieldValue.serverTimestamp());

                                            DocumentReference playerVote = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID()));
                                            playerVote.update(playerInfo);

                                            DocumentReference usersVote = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());

                                            usersVote.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Map<String, Object> usersInfo = new HashMap<>();
                                                    usersInfo.put("uid", mAuth.getCurrentUser().getUid());
                                                    usersInfo.put("timestamp", FieldValue.serverTimestamp());
                                                    if (task.getResult().exists()){
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.update(usersInfo);
                                                    } else {
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.set(usersInfo);
                                                    }
                                                }
                                            });


                                        } else {
                                            Map<String, Object> playerInfo = new HashMap<>();
                                            playerInfo.put("playerName", model.getName());
                                            playerInfo.put("playerImage", model.getImagePlayer());
                                            playerInfo.put("playerPoints", points);
                                            playerInfo.put("timestamp", FieldValue.serverTimestamp());

                                            DocumentReference playerVote = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID()));
                                            playerVote.set(playerInfo);

                                            DocumentReference usersVote = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());

                                            usersVote.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Map<String, Object> usersInfo = new HashMap<>();
                                                    usersInfo.put("uid", mAuth.getCurrentUser().getUid());
                                                    usersInfo.put("timestamp", FieldValue.serverTimestamp());
                                                    if (task.getResult().exists()){
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.update(usersInfo);
                                                    } else {
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.set(usersInfo);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("err", e.getLocalizedMessage());
                        }
                    });
                }
            }
        })

                .setNegativeButton("Cancel", null);

        playerVoteDialogBuilder.create();
        playerVoteDialogBuilder.show();

    }

    @Override
    public int getItemCount() {
        return topScorerModelArrayList.size();
    }
}
