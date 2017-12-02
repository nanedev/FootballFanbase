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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.malikbisic.sportapp.model.Comments;
import com.malikbisic.sportapp.model.CommentsInCommentsModel;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.DislikeUsernamPhoto;
import com.malikbisic.sportapp.model.UsersModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CommentsInComments extends AppCompatActivity implements View.OnClickListener {
    FirebaseFirestore mReference;
    DocumentReference setCommentRef;
    CollectionReference getCommentRef;
    CollectionReference postingDatabase, notificationReference;
    ImageButton sendComment;
    EditText writeComment;
    RecyclerView commentsInComments;
    FirebaseAuth auth;
    Intent myIntent;

    String key;
    String keyNotif;
    String keyPost;
    String keyNotifPush;
    String profileImage;
    String username;
    FirebaseFirestore profileUsers;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_in_comments);
        myIntent = getIntent();
        auth = FirebaseAuth.getInstance();
        mReference = FirebaseFirestore.getInstance();
        sendComment = (ImageButton) findViewById(R.id.sendCommentInComments);
        key = myIntent.getStringExtra("keyComment");
        keyNotif = myIntent.getStringExtra("keyComment3");
        keyNotifPush = myIntent.getStringExtra("post_key");
        keyPost = myIntent.getStringExtra("keyPost");
        profileImage = myIntent.getStringExtra("profileComment");
        username = myIntent.getStringExtra("username");
        profileUsers = FirebaseFirestore.getInstance();

        if (key == null && keyNotifPush != null ){
            key = keyNotifPush;
        }


        Log.i("commentsIn", "comments");
        if (!NotificationFragment.isNotificationClicked) {
            getCommentRef = mReference.collection("CommentsInComments").document(key).collection("reply-id");
        } else  {
            getCommentRef = mReference.collection("CommentsInComments").document(keyNotif).collection("reply");
            NotificationFragment.isNotificationClicked = false;
        }
        postingDatabase = mReference.collection("Posting");
        notificationReference = mReference.collection("Notification");
        writeComment = (EditText) findViewById(R.id.writeCommentInComments);
        commentsInComments = (RecyclerView) findViewById(R.id.rec_view_comments_in_comments);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        final Query query = getCommentRef;

        commentsInComments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        commentsInComments.setLayoutManager(linearLayoutManager);
        sendComment.setOnClickListener(this);

        final FirestoreRecyclerOptions<CommentsInCommentsModel> response = new FirestoreRecyclerOptions.Builder<CommentsInCommentsModel>()
                .setQuery(query, CommentsInCommentsModel.class)
                .build();


        FirestoreRecyclerAdapter populate = new FirestoreRecyclerAdapter<CommentsInCommentsModel, CommentsInCommentsViewHolder>(response) {
            @Override
            public CommentsInCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_in_comments_row, parent, false);
                return new CommentsInCommentsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final CommentsInCommentsViewHolder viewHolder, int position, CommentsInCommentsModel model) {
                final String post_key_comments = getSnapshots().getSnapshot(position).getId();
                viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setTextComment(model.getTextComment());

                viewHolder.profileImageImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.usernameTxt.getText().toString().trim();
                        Log.i("username", username);

                        profileUsers.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {

                                for (DocumentSnapshot dataSnapshot1 : dataSnapshot.getDocuments()) {

                                    final UsersModel userInfo = dataSnapshot1.toObject(UsersModel.class);

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

                                            DocumentReference profileInfo = profileUsers.collection("Users").document(uid);

                                            profileInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                                    Intent openUserProfile = new Intent(CommentsInComments.this, UserProfileActivity.class);
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

                final FirebaseFirestore commentsUsers = FirebaseFirestore.getInstance();
                commentsUsers.collection("CommentsInComments").document(key).collection("reply-id").document(post_key_comments).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot querySnapshot, FirebaseFirestoreException e) {


                        if (querySnapshot.exists()) {
                            if (auth.getCurrentUser().getUid().equals(querySnapshot.getString("uid"))) {
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
                                                    commentsUsers.collection("CommentsInComments").document(key).collection("reply-id").document(post_key_comments).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("deleteReply", e.getLocalizedMessage());
                                                        }
                                                    });
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
                });

            }

        };

        commentsInComments.setAdapter(populate);
        populate.startListening();
        populate.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendCommentInComments) {
            String textComment = writeComment.getText().toString().trim();

            CollectionReference post_comment = FirebaseFirestore.getInstance().collection("CommentsInComments").document(key).collection("reply-id");
            Map<String, Object> commentsMap = new HashMap<>();
            commentsMap.put("textComment", textComment);
            commentsMap.put("profileImage", profileImage);
            commentsMap.put("username", username);
            commentsMap.put("uid", auth.getCurrentUser().getUid());
            post_comment.add(commentsMap);

            FirebaseFirestore getIduserpost = FirebaseFirestore.getInstance();
            getIduserpost.collection("Posting").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                    if (dataSnapshot.exists()) {
                        String userpostUID = dataSnapshot.getString("uid");


                        Map<String, Object> notifMap = new HashMap<>();
                        notifMap.put("action", "coment");
                        notifMap.put("uid", auth.getCurrentUser().getUid());
                        notifMap.put("seen", false);
                        notifMap.put("whatIS", "reply");
                        notifMap.put("post_key", key);
                        CollectionReference notifSet = FirebaseFirestore.getInstance().collection("Notification").document(userpostUID).collection("notif-id");
                        notifSet.add(notifMap);
                    }

                    if (e != null) {
                        Log.e("likeERROR", e.getLocalizedMessage());
                    }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backToComments = new Intent(CommentsInComments.this, CommentsActivity.class);
        backToComments.putExtra("keyComment", keyPost);
        startActivity(backToComments);
    }
}
