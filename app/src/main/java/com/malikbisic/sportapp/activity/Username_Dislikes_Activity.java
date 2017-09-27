package com.malikbisic.sportapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.UsersModel;
import com.malikbisic.sportapp.model.DislikeUsernamPhoto;
import com.squareup.picasso.Picasso;

public class Username_Dislikes_Activity extends AppCompatActivity {

    RecyclerView dislikesRec;
    DatabaseReference dislikesReferences;
    Intent myIntent;


    DatabaseReference profileUsers;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    Toolbar dislikeToolbar;
    String openActivity = "";
    String postKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username__dislikes_);

        myIntent = getIntent();
        dislikeToolbar = (Toolbar) findViewById(R.id.dislike_toolbar);
        setSupportActionBar(dislikeToolbar);
        getSupportActionBar().setTitle("People who disliked");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String post_key = myIntent.getStringExtra("post_key");
        openActivity = myIntent.getStringExtra("openActivityToBack");
        postKey = myIntent.getStringExtra("keyPost");
        String post_keyComments = myIntent.getStringExtra("post_keyComment");
        boolean isComment = myIntent.getBooleanExtra("isDislikeComment", false);

        if (isComment){
            dislikesReferences = FirebaseDatabase.getInstance().getReference().child("DislikesComments").child(post_keyComments);
        } else {

            dislikesReferences = FirebaseDatabase.getInstance().getReference().child("Dislikes").child(post_key);
        }
        profileUsers = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        dislikesRec = (RecyclerView) findViewById(R.id.rec_view_dislike);
        dislikesRec.setHasFixedSize(false);
        dislikesRec.setLayoutManager(new LinearLayoutManager(this));

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

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
            protected void populateViewHolder(final Username_Dislikes_Activity.DislikeViewHolder viewHolder, DislikeUsernamPhoto model, int position) {
                viewHolder.setProfilePhoto(getApplicationContext(), model.getPhotoProfile());
                viewHolder.setUsername(model.getUsername());

                viewHolder.usernameProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.usernameProfile.getText().toString().trim();
                        Log.i("username", username);

                        profileUsers.child("Users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    final UsersModel userInfo = dataSnapshot1.getValue(UsersModel.class);

                                    String usernameFirebase = userInfo.getUsername();

                                    if (username.equals(usernameFirebase)) {
                                        final String uid = userInfo.getUserID();

                                        FirebaseUser user1 = mAuth.getCurrentUser();
                                        String myUID = user1.getUid();
                                        Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                        if (uid.equals(myUID)) {

                                            ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.dislike_layout, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true");

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    Intent openUserProfile = new Intent(Username_Dislikes_Activity.this, UserProfileActivity.class);
                                                    openUserProfile.putExtra("userID", uid);
                                                    startActivity(openUserProfile);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.photo_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.usernameProfile.getText().toString().trim();
                        Log.i("username", username);

                        profileUsers.child("Users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    final UsersModel userInfo = dataSnapshot1.getValue(UsersModel.class);

                                    String usernameFirebase = userInfo.getUsername();

                                    if (username.equals(usernameFirebase)) {
                                        final String uid = userInfo.getUserID();

                                        FirebaseUser user1 = mAuth.getCurrentUser();
                                        String myUID = user1.getUid();
                                        Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                        if (uid.equals(myUID)) {

                                            ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.dislike_layout, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true");

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {


                                                    Intent openUserProfile = new Intent(Username_Dislikes_Activity.this, UserProfileActivity.class);
                                                    openUserProfile.putExtra("userID", uid);
                                                    startActivity(openUserProfile);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        dislikesRec.setAdapter(populateRecView);
    }

    public static class DislikeViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView photo_image;
        TextView usernameProfile;

        public DislikeViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
             photo_image = (ImageView) mView.findViewById(R.id.profile_image_dislike);
            usernameProfile = (TextView) mView.findViewById(R.id.username_dislike);

        }

        public void setProfilePhoto(Context ctx, String photoProfile) {

            Picasso.with(ctx).load(photoProfile).into(photo_image);

        }

        public void setUsername(String username) {


            usernameProfile.setText(username);
        }
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {


        if (openActivity.equals("mainPage")){
            Intent backMainPage = new Intent(Username_Dislikes_Activity.this, MainPage.class);
            startActivity(backMainPage);
            finish();
        } else if (openActivity.equals("commentsActivity")){
            Intent backComments = new Intent(Username_Dislikes_Activity.this, CommentsActivity.class);
            backComments.putExtra("keyComment", postKey);
            startActivity(backComments);
            finish();
        }

        return super.getParentActivityIntent();

    }

}
