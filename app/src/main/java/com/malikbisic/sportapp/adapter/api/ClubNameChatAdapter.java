package com.malikbisic.sportapp.adapter.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.ChatMessageActivity;
import com.malikbisic.sportapp.model.firebase.UserChat;
import com.malikbisic.sportapp.viewHolder.firebase.ClubNameViewHolder;
import com.malikbisic.sportapp.viewHolder.firebase.UsersChatViewHolder;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by korisnik on 03/09/2017.
 */

public class ClubNameChatAdapter extends ExpandableRecyclerViewAdapter<ClubNameViewHolder, UsersChatViewHolder> {
    Context ctx;
    Activity activity;
    int numberOnline = 0;
    boolean isOnline;

    public ClubNameChatAdapter(List<? extends ExpandableGroup> groups, Context ctx, Activity activity) {
        super(groups);
        this.ctx = ctx;
        this.activity = activity;

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

                Intent intent = new Intent(ctx, ChatMessageActivity.class);
                intent.putExtra("userId", userChat.getUserID());
                intent.putExtra("username", userChat.getUsername());
                intent.putExtra("flag", userChat.getFlag());
                intent.putExtra("date", userChat.getDate());
                ctx.startActivity(intent);
            }
        });

        final DocumentReference onlineReference = FirebaseFirestore.getInstance().collection("UsersChat").document(group.getTitle()).collection("user-id").document(userChat.getUserID());
        onlineReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                if (dataSnapshot.exists()) {
                    String usernameDatabase = String.valueOf(dataSnapshot.getString("username"));
                    String username = holder.usernameUser.getText().toString().trim();
                    if (dataSnapshot.get("online") instanceof String) {
                        isOnline = Boolean.parseBoolean(String.valueOf(dataSnapshot.getString("online")));
                    } else {
                        isOnline = Boolean.parseBoolean(String.valueOf(dataSnapshot.getDate("online")));
                    }

                    if (isOnline) {
                        numberOnline++;
                    }

                    if (isOnline && username.equals(usernameDatabase)) {
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
        holder.setNumberOnline(group.getTitle(), activity);


    }
}
