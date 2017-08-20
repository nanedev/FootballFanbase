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
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.UsersModel;
import com.malikbisic.sportapp.model.Comments;
import com.squareup.picasso.Picasso;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference setCommentRef, getCommentRef;
    ImageButton sendComment;
    EditText writeComment;
    RecyclerView comments;
FirebaseAuth auth;
    Intent myIntent;

    String key;
    String profileImage;
    String username;
    DatabaseReference profileUsers;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    DatabaseReference likesReference, dislikeReference;
    boolean like_process = false;
    boolean dislike_process = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        auth = FirebaseAuth.getInstance();
        myIntent = getIntent();
        key = myIntent.getStringExtra("keyComment");
        profileImage = myIntent.getStringExtra("profileComment");
        username = myIntent.getStringExtra("username");
        profileUsers = FirebaseDatabase.getInstance().getReference();

        likesReference = FirebaseDatabase.getInstance().getReference().child("LikesComments");
        dislikeReference = FirebaseDatabase.getInstance().getReference().child("DislikesComments");
        likesReference.keepSynced(true);
        dislikeReference.keepSynced(true);

        getCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(key);
        Log.i("key", key);


        sendComment = (ImageButton) findViewById(R.id.sendComment);
        writeComment = (EditText) findViewById(R.id.writeComment);
        comments = (RecyclerView) findViewById(R.id.rec_view_comments);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        comments.setLayoutManager(linearLayoutManager);

        sendComment.setOnClickListener(this);
        FirebaseRecyclerAdapter<Comments, CommentsActivity.CommentsViewHolder> populateComment = new FirebaseRecyclerAdapter<Comments, CommentsActivity.CommentsViewHolder>(
                Comments.class,
                R.layout.comments_wall,
                CommentsViewHolder.class,
                getCommentRef) {
            @Override
            protected void populateViewHolder(final CommentsViewHolder viewHolder, final Comments model, int position) {
                final String post_key_comments = getRef(position).getKey();

                viewHolder.setTextComment(model.getTextComment());
                viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(post_key_comments);
                viewHolder.setDislikeBtn(post_key_comments);
                viewHolder.setNumberLikes(post_key_comments);
                viewHolder.setNumberDislikes(post_key_comments);
                viewHolder.setNumberComments(post_key_comments);

                viewHolder.likeCommentsImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        like_process = true;


                        likesReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (like_process) {
                                    if (dataSnapshot.child(post_key_comments).hasChild(auth.getCurrentUser().getUid())) {

                                        likesReference.child(post_key_comments).child(auth.getCurrentUser().getUid()).removeValue();
                                        like_process = false;


                                    } else {

                                        DatabaseReference newPost = likesReference.child(post_key_comments).child(auth.getCurrentUser().getUid());

                                        newPost.child("username").setValue(MainPage.usernameInfo);
                                        newPost.child("photoProfile").setValue(MainPage.profielImage);
                                        like_process = false;


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.i("error", databaseError.getMessage());

                            }
                        });

                        viewHolder.dislikeCommentImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dislike_process = true;
                                dislikeReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dislike_process) {
                                            if (dataSnapshot.child(post_key_comments).hasChild(auth.getCurrentUser().getUid())) {

                                                dislikeReference.child(post_key_comments).child(auth.getCurrentUser().getUid()).removeValue();
                                                dislike_process = false;


                                            } else {

                                                DatabaseReference newPost = dislikeReference.child(post_key_comments).child(auth.getCurrentUser().getUid());

                                                newPost.child("username").setValue(MainPage.usernameInfo);
                                                newPost.child("photoProfile").setValue(MainPage.profielImage);

                                                dislike_process = false;


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
                });


                viewHolder.numberLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent listUsername = new Intent(CommentsActivity.this, Username_Likes_Activity.class);
                        listUsername.putExtra("post_keyComment", post_key_comments);
                        listUsername.putExtra("isLikeComment", true);
                        startActivity(listUsername);
                    }
                });

                viewHolder.numberDislikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent listUsername = new Intent(CommentsActivity.this, Username_Dislikes_Activity.class);
                        listUsername.putExtra("post_keyComment", post_key_comments);
                        listUsername.putExtra("isDislikeComment", true);
                        startActivity(listUsername);
                    }
                });



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
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.commentLayout, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true");

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {


                                                    Intent openUserProfile = new Intent(CommentsActivity.this, UserProfileActivity.class);
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
                viewHolder.commentSomething.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CommentsActivity.this,CommentsInComments.class);
                        intent.putExtra("keyComment", post_key_comments);
                        intent.putExtra("profileComment", model.getProfileImage());
                        intent.putExtra("username", model.getUsername());
                        startActivity(intent);
                    }
                });

                viewHolder.commentsReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CommentsActivity.this,CommentsInComments.class);
                        intent.putExtra("keyComment", post_key_comments);
                        intent.putExtra("profileComment", model.getProfileImage());
                        intent.putExtra("username", model.getUsername());
                        startActivity(intent);
                    }
                });

                viewHolder.commentsReplyNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CommentsActivity.this,CommentsInComments.class);
                        intent.putExtra("keyComment", post_key_comments);
                        intent.putExtra("profileComment", model.getProfileImage());
                        intent.putExtra("username", model.getUsername());
                        startActivity(intent);
                    }
                });

            }
        };
    comments.setAdapter(populateComment);
    }


    @Override
    protected void onStart() {

        super.onStart();

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendComment){
            String textComment = writeComment.getText().toString().trim();
            setCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(key).push();
           DatabaseReference post_comment = setCommentRef ;
            post_comment.child("textComment").setValue(textComment);
            post_comment.child("profileImage").setValue(profileImage);
            post_comment.child("username").setValue(username);
            post_comment.child("uid").setValue(auth.getCurrentUser().getUid());

            writeComment.setText("");
            hideSoftKeyboard(CommentsActivity.this);

        }
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        View mView;
TextView commentSomething;
        ImageView profileImageImg;
        TextView commentsText;
        ImageView downArrow;
        TextView usernameTxt;
        FirebaseDatabase database;
        ImageView likeCommentsImg;
        ImageView dislikeCommentImg;
        DatabaseReference likeReference, dislikeReference, numberCommentsReference;
        FirebaseAuth mAuth;
        TextView numberLikes;
        TextView numberDislikes;
        TextView commentsReply;
        TextView commentsReplyNumber;
        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            profileImageImg = (ImageView) mView.findViewById(R.id.profileComment);
            commentsText = (TextView) mView.findViewById(R.id.textComment);
            downArrow = (ImageView) mView.findViewById(R.id.down_arrow_comments);
            usernameTxt = (TextView) mView.findViewById(R.id.username_comment_profile);
            likeCommentsImg = (ImageView)  mView.findViewById(R.id.likecommentsimage);
            database = FirebaseDatabase.getInstance();
            dislikeCommentImg = (ImageView) mView.findViewById(R.id.dislikecommentsimage);
            numberLikes = (TextView) mView.findViewById(R.id.number_of_likes_comments);
            numberDislikes = (TextView) mView.findViewById(R.id.number_of_dislikes_comments);
            commentSomething = (TextView) mView.findViewById(R.id.comment_something_comments);
            commentsReply = (TextView) mView.findViewById(R.id.commentsInComments_textview);
            commentsReplyNumber = (TextView) mView.findViewById(R.id.number_commentsInComments);
            mAuth = FirebaseAuth.getInstance();
            likeReference = database.getReference().child("LikesComments");
            dislikeReference = database.getReference().child("DislikesComments");
            numberCommentsReference = database.getReference().child("CommentsInComments");
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

        public void setUsername(String username){

            if (username != null) {
                usernameTxt.setText(username);
            } else {
                usernameTxt.setText("");
            }

        }

        public void setLikeBtn(final String post_key) {
            likeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                        dislikeCommentImg.setClickable(false);
                        likeCommentsImg.setActivated(true);

                    } else {
                        dislikeCommentImg.setClickable(true);
                        likeCommentsImg.setActivated(false);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setDislikeBtn(final String post_key) {
            dislikeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                        dislikeCommentImg.setActivated(true);
                        likeCommentsImg.setClickable(false);

                    } else {
                        dislikeCommentImg.setActivated(false);
                        likeCommentsImg.setClickable(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setNumberLikes(String post_key) {

            likeReference.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    long numberLikesNumber = dataSnapshot.getChildrenCount();
                    if (numberLikesNumber == 0) {
                        numberLikes.setText("");
                    } else {
                        numberLikes.setText(String.valueOf(numberLikesNumber));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setNumberDislikes(String post_key) {

            dislikeReference.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.i("fffff", String.valueOf(dataSnapshot.getChildrenCount()));
                    long numberDislikesNumber = dataSnapshot.getChildrenCount();
                    if (numberDislikesNumber == 0) {
                        numberDislikes.setText("");
                    } else {
                        numberDislikes.setText(String.valueOf(numberDislikesNumber));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setNumberComments(String post_key) {

            numberCommentsReference.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    long numberOfComments = dataSnapshot.getChildrenCount();

                    if (numberOfComments == 0) {

                        commentsReply.setVisibility(View.GONE);
                        commentsReplyNumber.setText("");
                    } else if (numberOfComments == 1){

                        commentsReply.setText("Reply");
                        commentsReplyNumber.setText(String.valueOf(numberOfComments));
                    } else {
                        commentsReply.setText("Replies");
                        commentsReplyNumber.setText(String.valueOf(numberOfComments));
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
