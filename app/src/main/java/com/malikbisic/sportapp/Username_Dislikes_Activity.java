package com.malikbisic.sportapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Username_Dislikes_Activity extends AppCompatActivity {

    RecyclerView dislikesRec;
    DatabaseReference dislikesReferences;
    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username__dislikes_);

        myIntent = getIntent();
        String post_key = myIntent.getStringExtra("post_key");

        dislikesReferences = FirebaseDatabase.getInstance().getReference().child("Posting").child("Dislikes").child(post_key);

        dislikesRec = (RecyclerView) findViewById(R.id.rec_view_dislike);
        dislikesRec.setHasFixedSize(false);
        dislikesRec.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<DislikeUsernamPhoto, Username_Dislikes_Activity.DislikeViewHolder> populateRecView = new FirebaseRecyclerAdapter<DislikeUsernamPhoto, Username_Dislikes_Activity.DislikeViewHolder>(
                DislikeUsernamPhoto.class,
                R.layout.username_dislike_row,
                Username_Dislikes_Activity.DislikeViewHolder.class,
                dislikesReferences
        ) {


            @Override
            protected void populateViewHolder(Username_Dislikes_Activity.DislikeViewHolder viewHolder, DislikeUsernamPhoto model, int position) {
                viewHolder.setProfilePhoto(getApplicationContext(), model.getPhotoProfile());
                viewHolder.setUsername(model.getUsername());
            }
        };

        dislikesRec.setAdapter(populateRecView);
    }

    public static class DislikeViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public DislikeViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setProfilePhoto(Context ctx, String photoProfile) {
            ImageView photo_image = (ImageView) mView.findViewById(R.id.profile_image_dislike);
            Picasso.with(ctx).load(photoProfile).into(photo_image);

        }

        public void setUsername(String username) {

            TextView usernameProfile = (TextView) mView.findViewById(R.id.username_dislike);
            usernameProfile.setText(username);
        }
    }
}
