package com.malikbisic.sportapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.UserChat;
import com.malikbisic.sportapp.model.UserChatGroup;
import com.malikbisic.sportapp.viewHolder.ClubNameViewHolder;
import com.malikbisic.sportapp.viewHolder.UsersChatViewHolder;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by korisnik on 03/09/2017.
 */

public class ClubNameChatAdapter extends ExpandableRecyclerViewAdapter<ClubNameViewHolder, UsersChatViewHolder> {
    Context ctx;

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
    public void onBindChildViewHolder(UsersChatViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

        UserChat userChat = (UserChat) group.getItems().get(childIndex);
        holder.setUsername(userChat.getUsername());
        holder.setFlag(ctx, userChat.getFlag());
        holder.setProfileImage(ctx, userChat.getProfileImage());
        holder.setOnlineImage();

    }

    @Override
    public void onBindGroupViewHolder(ClubNameViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setClubTitle(group);
    }
}
