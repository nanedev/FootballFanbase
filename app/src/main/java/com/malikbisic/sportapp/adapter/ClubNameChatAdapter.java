package com.malikbisic.sportapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.FragmentChatUsers;
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

        UserChat userChat = (UserChat) group.getItems().get(childIndex);
        holder.setUsername(userChat.getUsername());
        holder.setFlag(ctx, userChat.getFlag());
        holder.setProfileImage(ctx, userChat.getProfileImage());
        holder.setDate(userChat.getDate());



        final DatabaseReference onlineReference = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(group.getTitle()).child(userChat.getUserID());
        onlineReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String usernameDatabase = String.valueOf(dataSnapshot.child("username").getValue());
                String username = holder.usernameUser.getText().toString().trim();
                boolean isOnline = (boolean) dataSnapshot.child("online").getValue();
                Log.i("ref", String.valueOf(dataSnapshot.getRef()));

                if (isOnline){
                    numberOnline++;
                }

                if (isOnline && username.equals(usernameDatabase)) {
                    holder.onlineImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.online_shape));

                } else {
                    holder.onlineImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.offline_shape));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBindGroupViewHolder(ClubNameViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setClubTitle(group);
        holder.setNumberOnline(group.getTitle());
    }
}
