package com.malikbisic.sportapp.adapter.firebase;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.firebase.Messages;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 15/09/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{
    List<Messages> listMessage;
    Context ctx;
    FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();
        String myUID = mAuth.getCurrentUser().getUid();
        final Messages model = listMessage.get(position);

        String fromUID = model.getFrom();
        final String type = model.getType();

        if (myUID.equals(fromUID)){
            if (type.equals("image")){
                holder.lastMessageTxt.setText("You: " + "Sent image");
            }else {
                holder.lastMessageTxt.setText("You: " + model.getMessage());
            }
        } else {
            DocumentReference usersInfo = FirebaseFirestore.getInstance().collection("Users").document(fromUID);
            usersInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    String username = String.valueOf(documentSnapshot.getString("username"));
                    if (type.equals("image")){
                        holder.lastMessageTxt.setText(username + ": " + "Sent image");
                    }else {
                        holder.lastMessageTxt.setText(username + ": " + model.getMessage());
                    }

                }
            });

            if (!model.isSeen()){
                holder.lastMessageTxt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                holder.lastMessageTxt.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
        }



        DocumentReference usersInfo = FirebaseFirestore.getInstance().collection("Users").document(model.getTo());
        usersInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                String username = String.valueOf(documentSnapshot.getString("username"));
                String profileImage = String.valueOf(documentSnapshot.getString("profileImage"));

                holder.usernameTxt.setText(username);
                Picasso.with(ctx).load(profileImage).into(holder.profileImageImg);
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
