package com.malikbisic.sportapp.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.firebase.Comments;
import com.malikbisic.sportapp.model.firebase.PlayerCommentsModel;
import com.malikbisic.sportapp.viewHolder.firebase.PlayerCommentsViewHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by malikbisic on 17/01/2018.
 */

public class PlayerComments {

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public void sendComments(ImageButton sendBtn, final String playerID, final EditText commentsText) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comments = commentsText.getText().toString().trim();

                final Map<String, Object> comPlayerMap = new HashMap<>();
                comPlayerMap.put("text", comments);
                comPlayerMap.put("uid", mAuth.getCurrentUser().getUid());
                comPlayerMap.put("timestamp", FieldValue.serverTimestamp());
                CollectionReference playerRef = db.collection("PlayerComments").document(playerID).collection("player-id");
                playerRef.add(comPlayerMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        commentsText.setText("");
                    }
                });


            }
        });
    }

    public void getComments(RecyclerView recyclerView, String playerID, Context  ctx){
        db = FirebaseFirestore.getInstance();
        Query comRef = db.collection("PlayerComments").document(playerID).collection("player-id").orderBy("timestamp", Query.Direction.DESCENDING);

        final FirestoreRecyclerOptions<PlayerCommentsModel> response = new FirestoreRecyclerOptions.Builder<PlayerCommentsModel>()
                .setQuery(comRef, PlayerCommentsModel.class)
                .build();

        FirestoreRecyclerAdapter<PlayerCommentsModel, PlayerCommentsViewHolder> adapter = new FirestoreRecyclerAdapter<PlayerCommentsModel, PlayerCommentsViewHolder>(response) {
            @Override
            protected void onBindViewHolder(PlayerCommentsViewHolder holder, int position, PlayerCommentsModel model) {
                holder.setTextComments(model.getText());
                holder.profileSet(model.getUid());
            }

            @Override
            public PlayerCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_player_item, parent, false);
                return new PlayerCommentsViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
