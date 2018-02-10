package com.malikbisic.sportapp.adapter.firebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
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
import com.malikbisic.sportapp.utils.RoundedTransformation;
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
                holder.layoutToUser.setVisibility(View.VISIBLE);
                holder.layoutFromUser.setVisibility(View.GONE);
                holder.layoutImageFromUser.setVisibility(View.GONE);
                holder.layoutImageToUser.setVisibility(View.GONE);

                if (type.equals("text")) {
                    holder.layoutToUser.setVisibility(View.VISIBLE);
                    holder.layoutFromUser.setVisibility(View.GONE);
                    holder.layoutImageFromUser.setVisibility(View.GONE);
                    holder.layoutImageToUser.setVisibility(View.GONE);
                    holder.messageTextTOUser.setText(messages.getMessage());
                    if (messages.getTime() != null) {
                        String time = DateUtils.formatDateTime(ctx, messages.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                        holder.timeTextViewToUser.setText(time);
                    }
                } else if (type.equals("image")) {
                    holder.layoutImageToUser.setVisibility(View.VISIBLE);
                    holder.layoutFromUser.setVisibility(View.GONE);
                    holder.layoutImageFromUser.setVisibility(View.GONE);
                  holder.layoutToUser.setVisibility(View.GONE);
                    Picasso.with(ctx).setIndicatorsEnabled(false);
                    Picasso.with(ctx).load(messages.getMessage()).transform(new RoundedTransformation(20,3)).fit().centerCrop().into(holder.messageImageViewToUser);

                    if (messages.getTime() != null) {
                        String time = DateUtils.formatDateTime(ctx, messages.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                        holder.timeImageTOUser.setText(time);
                    }
                }


            } else {

                holder.layoutFromUser.setVisibility(View.VISIBLE);
                holder.profileImageImg.setVisibility(View.VISIBLE);
                holder.layoutToUser.setVisibility(View.GONE);
                holder.layoutImageToUser.setVisibility(View.GONE);
                holder.layoutImageFromUser.setVisibility(View.GONE);


                if (type.equals("text")) {
                    holder.layoutFromUser.setVisibility(View.VISIBLE);
                    holder.layoutToUser.setVisibility(View.GONE);
                    holder.layoutImageToUser.setVisibility(View.GONE);
                    holder.layoutImageFromUser.setVisibility(View.GONE);
                    holder.userProfileForIMage.setVisibility(View.GONE);
                    holder.messageTextFromUser.setText(messages.getMessage());
                    if (messages.getTime() != null) {
                        String time = DateUtils.formatDateTime(ctx, messages.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                        holder.timeTextViewFromUser.setText(time);
                    }
                } else if (type.equals("image")) {
                    holder.layoutImageFromUser.setVisibility(View.VISIBLE);
                    holder.layoutToUser.setVisibility(View.GONE);
                    holder.layoutImageToUser.setVisibility(View.GONE);
                    holder.userProfileForIMage.setVisibility(View.VISIBLE);
               holder.layoutFromUser.setVisibility(View.GONE);
                    Picasso.with(ctx).setIndicatorsEnabled(false);
                    Picasso.with(ctx).load(messages.getMessage()).transform(new RoundedTransformation(20,3)).fit().centerCrop().into(holder.messageImageViewFromUser);

                    if (messages.getTime() != null) {
                        String time = DateUtils.formatDateTime(ctx, messages.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                        holder.timeImageFromUser.setText(time);
                    }
                    FirebaseFirestore displayImage = FirebaseFirestore.getInstance();

                    displayImage.collection("Users").document(from_user).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                            UserChat model2 = dataSnapshot.toObject(UserChat.class);
                            String profileImage = model2.getProfileImage();

                            holder.setProfileImageForImage(ctx, profileImage);
                        }
                    });
                }


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


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextFromUser;
        public TextView messageTextTOUser;
        public CircleImageView profileImageImg;

        public TextView timeTextViewFromUser;
        public TextView timeTextViewToUser;
        public ImageView messageImageViewFromUser;
        public ImageView messageImageViewToUser;
        public RelativeLayout layoutFromUser;
        public RelativeLayout layoutToUser;
        public RelativeLayout layoutImageFromUser;
        public RelativeLayout layoutImageToUser;
        public TextView timeImageTOUser;
        public TextView timeImageFromUser;
        public CircleImageView userProfileForIMage;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageTextFromUser = (TextView) itemView.findViewById(R.id.message_textFromUser);
            messageTextTOUser = (TextView) itemView.findViewById(R.id.message_textToUser);
            profileImageImg = (CircleImageView) itemView.findViewById(R.id.message_image);
            layoutFromUser = (RelativeLayout) itemView.findViewById(R.id.message_from_user);
            layoutToUser = (RelativeLayout) itemView.findViewById(R.id.message_layout_to_user);
            timeTextViewFromUser = (TextView) itemView.findViewById(R.id.timeMessageFromUser);
            timeTextViewToUser = (TextView) itemView.findViewById(R.id.timeMessageToUser);
            layoutImageFromUser = (RelativeLayout) itemView.findViewById(R.id.messageimagelayoutFromUser);
            layoutImageToUser = (RelativeLayout) itemView.findViewById(R.id.messageimagelayoutToUser);
            timeImageFromUser = (TextView) itemView.findViewById(R.id.timemessageImageFromUser);
            timeImageTOUser = (TextView) itemView.findViewById(R.id.timemessageImageToUser);
            messageImageViewFromUser = (ImageView) itemView.findViewById(R.id.imageMessageFromUser);
            messageImageViewToUser = (ImageView) itemView.findViewById(R.id.imageMessageToUser);
            userProfileForIMage = (CircleImageView) itemView.findViewById(R.id.message_imageImageFrom);

        }

        public void setProfileImageImg(Context ctx, String profileImage) {
            Picasso.with(ctx).load(profileImage).into(profileImageImg);
        }
        public void setProfileImageForImage(Context ctx, String profileImage) {
            Picasso.with(ctx).load(profileImage).into(userProfileForIMage);
        }
    }

}