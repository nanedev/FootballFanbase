package com.malikbisic.sportapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.ChatMessageActivity;
import com.malikbisic.sportapp.activity.FragmentChatUsers;
import com.malikbisic.sportapp.activity.MainPage;
import com.malikbisic.sportapp.model.UserChat;
import com.malikbisic.sportapp.model.UserChatGroup;
import com.malikbisic.sportapp.viewHolder.ClubNameViewHolder;
import com.malikbisic.sportapp.viewHolder.UsersChatViewHolder;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by korisnik on 03/09/2017.
 */

public class ClubNameChatAdapter extends ExpandableRecyclerViewAdapter<ClubNameViewHolder, UsersChatViewHolder> {
    Context ctx;
    int numberOnline = 0;
    public ClubNameChatAdapter(List<? extends ExpandableGroup> groups, Context ctx) {
        super(groups);
        this.ctx = ctx;
    }

    @Override
    public ClubNameViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_layout_users, parent, false);

        return new ClubNameViewHolder(view);
    }

    @Override
    public UsersChatViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_layout_users, parent, false);

        return new UsersChatViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(final UsersChatViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

        final UserChat userChat = (UserChat) group.getItems().get(childIndex);
        holder.setUsername(userChat.getUsername());
        holder.setFlag(ctx, userChat.getFlag());
        holder.setProfileImage(ctx, userChat.getProfileImage());
        holder.setDate(userChat.getDate());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ctx,ChatMessageActivity.class);
                intent.putExtra("userId",userChat.getUserID());
                intent.putExtra("username",userChat.getUsername());
                intent.putExtra("flag",userChat.getFlag());
                intent.putExtra("date",userChat.getDate());
                ctx.startActivity(intent);
            }
        });



        final CollectionReference onlineReference = FirebaseFirestore.getInstance().collection("UsersChat").document(group.getTitle()).collection(userChat.getUserID());
        onlineReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {

                for (DocumentSnapshot snapshot : dataSnapshot.getDocuments()) {
                    String usernameDatabase = String.valueOf(snapshot.getString("username"));
                    String username = holder.usernameUser.getText().toString().trim();
                    String isOnline = String.valueOf(snapshot.getString("online"));

                    if (isOnline.equals("true")) {
                        numberOnline++;
                    }

                    if (isOnline.equals("true") && username.equals(usernameDatabase)) {
                        holder.onlineImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.online_shape));

                    } else {
                        holder.onlineImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.offline_shape));
                    }
                }
            }

        });

    }

    @Override
    public void onBindGroupViewHolder(ClubNameViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setClubTitle(group);
        holder.setNumberOnline(group.getTitle());


    }
}
