package com.malikbisic.sportapp.adapter.firebase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.ChatMessageActivity;
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
                holder.lastMessageTxt.setText(ctx.getResources().getString(R.string.sentPhoto));
            }else if (type.equals("text")){
                holder.lastMessageTxt.setText(ctx.getResources().getString(R.string.sentText, model.getMessage()));
            } else if (type.equals("gallery")){
                holder.lastMessageTxt.setText(ctx.getResources().getString(R.string.sentGallery, model.getGalleryImage().size()));
            } else if (type.equals("audio")){
                holder.lastMessageTxt.setText(ctx.getResources().getString(R.string.sentaudioFile));
            }
        } else {
            DocumentReference usersInfo = FirebaseFirestore.getInstance().collection("Users").document(fromUID);
            usersInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    String username = String.valueOf(documentSnapshot.getString("username"));
                    if (type.equals("image")){
                        holder.lastMessageTxt.setText(ctx.getResources().getString(R.string.usersentPhoto, username));
                    }else if (type.equals("text")){
                        holder.lastMessageTxt.setText(ctx.getResources().getString(R.string.usersentText, username, model.getMessage()));
                    } else if (type.equals("gallery")){
                        holder.lastMessageTxt.setText(ctx.getResources().getString(R.string.usersentGallery, username, model.getGalleryImage().size()));
                    } else if (type.equals("audio")){
                        holder.lastMessageTxt.setText(ctx.getResources().getString(R.string.usersentaudioFile, username));
                    }

                }
            });

            if (!model.isSeen()){
                holder.lastMessageTxt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                holder.lastMessageTxt.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
        }

        if (model.getTime() != null) {
            String time = DateUtils.formatDateTime(ctx, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
            holder.timeLastMessageTxt.setText(time);
        }


        DocumentReference usersInfo = FirebaseFirestore.getInstance().collection("Users").document(model.getTo());
        usersInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                final String username = String.valueOf(documentSnapshot.getString("username"));
                String profileImage = String.valueOf(documentSnapshot.getString("profileImage"));

                holder.usernameTxt.setText(username);
                Picasso.with(ctx).load(profileImage).into(holder.profileImageImg);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference seenMessage = db.collection("Messages").document(model.getTo()).collection("chat-user").document(mAuth.getCurrentUser().getUid()).collection("message");
                        Query queryMessage = seenMessage.whereEqualTo("seen", false);

                        queryMessage.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (DocumentSnapshot snapshot : task.getResult()){
                                    String docID = snapshot.getId();

                                    db.collection("Messages").document(model.getTo()).collection("chat-user").document(mAuth.getCurrentUser().getUid()).collection("message").document(docID)
                                    .update("seen", true);
                                }
                            }
                        });

                        Intent intent = new Intent(ctx, ChatMessageActivity.class);
                        intent.putExtra("userId", model.getTo());
                        intent.putExtra("username", username);
                        ctx.startActivity(intent);
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return  listMessage == null ? 0 : listMessage.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {

        View view;
        public CircleImageView profileImageImg;
        public TextView usernameTxt;
        public TextView lastMessageTxt;
        public TextView timeLastMessageTxt;

        public ChatListViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            profileImageImg = (CircleImageView) view.findViewById(R.id.profileImage_chatList);
            usernameTxt = (TextView) view.findViewById(R.id.username_chatList);
            lastMessageTxt = (TextView) view.findViewById(R.id.lastChat_chatList);
            timeLastMessageTxt = (TextView) view.findViewById(R.id.timeLastMessage);

        }
    }
}
