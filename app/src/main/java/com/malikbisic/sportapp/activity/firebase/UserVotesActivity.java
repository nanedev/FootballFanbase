package com.malikbisic.sportapp.activity.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.UserVotesAdapter;
import com.malikbisic.sportapp.model.firebase.UserVoteModel;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class UserVotesActivity extends AppCompatActivity {

    Intent myIntent;
    RecyclerView usersVoteRecView;
    String playerID;

    ArrayList<UserVoteModel> userVoteModels = new ArrayList<>();
    UserVotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_votes);

        myIntent = getIntent();
        playerID = myIntent.getStringExtra("playerID");

        adapter = new UserVotesAdapter(userVoteModels, this);
        usersVoteRecView = (RecyclerView) findViewById(R.id.uservotesRecylerview);
        usersVoteRecView.setLayoutManager(new LinearLayoutManager(this));
        usersVoteRecView.setAdapter(adapter);

        DateTime prevDate = new DateTime().minusMonths(1);
        final String prevMonth = prevDate.toString("MMMM");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("PlayerPoints").document(prevMonth).collection("player-id").document(playerID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){

                final String playerName = task.getResult().getString("playerName");
                final String playerImage = task.getResult().getString("playerImage");
                final String playerPoints = String.valueOf(task.getResult().getLong("playerPoints"));
///PlayerPoints/February/player-id/180/usersVote
                db.collection("PlayerPoints").document(prevMonth).collection("player-id").document(playerID).collection("usersVote").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()){
                            String userID = snapshot.getString("uid");
                            String userGivePoints = snapshot.getString("userGivePoints");


                            FirebaseFirestore dbuser = FirebaseFirestore.getInstance();

                            dbuser.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String username = task.getResult().getString("username");
                                    String profileImage = task.getResult().getString("profileImage");

                                    UserVoteModel model = new UserVoteModel(profileImage, username, playerImage, playerName, playerPoints + "pts");
                                    userVoteModels.add(model);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }}
        });
    }
}
