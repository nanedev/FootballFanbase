package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.MyPostsActivity;
import com.malikbisic.sportapp.activity.UserProfileActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nane on 29.9.2017.
 */

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.UserProfileViewHodler>{

    private String[] titles = {"Posts",  "Rank", "Premium"};
    private int[] images = {R.drawable.posts, R.drawable.boost, R.drawable.premium};

    int number;
    Activity activity;
    String uid;

    public UserProfileAdapter(Activity activity,String uid) {
        this.activity = activity;
        this.uid = uid;
    }



    @Override
    public UserProfileViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_in_user_profile, parent, false);


        return new UserProfileAdapter.UserProfileViewHodler(v);
    }

    @Override
    public void onBindViewHolder(UserProfileViewHodler holder, int position) {
        holder.itemTitle.setText(titles[position]);
        holder.itemImage.setImageResource(images[position]);
        if (position == 0){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, MyPostsActivity.class);
                    activity.startActivity(intent);
                }
            });

        }

        if (position == 0){
            holder.numberPost();
        }

       /* holder.itemTitle.setText(titles[position]);
        holder.itemImage.setImageResource(images[position]);

        if (position == 0){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, MyPostsActivity.class);
                    activity.startActivity(intent);
                }
            });

        }*/

     /*   if (position == 0){
            holder.numberPost();
        } else if (position == 1){
            holder.numberPointsForYourTeam();
        } else if (position == 2){
            holder.positionTeam();
        }else if (position == 3){
            holder.premiumTrialDateText();
        }*/
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class UserProfileViewHodler extends RecyclerView.ViewHolder{
        ImageView itemImage;
        TextView itemTitle;
        TextView numberSomething;
        FirebaseDatabase mDatabase;
        DatabaseReference mReference;

        String uid;
        public UserProfileViewHodler(View itemView) {
            super(itemView);

            mReference = mDatabase.getReference().child("Users").child(uid);
        }

        public void numberPost() {

            DatabaseReference numberPostRef = FirebaseDatabase.getInstance().getReference().child("Posting");
            Query numberPostQuery = numberPostRef.orderByChild("uid").equalTo(uid);
            numberPostQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    number  = (int) dataSnapshot.getChildrenCount();
                    numberSomething.setText(""+number);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
