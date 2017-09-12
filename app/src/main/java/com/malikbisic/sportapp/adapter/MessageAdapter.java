package com.malikbisic.sportapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.Messages;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 11.9.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    FirebaseAuth mAutH;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        mAutH = FirebaseAuth.getInstance();

            String current_user_id = mAutH.getCurrentUser().getUid();

        Messages messages = mMessageList.get(position);
        String from_user = messages.getFrom();

       if (from_user.equals(current_user_id)){

            holder.messagetTextTexview.setBackgroundColor(Color.WHITE);
            holder.messagetTextTexview.setTextColor(R.color.black);
           holder.messagetTextTexview.setGravity(Gravity.RIGHT);
           holder.messagetTextTexview.setTypeface(holder.messagetTextTexview.getTypeface(), Typeface.BOLD);

        }else {
            holder.messagetTextTexview.setBackgroundResource(R.drawable.message_text_background);
            holder.messagetTextTexview.setTextColor(Color.WHITE);
           holder.messagetTextTexview.setTypeface(holder.messagetTextTexview.getTypeface(), Typeface.BOLD);


        }

        holder.messagetTextTexview.setText(messages.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messagetTextTexview;
        public CircleImageView profileImageImg;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messagetTextTexview = (TextView) itemView.findViewById(R.id.message_text);
            profileImageImg = (CircleImageView) itemView.findViewById(R.id.message_image);

        }

    }

}
