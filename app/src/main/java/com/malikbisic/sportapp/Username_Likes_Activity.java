package com.malikbisic.sportapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Username_Likes_Activity extends AppCompatActivity {

    RecyclerView likesRec;
    DatabaseReference likesReferences;
    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username__likes_);
        myIntent = getIntent();
        String post_key = myIntent.getStringExtra("post_key");
        likesReferences = FirebaseDatabase.getInstance().getReference().child("Likes").child(post_key);

        likesRec = (RecyclerView) findViewById(R.id.rec_view_username_likes);
        likesRec.setHasFixedSize(false);
        likesRec.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<LikesUsernamePhoto, LikesViewHolder> populateRecView = new FirebaseRecyclerAdapter<LikesUsernamePhoto, LikesViewHolder>(
                LikesUsernamePhoto.class,
                R.layout.username_likes_row,
                LikesViewHolder.class,
                likesReferences
        ) {
            @Override
            protected void populateViewHolder(LikesViewHolder viewHolder, LikesUsernamePhoto model, int position) {

                viewHolder.setProfilePhoto(getApplicationContext(), model.getPhoto());
                viewHolder.setUsername(model.getUsername());


            }
        };

        likesRec.setAdapter(populateRecView);

    }

    public static class LikesViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public LikesViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setProfilePhoto(Context ctx, String photoProfile) {
            ImageView photo_image = (ImageView) mView.findViewById(R.id.profile_image_like);
            Picasso.with(ctx).load(photoProfile).into(photo_image);

        }

        public void setUsername(String username) {

            TextView usernameProfile = (TextView) mView.findViewById(R.id.username_like);
            usernameProfile.setText(username);
        }
    }
}
