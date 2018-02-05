package com.malikbisic.sportapp.adapter.firebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.firebase.Messages;
import com.malikbisic.sportapp.model.firebase.UserChat;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 11.9.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    FirebaseAuth mAutH;
    Context ctx;
    Activity activity;

    public MessageAdapter(List<Messages> mMessageList, Context ctx, Activity activity) {
        this.mMessageList = mMessageList;
        this.ctx = ctx;
        this.activity = activity;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        mAutH = FirebaseAuth.getInstance();

        String current_user_id = mAutH.getCurrentUser().getUid();

        Messages messages = mMessageList.get(position);
        String from_user = messages.getFrom();
        String type = messages.getType();
        if (from_user != null) {

            if (from_user.equals(current_user_id)) {

                holder.messagetTextTexview.setBackgroundColor(Color.WHITE);
                holder.messagetTextTexview.setTextColor(R.color.black);
                holder.layout.setGravity(Gravity.RIGHT);
                holder.messagetTextTexview.setTypeface(holder.messagetTextTexview.getTypeface(), Typeface.BOLD);
                holder.profileImageImg.setVisibility(View.GONE);

            } else {
                holder.messagetTextTexview.setBackgroundResource(R.drawable.message_text_background);
                holder.messagetTextTexview.setTextColor(Color.WHITE);
                holder.layout.setGravity(Gravity.LEFT);
                holder.messagetTextTexview.setTypeface(holder.messagetTextTexview.getTypeface(), Typeface.BOLD);
                holder.profileImageImg.setVisibility(View.VISIBLE);

                FirebaseFirestore displayImage = FirebaseFirestore.getInstance();

                displayImage.collection("Users").document(from_user).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                        UserChat model2 = dataSnapshot.toObject(UserChat.class);
                        String profileImage = model2.getProfileImage();

                        holder.setProfileImageImg(ctx, profileImage);
                    }
                });
            }

        }

        Log.i("typeMessage", type);

        if (type.equals("text")) {
            holder.messagetTextTexview.setVisibility(View.VISIBLE);
            holder.messagetTextTexview.setText(messages.getMessage());
            holder.messageImageView.setVisibility(View.INVISIBLE);
        } else if (type.equals("image")){
            holder.messageImageView.setVisibility(View.VISIBLE);
            Glide.with(ctx).load(messages.getMessage()).into(holder.messageImageView);
            holder.messagetTextTexview.setVisibility(View.INVISIBLE);
        }
        if (messages.getTime() != null) {
            String time = DateUtils.formatDateTime(ctx, messages.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
            holder.timeTextView.setText(time);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messagetTextTexview;
        public CircleImageView profileImageImg;
        public RelativeLayout layout;
        public TextView timeTextView;
        public ImageView messageImageView;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messagetTextTexview = (TextView) itemView.findViewById(R.id.message_text);
            profileImageImg = (CircleImageView) itemView.findViewById(R.id.message_image);
            layout = (RelativeLayout) itemView.findViewById(R.id.message_single_layout);
            timeTextView = (TextView) itemView.findViewById(R.id.timeMessage);
            messageImageView = (ImageView) itemView.findViewById(R.id.message_image_sender);

        }

        public void setProfileImageImg(Context ctx, String profileImage) {
            Picasso.with(ctx).load(profileImage).into(profileImageImg);
        }

    }

}