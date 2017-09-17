package com.malikbisic.sportapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.Messages;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 15/09/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{
    List<Messages> listMessage;
    Context ctx;

    public ChatListAdapter(List<Messages> listMessage, Context ctx) {
        this.listMessage = listMessage;
        this.ctx = ctx;
    }

    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_chat_list_row, parent, false);
        return  new ChatListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ChatListViewHolder holder, int position) {
        final Messages model = listMessage.get(position);

        holder.lastMessageTxt.setText(model.getMessage());

        DatabaseReference usersInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getFrom());
        usersInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = String.valueOf(dataSnapshot.child("username").getValue());
                String profileImage = String.valueOf(dataSnapshot.child("profileImage").getValue());

                holder.usernameTxt.setText(username);
                Picasso.with(ctx).load(profileImage).into(holder.profileImageImg);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return  listMessage == null ? 0 : listMessage.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {

        View view;
        CircleImageView profileImageImg;
        TextView usernameTxt;
        TextView lastMessageTxt;

        public ChatListViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            profileImageImg = (CircleImageView) view.findViewById(R.id.profileImage_chatList);
            usernameTxt = (TextView) view.findViewById(R.id.username_chatList);
            lastMessageTxt = (TextView) view.findViewById(R.id.lastChat_chatList);

        }
    }
}
