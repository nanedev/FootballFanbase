package com.malikbisic.sportapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.malikbisic.sportapp.model.CommentsInCommentsModel;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.UsersModel;
import com.squareup.picasso.Picasso;

public class CommentsInComments extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference setCommentRef, getCommentRef, postingDatabase, notificationReference;
    ImageButton sendComment;
    EditText writeComment;
    RecyclerView commentsInComments;
    FirebaseAuth auth;
    Intent myIntent;

    String key;
    String keyNotif;
    String keyPost;
    String profileImage;
    String username;
    DatabaseReference profileUsers;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_in_comments);
        myIntent = getIntent();
        auth = FirebaseAuth.getInstance();
        sendComment = (ImageButton) findViewById(R.id.sendCommentInComments);
        key = myIntent.getStringExtra("keyComment");
        keyNotif = myIntent.getStringExtra("keyComment3");

        keyPost = myIntent.getStringExtra("keyPost");
        profileImage = myIntent.getStringExtra("profileComment");
        username = myIntent.getStringExtra("username");
        if (!NotificationFragment.isNotificationClicked) {
            getCommentRef = FirebaseDatabase.getInstance().getReference().child("CommentsInComments").child(key);
        } else {
            getCommentRef = FirebaseDatabase.getInstance().getReference().child("CommentsInComments").child(keyNotif);
            NotificationFragment.isNotificationClicked = false;
        }
        postingDatabase = FirebaseDatabase.getInstance().getReference().child("Posting");
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notification");
        writeComment = (EditText) findViewById(R.id.writeCommentInComments);
        commentsInComments = (RecyclerView) findViewById(R.id.rec_view_comments_in_comments);
        profileUsers = FirebaseDatabase.getInstance().getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };


        commentsInComments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        commentsInComments.setLayoutManager(linearLayoutManager);
        sendComment.setOnClickListener(this);

        FirebaseRecyclerAdapter<CommentsInCommentsModel, CommentsInComments.CommentsInCommentsViewHolder> populate = new FirebaseRecyclerAdapter<CommentsInCommentsModel, CommentsInCommentsViewHolder>(
                CommentsInCommentsModel.class,
                R.layout.comments_in_comments_row,
                CommentsInCommentsViewHolder.class,
                getCommentRef
        ) {


            @Override
            protected void populateViewHolder(final CommentsInCommentsViewHolder viewHolder, CommentsInCommentsModel model, int position) {

                final String post_key_comments = getRef(position).getKey();
                viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setTextComment(model.getTextComment());

                viewHolder.profileImageImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.usernameTxt.getText().toString().trim();
                        Log.i("username", username);

                        profileUsers.child("Users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    final UsersModel userInfo = dataSnapshot1.getValue(UsersModel.class);

                                    String usernameFirebase = userInfo.getUsername();

                                    if (username.equals(usernameFirebase)) {
                                        final String uid = userInfo.getUserID();

                                        FirebaseUser user1 = auth.getCurrentUser();
                                        String myUID = user1.getUid();
                                        Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                        if (uid.equals(myUID)) {

                                            ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.commenInCommenstLayout, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true");

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {


                                                    Intent openUserProfile = new Intent(CommentsInComments.this, UserProfileActivity.class);
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

                getCommentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(post_key_comments).child("uid").exists()) {


                            if (auth.getCurrentUser().getUid().equals(dataSnapshot.child(post_key_comments).child("uid").getValue().toString())) {
                                viewHolder.downArrow.setVisibility(View.VISIBLE);

                                viewHolder.downArrow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String[] items = {"Delete comment", "Cancel"};

                                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(viewHolder.mView.getContext());
                                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (items[which].equals("Delete comment")) {
                                                    getCommentRef.child(post_key_comments).removeValue();
                                                } else if (items[which].equals("Cancel")) {

                                                }
                                            }

                                        });
                                        dialog.create();
                                        dialog.show();
                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }


        };



        commentsInComments.setAdapter(populate);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendCommentInComments) {
            String textComment = writeComment.getText().toString().trim();
            setCommentRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference post_comment = setCommentRef.child("CommentsInComments").child(key).push();
            post_comment.child("textComment").setValue(textComment);
            post_comment.child("profileImage").setValue(profileImage);
            post_comment.child("username").setValue(username);
            post_comment.child("uid").setValue(auth.getCurrentUser().getUid());

            DatabaseReference getIduserpost = FirebaseDatabase.getInstance().getReference().child("Comments").child(keyPost);
            getIduserpost.child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userpostUID = String.valueOf(dataSnapshot.child("uid").getValue());

                    DatabaseReference notifSet = notificationReference.child(userpostUID).push();
                    notifSet.child("action").setValue("comment");
                    notifSet.child("uid").setValue(auth.getCurrentUser().getUid());
                    notifSet.child("seen").setValue(false);
                    notifSet.child("whatIS").setValue("reply");
                    notifSet.child("post_key").setValue(key);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            writeComment.setText("");
            hideSoftKeyboard(CommentsInComments.this);
        }
    }

    private void hideSoftKeyboard(CommentsInComments commentsInComments) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) commentsInComments.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                commentsInComments.getCurrentFocus().getWindowToken(), 0);
    }

    public static class CommentsInCommentsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView profileImageImg;
        TextView commentsText;
        ImageView downArrow;
        TextView usernameTxt;
        FirebaseDatabase database;
        FirebaseAuth mAuth;

        public CommentsInCommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            profileImageImg = (ImageView) mView.findViewById(R.id.profileCommentInComment);
            commentsText = (TextView) mView.findViewById(R.id.textCommentInComments);
            downArrow = (ImageView) mView.findViewById(R.id.down_arrow_comments_in_Comments);
            usernameTxt = (TextView) mView.findViewById(R.id.username_comment_profile_in_comment);

            database = FirebaseDatabase.getInstance();


            mAuth = FirebaseAuth.getInstance();

        }

        public void setTextComment(String textComment) {
            commentsText.setText(textComment);
            commentsText.setTextColor(Color.parseColor("#000000"));
            Log.i("comment", textComment);

        }

        public void setProfileImage(Context ctx, String profileImage) {

            Picasso.with(ctx).load(profileImage).into(profileImageImg);
            // Log.i("prp", profileImage);

        }

        public void setUsername(String username) {

            if (username != null) {
                usernameTxt.setText(username);
            } else {
                usernameTxt.setText("");
            }

        }

    }


}
