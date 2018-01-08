package com.malikbisic.sportapp.activity.firebase;

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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.fragment.firebase.NotificationFragment;
import com.malikbisic.sportapp.fragment.firebase.ProfileFragment;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.malikbisic.sportapp.model.firebase.Comments;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference setCommentRef;
    ImageButton sendComment;
    EditText writeComment;
    RecyclerView comments;
    FirebaseAuth auth;
    Intent myIntent;
    FirebaseFirestore mReference;
    CollectionReference getCommentRef;
    CollectionReference postingDatabase, notificationReference;
    FirebaseFirestore replyRef;

    public static String key;
    String keyNotif;
    String profileImage;
    String username;
    String keyNotifPush;

    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseFirestore profileUsers;

    FirebaseFirestore likesReference, dislikeReference;
    boolean like_process = false;
    boolean dislike_process = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        auth = FirebaseAuth.getInstance();
        myIntent = getIntent();
        key = myIntent.getStringExtra("keyComment");
        keyNotif = myIntent.getStringExtra("keyComment2");
        profileImage = myIntent.getStringExtra("profileComment");
        keyNotifPush = myIntent.getStringExtra("post_key");
        username = myIntent.getStringExtra("username");
        profileUsers = FirebaseFirestore.getInstance();
        postingDatabase = profileUsers.collection("Posting");
        //     notificationReference = mReference.collection("Notification");
        replyRef = FirebaseFirestore.getInstance();
        likesReference = FirebaseFirestore.getInstance();
        dislikeReference = FirebaseFirestore.getInstance();
        mReference = FirebaseFirestore.getInstance();


        if (key == null && keyNotifPush != null) {
            key = keyNotifPush;
        } else if (key != null) {
            keyNotif = key;
        }

        if (key == null && keyNotif != null) {
            key = keyNotif;
        }


        if (!NotificationFragment.isNotificationClicked) {
            getCommentRef = mReference.collection("Comments").document(key).collection("comment-id");

            keyNotif = key;
        } else {
            try {
                getCommentRef = mReference.collection("Comments").document(keyNotif).collection("comment-id");
            } catch (Exception e) {
                Log.e("error", e.getLocalizedMessage());
            }
            key = keyNotif;
            NotificationFragment.isNotificationClicked = false;
        }


        sendComment = (ImageButton) findViewById(R.id.sendComment);
        writeComment = (EditText) findViewById(R.id.writeComment);
        comments = (RecyclerView) findViewById(R.id.rec_view_comments);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        final Query query = getCommentRef;

        comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        comments.setLayoutManager(linearLayoutManager);

        sendComment.setOnClickListener(this);

        final FirestoreRecyclerOptions<Comments> response = new FirestoreRecyclerOptions.Builder<Comments>()
                .setQuery(query, Comments.class)
                .build();


        FirestoreRecyclerAdapter populate = new FirestoreRecyclerAdapter<Comments, CommentsActivity.CommentsViewHolder>(response) {
            @Override
            public CommentsActivity.CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_wall, parent, false);
                return new CommentsActivity.CommentsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final CommentsActivity.CommentsViewHolder viewHolder, int position, Comments model) {
                final String post_key_comments = getSnapshots().getSnapshot(position).getId();
                viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setTextComment(model.getTextComment());
                viewHolder.setLikeBtn(post_key_comments, CommentsActivity.this);
                viewHolder.setDislikeBtn(post_key_comments, CommentsActivity.this);
                viewHolder.setNumberLikes(post_key_comments, CommentsActivity.this);
                viewHolder.setNumberDislikes(post_key_comments, CommentsActivity.this);
                viewHolder.setNumberComments(post_key_comments, CommentsActivity.this);
                final String uid = auth.getCurrentUser().getUid();


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

                                           /* ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true"); */

                                            Intent intent = new Intent(CommentsActivity.this, ProfileFragment.class);
                                            startActivity(intent);

                                        } else {

                                            DocumentReference profileInfo = profileUsers.collection("Users").document(uid);

                                            profileInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                                    Intent openUserProfile = new Intent(CommentsActivity.this, UserProfileActivity.class);
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
                commentsUsers.collection("Comments").document(key).collection("comment-id").document(post_key_comments).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                                                    commentsUsers.collection("Comments").document(key).collection("comment-id").document(post_key_comments).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            likesReference.collection("LikeComments").document(post_key_comments).collection("like-id").document(auth.getCurrentUser().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });


                                                            dislikeReference.collection("DislikesComments").document(post_key_comments).collection("dislike-id").document(auth.getCurrentUser().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });


                                                            replyRef.collection("CommentsInComments").document(post_key_comments).collection("reply-id").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    for (DocumentSnapshot snapshot : task.getResult()){
                                                                        String id = snapshot.getId();
                                                                        replyRef.collection("CommentsInComments").document(post_key_comments).collection("reply-id").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.e("errorRepyELETE", e.getLocalizedMessage());
                                                                            }
                                                                        }); //jjjnb
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("deleteComment", e.getLocalizedMessage());
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


                viewHolder.likeComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        like_process = true;
                        viewHolder.setNumberLikes(post_key_comments, CommentsActivity.this);
                        likesReference.collection("LikeComments").document(post_key_comments).collection("like-id").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {

                                if (like_process) {


                                    if (snapshot.exists()) {

                                        likesReference.collection("LikeComments").document(post_key_comments).collection("like-id").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.i("deleteLike", "complete");

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("deleteLike", e.getLocalizedMessage());
                                            }
                                        });
                                        like_process = false;

                                        Log.i("nema like", "NEMA");

                                    } else {
                                        Log.i("ima like", "IMA");
                                        Map<String, Object> userLikeInfo = new HashMap<>();
                                        userLikeInfo.clear();
                                        userLikeInfo.put("username", MainPage.usernameInfo);
                                        userLikeInfo.put("photoProfile", MainPage.profielImage);
                                        userLikeInfo.put("timestamp", FieldValue.serverTimestamp());

                                        final DocumentReference newPost = likesReference.collection("LikeComments").document(post_key_comments).collection("like-id").document(uid);
                                        newPost.set(userLikeInfo);


                                        CollectionReference getIduserpost = FirebaseFirestore.getInstance().collection("Comments").document(key).collection("comment-id");
                                        getIduserpost.document(post_key_comments).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                                if (dataSnapshot.exists()) {
                                                    String userpostUID = dataSnapshot.getString("uid");

                                                    Map<String, Object> notifMap = new HashMap<>();
                                                    notifMap.put("action", "liked");
                                                    notifMap.put("uid", uid);
                                                    notifMap.put("seen", false);
                                                    notifMap.put("whatIS", "comment");
                                                    notifMap.put("post_key", key);
                                                    notifMap.put("timestamp", FieldValue.serverTimestamp());

                                                    if (!userpostUID.equals(uid)) {
                                                        CollectionReference notifSet = FirebaseFirestore.getInstance().collection("Notification").document(userpostUID).collection("notif-id");
                                                        notifSet.add(notifMap);
                                                    }


                                                    if (e != null) {
                                                        Log.e("likeERROR", e.getLocalizedMessage());
                                                    }
                                                }
                                            }


                                        });


                                        like_process = false;


                                    }
                                }
                            }
                        });

                    }
                });
                viewHolder.dislikeComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dislike_process = true;
                        viewHolder.setNumberDislikes(post_key_comments, CommentsActivity.this);


                        dislikeReference.collection("DislikesComments").document(post_key_comments).collection("dislike-id").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                                if (dislike_process) {


                                    if (documentSnapshots.exists()) {
                                        dislikeReference.collection("DislikesComments").document(post_key_comments).collection("dislike-id").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.i("deleteDislike", "complete");

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("deletedislike", e.getLocalizedMessage());
                                            }
                                        });
                                        dislike_process = false;


                                    } else {

                                        Map<String, Object> userDislikeInfo = new HashMap<>();
                                        userDislikeInfo.put("username", MainPage.usernameInfo);
                                        userDislikeInfo.put("photoProfile", MainPage.profielImage);
                                        userDislikeInfo.put("timestamp", FieldValue.serverTimestamp());

                                        final DocumentReference newPost = dislikeReference.collection("DislikesComments").document(post_key_comments).collection("dislike-id").document(uid);
                                        newPost.set(userDislikeInfo);


                                        CollectionReference getIduserpost = FirebaseFirestore.getInstance().collection("Comments").document(key).collection("comment-id");
                                        getIduserpost.document(post_key_comments).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                                if (dataSnapshot.exists()) {
                                                    String userpostUID = dataSnapshot.getString("uid");

                                                    Map<String, Object> notifMap = new HashMap<>();
                                                    notifMap.put("action", "disliked");
                                                    notifMap.put("uid", uid);
                                                    notifMap.put("seen", false);
                                                    notifMap.put("whatIS", "comment");
                                                    notifMap.put("post_key", key);
                                                    notifMap.put("timestamp", FieldValue.serverTimestamp());
                                                    if (!userpostUID.equals(uid)) {
                                                        CollectionReference notifSet = FirebaseFirestore.getInstance().collection("Notification").document(userpostUID).collection("notif-id");
                                                        notifSet.add(notifMap);
                                                    }
                                                }

                                            }

                                        });


                                        dislike_process = false;


                                    }
                                }
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
                        if (key != null) {
                            listUsername.putExtra("keyPost", key);
                        } else if (keyNotif != null) {
                            listUsername.putExtra("keyPost", keyNotif);
                        }
                        listUsername.putExtra("openActivityToBack", "commentsActivity");
                        startActivity(listUsername);
                    }
                });

                viewHolder.numberDislikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent listUsername = new Intent(CommentsActivity.this, Username_Dislikes_Activity.class);
                        listUsername.putExtra("post_keyComment", post_key_comments);
                        listUsername.putExtra("isDislikeComment", true);
                        if (key != null) {
                            listUsername.putExtra("keyPost", key);
                        } else if (keyNotif != null) {
                            listUsername.putExtra("keyPost", keyNotif);
                        }
                        listUsername.putExtra("openActivityToBack", "commentsActivity");
                        startActivity(listUsername);
                    }
                });
                viewHolder.likeBtnImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent listUsername = new Intent(CommentsActivity.this, Username_Likes_Activity.class);
                        listUsername.putExtra("post_keyComment", post_key_comments);
                        listUsername.putExtra("isLikeComment", true);
                        if (key != null) {
                            listUsername.putExtra("keyPost", key);
                        } else if (keyNotif != null) {
                            listUsername.putExtra("keyPost", keyNotif);
                        }
                        listUsername.putExtra("openActivityToBack", "commentsActivity");
                        startActivity(listUsername);
                    }
                });


                viewHolder.dislikeBtnImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent listUsername = new Intent(CommentsActivity.this, Username_Dislikes_Activity.class);
                        listUsername.putExtra("post_keyComment", post_key_comments);
                        listUsername.putExtra("isDislikeComment", true);
                        if (key != null) {
                            listUsername.putExtra("keyPost", key);
                        } else if (keyNotif != null) {
                            listUsername.putExtra("keyPost", keyNotif);
                        }
                        listUsername.putExtra("openActivityToBack", "commentsActivity");
                        startActivity(listUsername);
                    }
                });


                viewHolder.commentSomething.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CommentsActivity.this, CommentsInComments.class);
                        intent.putExtra("keyComment", post_key_comments);
                        if (key != null) {
                            intent.putExtra("keyPost", key);
                        } else if (keyNotif != null) {
                            intent.putExtra("keyPost", keyNotif);
                        }
                        intent.putExtra("profileComment", MainPage.profielImage);
                        intent.putExtra("username", MainPage.usernameInfo);
                        startActivity(intent);
                    }
                });


                viewHolder.commentsReplyNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CommentsActivity.this, CommentsInComments.class);
                        intent.putExtra("keyComment", post_key_comments);
                        if (key != null) {
                            intent.putExtra("keyPost", key);
                        } else if (keyNotif != null) {
                            intent.putExtra("keyPost", keyNotif);
                        }
                        intent.putExtra("profileComment", MainPage.profielImage);
                        intent.putExtra("username", MainPage.usernameInfo);
                        startActivity(intent);
                    }
                });


            }

        };
        comments.setAdapter(populate);
        populate.startListening();
        populate.notifyDataSetChanged();

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
        if (view.getId() == R.id.sendComment) {
            String textComment = writeComment.getText().toString().trim();
            CollectionReference post_comment = FirebaseFirestore.getInstance().collection("Comments").document(key).collection("comment-id");
            Map<String, Object> commentsMap = new HashMap<>();
            commentsMap.put("textComment", textComment);
            commentsMap.put("profileImage", profileImage);
            commentsMap.put("username", username);
            commentsMap.put("uid", auth.getCurrentUser().getUid());
            post_comment.add(commentsMap);

            FirebaseFirestore getIduserpost = FirebaseFirestore.getInstance();
            getIduserpost.collection("Posting").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {

                    if (snapshot.exists()) {
                        String userpostUID = snapshot.getString("uid");


                        Map<String, Object> notifMap = new HashMap<>();
                        notifMap.put("action", "coment");
                        notifMap.put("uid", auth.getCurrentUser().getUid());
                        notifMap.put("seen", false);
                        notifMap.put("whatIS", "post");
                        notifMap.put("post_key", key);
                        notifMap.put("timestamp", FieldValue.serverTimestamp());
                        if (!userpostUID.equals(auth.getCurrentUser().getUid())) {
                            CollectionReference notifSet = FirebaseFirestore.getInstance().collection("Notification").document(userpostUID).collection("notif-id");
                            notifSet.add(notifMap);
                        }
                    }

                    if (e != null) {
                        Log.e("likeERROR", e.getLocalizedMessage());
                    }
                }
            });

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
        FirebaseFirestore database;
        TextView likeComments;
        ImageView likeBtnImage;
        TextView dislikeComment;
        ImageView dislikeBtnImage;
        CollectionReference likeReference, dislikeReference, numberCommentsReference;
        FirebaseAuth mAuth;
        TextView numberLikes;
        TextView numberDislikes;

        TextView commentsReplyNumber;
        int numberLikesInt;
        int numberDislikesInt;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            profileImageImg = (ImageView) mView.findViewById(R.id.profileComment);
            commentsText = (TextView) mView.findViewById(R.id.minuteText);
            downArrow = (ImageView) mView.findViewById(R.id.down_arrow_comments);
            usernameTxt = (TextView) mView.findViewById(R.id.username_comment_profile);
            likeComments = (TextView) mView.findViewById(R.id.like_comments_wall);
            database = FirebaseFirestore.getInstance();
            dislikeComment = (TextView) mView.findViewById(R.id.dislike_comments_wall);
            likeBtnImage = (ImageView) mView.findViewById(R.id.likecommentsimage);
            numberLikes = (TextView) mView.findViewById(R.id.number_of_likes_comments);
            numberDislikes = (TextView) mView.findViewById(R.id.number_of_dislikes_comments);
            commentSomething = (TextView) mView.findViewById(R.id.comment_something_comments);
            commentsReplyNumber = (TextView) mView.findViewById(R.id.number_commentsInComments);
            dislikeBtnImage = (ImageView) mView.findViewById(R.id.dislikecommentsimage);
            mAuth = FirebaseAuth.getInstance();
            likeReference = database.collection("LikeComments");
            dislikeReference = database.collection("DislikesComments");
            numberCommentsReference = database.collection("CommentsInComments");

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

        public void setLikeBtn(final String post_key, Activity activity) {
            String uid = mAuth.getCurrentUser().getUid();
            Log.i("uid", uid);
            final DocumentReference doc = likeReference.document(post_key).collection("like-id").document(uid);
            doc.addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {

                        dislikeComment.setClickable(false);
                        likeComments.setActivated(true);

                    } else {
                        dislikeComment.setClickable(true);
                        likeComments.setActivated(false);
                        Log.i("key like nema ", post_key);
                    }

                }
            });
        }

        public void setDislikeBtn(final String post_key, Activity activity) {
            String uid = mAuth.getCurrentUser().getUid();
            final DocumentReference doc = dislikeReference.document(post_key).collection("dislike-id").document(uid);
            doc.addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                    if (snapshot.exists()) {
                        likeComments.setClickable(false);
                        dislikeComment.setActivated(true);


                    } else {
                        likeComments.setClickable(true);
                        dislikeComment.setActivated(false);
                        Log.i("key dislike nema ", post_key);
                    }
                }
            });

        }

        public void setNumberLikes(String post_key, Activity activity) {
            CollectionReference col = likeReference.document(post_key).collection("like-id");
            col.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (e == null) {

                        numberLikesInt = documentSnapshots.size();


                        if (numberLikesInt == 0) {
                            numberLikes.setText("");
                            likeBtnImage.setVisibility(View.GONE);
                        } else {
                            numberLikes.setText(String.valueOf(numberLikesInt));
                            likeBtnImage.setVisibility(View.VISIBLE);
                            numberLikes.setVisibility(View.VISIBLE);
                        }
                    }
                }

            });
        }

        public void setNumberDislikes(String post_key, Activity activity) {

            CollectionReference col = dislikeReference.document(post_key).collection("dislike-id");
            col.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (e == null) {
                        numberDislikesInt = documentSnapshots.size();


                        if (numberDislikesInt == 0) {
                            numberDislikes.setText("");
                            dislikeBtnImage.setVisibility(View.GONE);
                        } else {
                            numberDislikes.setVisibility(View.VISIBLE);
                            numberDislikes.setText(String.valueOf(numberDislikesInt));
                            dislikeBtnImage.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }

        public void setNumberComments(String post_key, Activity activity) {

            numberCommentsReference.document(post_key).collection("reply-id").addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (e == null) {
                        long numberOfComments = documentSnapshots.size();

                        if (numberOfComments == 0) {

                            commentsReplyNumber.setText("");
                        } else if (numberOfComments == 1) {

                            commentSomething.setText("Reply");
                            commentsReplyNumber.setText(String.valueOf(numberOfComments));
                        } else {
                            commentSomething.setText("Replies");
                            commentsReplyNumber.setText(String.valueOf(numberOfComments));

                        }
                    }
                }
            });


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent backMainPage = new Intent(CommentsActivity.this, MainPage.class);
        startActivity(backMainPage);
        finish();
    }
}
