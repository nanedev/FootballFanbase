package com.malikbisic.sportapp.activity.firebase;

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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.fragment.firebase.ProfileFragment;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.malikbisic.sportapp.model.firebase.DislikeUsernamPhoto;
import com.squareup.picasso.Picasso;

public class Username_Dislikes_Activity extends AppCompatActivity {

    RecyclerView dislikesRec;
    FirebaseFirestore dislikesReferences;
    Intent myIntent;


    FirebaseFirestore profileUsers;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    Toolbar dislikeToolbar;
    String openActivity = "";
    String postKey = "";

    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username__dislikes_);
        dislikesReferences = FirebaseFirestore.getInstance();
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

        if (isComment) {
            query = FirebaseFirestore.getInstance().collection("DislikesComments").document(post_keyComments).collection("dislike-id");
        } else {

            query = FirebaseFirestore.getInstance().collection("Dislikes").document(post_key).collection("dislike-id");
        }
        profileUsers = FirebaseFirestore.getInstance();
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

        FirestoreRecyclerOptions<DislikeUsernamPhoto> response = new FirestoreRecyclerOptions.Builder<DislikeUsernamPhoto>()
                .setQuery(query, DislikeUsernamPhoto.class)
                .build();

        FirestoreRecyclerAdapter populateRecView = new FirestoreRecyclerAdapter<DislikeUsernamPhoto, DislikeViewHolder>(response) {
            @Override
            public DislikeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.username_dislike_row, parent, false);
                return new DislikeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final DislikeViewHolder viewHolder, int position, DislikeUsernamPhoto model) {
                viewHolder.setProfilePhoto(getApplicationContext(), model.getPhotoProfile());
                viewHolder.setUsername(model.getUsername());

                viewHolder.usernameProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.usernameProfile.getText().toString().trim();
                        Log.i("username", username);

                        profileUsers.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {

                                for (DocumentSnapshot dataSnapshot1 : dataSnapshot.getDocuments()) {

                                    final UsersModel userInfo = dataSnapshot1.toObject(UsersModel.class);

                                    String usernameFirebase = userInfo.getUsername();

                                    if (username.equals(usernameFirebase)) {
                                        final String uid = userInfo.getUserID();
                                        FirebaseUser user1 = mAuth.getCurrentUser();
                                        String myUID = user1.getUid();
                                        Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                        if (uid.equals(myUID)) {

                                           /* ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true"); */

                                            Intent intent = new Intent(Username_Dislikes_Activity.this, ProfileFragment.class);
                                            startActivity(intent);

                                        } else {

                                            DocumentReference profileInfo = profileUsers.collection("Users").document(uid);

                                            profileInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                                    Intent openUserProfile = new Intent(Username_Dislikes_Activity.this, UserProfileActivity.class);
                                                    openUserProfile.putExtra("userID", uid);
                                                    startActivity(openUserProfile);
                                                }
                                            });
                                        }
                                    }
                                }
                            }

                        });
                    }
                });

                viewHolder.photo_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.usernameProfile.getText().toString().trim();
                        Log.i("username", username);

                        profileUsers.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {

                                for (DocumentSnapshot dataSnapshot1 : dataSnapshot.getDocuments()) {

                                    final UsersModel userInfo = dataSnapshot1.toObject(UsersModel.class);

                                    String usernameFirebase = userInfo.getUsername();

                                    if (username.equals(usernameFirebase)) {
                                        final String uid = userInfo.getUserID();
                                        FirebaseUser user1 = mAuth.getCurrentUser();
                                        String myUID = user1.getUid();
                                        Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                        if (uid.equals(myUID)) {

                                           /* ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true"); */

                                            Intent intent = new Intent(Username_Dislikes_Activity.this, ProfileFragment.class);
                                            startActivity(intent);

                                        } else {

                                            DocumentReference profileInfo = profileUsers.collection("Users").document(uid);

                                            profileInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                                    Intent openUserProfile = new Intent(Username_Dislikes_Activity.this, UserProfileActivity.class);
                                                    openUserProfile.putExtra("userID", uid);
                                                    startActivity(openUserProfile);
                                                }
                                            });
                                        }
                                    }
                                }
                            }

                        });
                    }
                });
            }
        };
        dislikesRec.setAdapter(populateRecView);
        populateRecView.notifyDataSetChanged();
        populateRecView.startListening();
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


        if (openActivity.equals("mainPage")) {
            Intent backMainPage = new Intent(Username_Dislikes_Activity.this, MainPage.class);
            startActivity(backMainPage);
            finish();
        } else if (openActivity.equals("commentsActivity")) {
            Intent backComments = new Intent(Username_Dislikes_Activity.this, CommentsActivity.class);
            backComments.putExtra("keyComment", postKey);
            startActivity(backComments);
            finish();
        }

        return super.getParentActivityIntent();

    }

}
