package com.malikbisic.sportapp.adapter.firebase;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.FootballPlayer;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.viewHolder.api.PlayersInProfileViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 06/01/2018.
 */

public class PlayerFirebaseAdapter extends RecyclerView.Adapter<PlayersInProfileViewHolder> {

    List<PlayerModel> list;
    Activity activity;
    FirebaseAuth mAuth;
    long myPointsVote;
    long playerPoints;

    public PlayerFirebaseAdapter(List<PlayerModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public PlayersInProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_profileplayer, parent, false);
        return new PlayersInProfileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlayersInProfileViewHolder holder, int position) {
        final PlayerModel model = list.get(position);
        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImage())
                .into(holder.image);
        holder.pointPlayer.setText("" +list.get(position).getPoints());
        holder.playerPosition.setText(model.getId() + ".");

        holder.voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                votePlayer(model);
            }
        });
    }

    public void votePlayer(final PlayerModel model){
        DateFormat currentDateFormat = new SimpleDateFormat("MMMM");
        final Date currentDate = new Date();

        final String currentMonth = currentDateFormat.format(currentDate);
        AlertDialog.Builder playerVoteDialogBuilder = new AlertDialog.Builder(activity);
        View viewDialog = LayoutInflater.from(activity).inflate(R.layout.vote_player_dialog, null);
        playerVoteDialogBuilder.setView(viewDialog);

        final TextView playerPointsTextview = (TextView) viewDialog.findViewById(R.id.playerPointsVote);
        CircleImageView playerImage = (CircleImageView) viewDialog.findViewById(R.id.playerImageVote);
        TextView playerName = (TextView) viewDialog.findViewById(R.id.playerNameVote);
        final TextView myPoints = (TextView) viewDialog.findViewById(R.id.myPointsVote);
        final EditText enterPointsVote = (EditText) viewDialog.findViewById(R.id.enterPointsVote);


        Glide.with(playerImage.getContext()).load(model.getImage()).into(playerImage);
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

        final DocumentReference documentReference = db.collection("PlayerPoints").document(String.valueOf(model.getPlayerID()));
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {



                if (documentSnapshot.exists()) {


                    playerPoints = documentSnapshot.getLong("playerPoints");
                    playerPointsTextview.setText("Player points: " + playerPoints);

                } else {
                    playerPointsTextview.setText("Player points: " + 0);
                }
            }
        });


        playerVoteDialogBuilder.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enterPoint = enterPointsVote.getText().toString().trim();
                if (!TextUtils.isEmpty(enterPoint)) {
                    final long points = Integer.parseInt(enterPoint);

                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference myPointsGet = db.collection("Points").document(mAuth.getCurrentUser().getUid());
                    myPointsGet.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            long totalNumber = task.getResult().getLong("prevMonthPoints.totalPoints");
                            if (totalNumber >= 0) {
                                myPointsVote = totalNumber - points;

                                db.collection("Points").document(mAuth.getCurrentUser().getUid()).update("prevMonthPoints.totalPoints", myPointsVote);



                                DocumentReference playerVote = db.collection("PlayerPoints").document(currentMonth).collection("player-id").document(String.valueOf(model.getPlayerID()));
                                playerVote.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            long currentPointsPlayer = (long) task.getResult().get("playerPoints");
                                            long setPoints = currentPointsPlayer + points;
                                            Map<String, Object> playerInfo = new HashMap<>();
                                            playerInfo.put("playerName", model.getName());
                                            playerInfo.put("playerImage", model.getImage());
                                            playerInfo.put("playerPoints", setPoints);
                                            playerInfo.put("timestamp", FieldValue.serverTimestamp());

                                            DocumentReference playerVote = db.collection("PlayerPoints").document(currentMonth).collection("player-id").document(String.valueOf(model.getPlayerID()));
                                            playerVote.update(playerInfo);

                                            DocumentReference usersVote = db.collection("PlayerPoints").document(currentMonth).collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());

                                            usersVote.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Map<String, Object> usersInfo = new HashMap<>();
                                                    usersInfo.put("uid", mAuth.getCurrentUser().getUid());
                                                    usersInfo.put("timestamp", FieldValue.serverTimestamp());
                                                    if (task.getResult().exists()){
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document(currentMonth).collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.update(usersInfo);
                                                    } else {
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document(currentMonth).collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.set(usersInfo);
                                                    }
                                                }
                                            });


                                        } else {
                                            Map<String, Object> playerInfo = new HashMap<>();
                                            playerInfo.put("playerName", model.getName());
                                            playerInfo.put("playerImage", model.getImage());
                                            playerInfo.put("playerPoints", points);
                                            playerInfo.put("timestamp", FieldValue.serverTimestamp());

                                            DocumentReference playerVote = db.collection("PlayerPoints").document(currentMonth).collection("player-id").document(String.valueOf(model.getPlayerID()));
                                            playerVote.set(playerInfo);

                                            DocumentReference usersVote = db.collection("PlayerPoints").document(currentMonth).collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());

                                            usersVote.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Map<String, Object> usersInfo = new HashMap<>();
                                                    usersInfo.put("uid", mAuth.getCurrentUser().getUid());
                                                    usersInfo.put("timestamp", FieldValue.serverTimestamp());
                                                    if (task.getResult().exists()){
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document(currentMonth).collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.update(usersInfo);
                                                    } else {
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document(currentMonth).collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.set(usersInfo);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                                DocumentReference playerVoteAlltime = db.collection("PlayerPoints").document("AllTime").collection("player-id").document(String.valueOf(model.getPlayerID()));
                                playerVoteAlltime.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            long currentPointsPlayer = (long) task.getResult().get("playerPoints");
                                            long setPoints = currentPointsPlayer + points;
                                            Map<String, Object> playerInfo = new HashMap<>();
                                            playerInfo.put("playerName", model.getName());
                                            playerInfo.put("playerImage", model.getImage());
                                            playerInfo.put("playerPoints", setPoints);
                                            playerInfo.put("timestamp", FieldValue.serverTimestamp());

                                            DocumentReference playerVote = db.collection("PlayerPoints").document("AllTime").collection("player-id").document(String.valueOf(model.getPlayerID()));
                                            playerVote.update(playerInfo);

                                            DocumentReference usersVote = db.collection("PlayerPoints").document("AllTime").collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());

                                            usersVote.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Map<String, Object> usersInfo = new HashMap<>();
                                                    usersInfo.put("uid", mAuth.getCurrentUser().getUid());
                                                    usersInfo.put("timestamp", FieldValue.serverTimestamp());
                                                    if (task.getResult().exists()){
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document("AllTime").collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.update(usersInfo);
                                                    } else {
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document("AllTime").collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.set(usersInfo);
                                                    }
                                                }
                                            });


                                        } else {
                                            Map<String, Object> playerInfo = new HashMap<>();
                                            playerInfo.put("playerName", model.getName());
                                            playerInfo.put("playerImage", model.getImage());
                                            playerInfo.put("playerPoints", points);
                                            playerInfo.put("timestamp", FieldValue.serverTimestamp());

                                            DocumentReference playerVote = db.collection("PlayerPoints").document("AllTime").collection("player-id").document(String.valueOf(model.getPlayerID()));
                                            playerVote.set(playerInfo);

                                            DocumentReference usersVote = db.collection("PlayerPoints").document("AllTime").collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());

                                            usersVote.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Map<String, Object> usersInfo = new HashMap<>();
                                                    usersInfo.put("uid", mAuth.getCurrentUser().getUid());
                                                    usersInfo.put("timestamp", FieldValue.serverTimestamp());
                                                    if (task.getResult().exists()){
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document("AllTime").collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
                                                        usersVote.update(usersInfo);
                                                    } else {
                                                        DocumentReference usersVote = db.collection("PlayerPoints").document("AllTime").collection("player-id").document(String.valueOf(model.getPlayerID())).collection("usersVote").document(mAuth.getCurrentUser().getUid());
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
        return list.size();
    }
}
