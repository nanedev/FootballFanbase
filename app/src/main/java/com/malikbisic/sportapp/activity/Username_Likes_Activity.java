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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.malikbisic.sportapp.model.UsersModel;
import com.malikbisic.sportapp.model.LikesUsernamePhoto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Username_Likes_Activity extends AppCompatActivity {

    RecyclerView likesRec;
    Intent myIntent;
    FirebaseFirestore db;
  FirestoreRecyclerAdapter adapter;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    Toolbar likeToolbar;
    String openActivity = "";
    String postKey = "";
    CollectionReference likesReferences;
    FirebaseAuth mAuth;
   CollectionReference userReference;
   List<UsersModel> list;
    Query query;
    String userId;
    UsersModel usersModel;
    String usernameUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username__likes_);
        myIntent = getIntent();
        likeToolbar = (Toolbar) findViewById(R.id.like_toolbar);
        setSupportActionBar(likeToolbar);
        getSupportActionBar().setTitle("People who liked");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersModel = new UsersModel();
        final String post_key = myIntent.getStringExtra("post_key");
        openActivity = myIntent.getStringExtra("openActivityToBack");
        postKey = myIntent.getStringExtra("keyPost");
        String post_keyComments = myIntent.getStringExtra("post_keyComment");
        boolean isComment = myIntent.getBooleanExtra("isLikeComment", false);
list = new ArrayList<>();
        if (isComment){
            likesReferences = FirebaseFirestore.getInstance().collection("LikeComments").document(post_keyComments).collection("like-id");
        } else {
            likesReferences = FirebaseFirestore.getInstance().collection("Likes").document(post_key).collection("like-id");
        }
     /*   profileUsers = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();*//*
*/
        likesRec = (RecyclerView) findViewById(R.id.rec_view_username_likes);
        likesRec.setHasFixedSize(false);
        likesRec.setLayoutManager(new LinearLayoutManager(this));

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

userId = mAuth.getCurrentUser().getUid();

         query = likesReferences;

                FirestoreRecyclerOptions<LikesUsernamePhoto> response = new FirestoreRecyclerOptions.Builder<LikesUsernamePhoto>()
                .setQuery(query,LikesUsernamePhoto.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LikesUsernamePhoto, LikesViewHolder>(response) {
            @Override
            public LikesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.username_likes_row,parent,false);

                return new LikesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final LikesViewHolder holder, int position, final LikesUsernamePhoto model) {
holder.setProfilePhoto(getApplicationContext(),model.getPhotoProfile());
holder.setUsername(model.getUsername());

holder.mView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        final String username = holder.usernameProfile.getText().toString().trim();
        userReference = db.collection("Users");
        userReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Fail", "Listen failed.", e);
                    return;
                }

                for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()){

                    if (snapshot != null && snapshot.exists()) {

                        String usernameFirestore = String.valueOf(snapshot.getData().get("username"));
                       usersModel = snapshot.toObject(UsersModel.class);

if (username.equals(usernameFirestore)) {
    final String uid = usersModel.getUserID();
    FirebaseUser user1 = mAuth.getCurrentUser();
    String myUID = user1.getUid();
    Log.i("myUID: ", myUID + ", iz baze uid: " + uid);
                            if (uid.equals(myUID)){
                                ProfileFragment profileFragment = new ProfileFragment();

                                FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

                                manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                        R.anim.push_left_out, R.anim.push_left_out).replace(R.id.likes_layout, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                Log.i("tacno", "true");
                            }
                           else {
                                userReference.document(usersModel.getUserID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                                        Intent openUserProfile = new Intent(Username_Likes_Activity.this, UserProfileActivity.class);
                                        openUserProfile.putExtra("userID", uid);
                                        startActivity(openUserProfile);
                                    }
                                });
                            }
                            }

                        }

                    }
                }


        });


    }
});
            }
        };

adapter.notifyDataSetChanged();
likesRec.setAdapter(adapter);
adapter.startListening();
    }

    @Override
    protected void onStart() {

        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    /*
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

        FirestoreRecyclerAdapter<LikesUsernamePhoto, LikesViewHolder> populateRecView = new FirestoreRecyclerAdapter<LikesUsernamePhoto, LikesViewHolder>(
                LikesUsernamePhoto.class,
                R.layout.username_likes_row,
                LikesViewHolder.class,
                likesReferences
        ) {
            @Override
            public LikesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(LikesViewHolder holder, int position, LikesUsernamePhoto model) {

            }

           *//* @Override
            protected void populateViewHolder(final LikesViewHolder viewHolder, LikesUsernamePhoto model, int position) {

                viewHolder.setProfilePhoto(getApplicationContext(), model.getPhoto());
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
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.likes_layout, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true");

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    Intent openUserProfile = new Intent(Username_Likes_Activity.this, UserProfileActivity.class);
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
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.likes_layout, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true");

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {


                                                    Intent openUserProfile = new Intent(Username_Likes_Activity.this, UserProfileActivity.class);
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

            }*//*
        };



        likesRec.setAdapter(populateRecView);

    }*/

    public static class LikesViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView photo_image;
        TextView usernameProfile;

        public LikesViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            photo_image = (ImageView) mView.findViewById(R.id.profile_image_like);
            usernameProfile = (TextView) mView.findViewById(R.id.username_like);
        }

        public void setProfilePhoto(Context ctx, String photoProfile) {
photo_image.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
            Picasso.with(ctx).load(photoProfile).into(photo_image);

        }

        public void setUsername(String username) {

            usernameProfile.setText(username);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","nesto");
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {


        if (openActivity.equals("mainPage")){
            Intent backMainPage = new Intent(Username_Likes_Activity.this, MainPage.class);
            startActivity(backMainPage);
            finish();
        } else if (openActivity.equals("commentsActivity")){
            Intent backComments = new Intent(Username_Likes_Activity.this, CommentsActivity.class);
            backComments.putExtra("keyComment", postKey);
            startActivity(backComments);
            finish();
        }

        return super.getParentActivityIntent();

    }
}
