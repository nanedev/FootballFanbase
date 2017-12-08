package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class FullScreenImage extends AppCompatActivity {
    FirebaseFirestore db;
    CollectionReference likeRef;
    CollectionReference dislikeRef;
    CollectionReference commentsRef;
    Intent intent;
    String postKey;
    String uri;
  ImageView numberLikeImage;
  ImageView numberDislikeImage;
FirebaseFirestore likesReference;
    TextView numberLikesTextview;
    TextView numberDislikesTextview;
    TextView numberCommentsTextivew;
    RelativeLayout numberLikesLayout;
    RelativeLayout numberDislikesLayout;
    RelativeLayout numberCommentsLayout;
    FirebaseAuth mAuth;
  ImageView likeFullscreenImage;
  ImageView dislikeFullscreenImage;
  ImageView commentFullscreenImage;
    boolean like_process = false;
    boolean dislike_process = false;
    FirebaseFirestore postingDatabase;
    FirebaseFirestore notificationReference;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        likesReference = FirebaseFirestore.getInstance();
        notificationReference = FirebaseFirestore.getInstance();
        numberLikesTextview = (TextView) findViewById(R.id.likesinfullscreen);
        numberDislikesTextview = (TextView) findViewById(R.id.dislikesFullscreen);
        numberCommentsTextivew = (TextView) findViewById(R.id.numberCommentsFullscreen);
        likeFullscreenImage = (ImageView)findViewById(R.id.likeImageFullscreen);
        dislikeFullscreenImage = (ImageView) findViewById(R.id.dislikeImageFullscreen);
        commentFullscreenImage = (ImageView)findViewById(R.id.commentFullscreenimage);
postingDatabase = FirebaseFirestore.getInstance();
        intent = getIntent();
        postKey = intent.getStringExtra("postKey");
        ImageView image = (ImageView) findViewById(R.id.fullScreenImageView);
        uri = intent.getStringExtra("imageURL");
        Glide.with(this).load(uri).into(image);
        numberLikesLayout = (RelativeLayout) findViewById(R.id.likeslayout);
        numberDislikesLayout = (RelativeLayout)findViewById(R.id.dislikeslayout);
        numberCommentsLayout = (RelativeLayout) findViewById(R.id.comentslayout);
uid = mAuth.getCurrentUser().getUid();

        likeRef = db.collection("Likes").document(postKey).collection("like-id");
        likeRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int numberLikes = documentSnapshots.size();
                if (numberLikes == 0) {
                    numberLikesTextview.setText("");
                    numberLikesLayout.setVisibility(View.GONE);
                } else {
                    numberLikesTextview.setText(String.valueOf(numberLikes));
                }

            }
        });

        dislikeRef = db.collection("Dislikes").document(postKey).collection("dislike-id");
        dislikeRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                int numberDislikes = documentSnapshots.size();

                if (numberDislikes == 0) {
                    numberDislikesTextview.setText("");
                    numberDislikesLayout.setVisibility(View.GONE);
                } else {
                    numberDislikesTextview.setText(String.valueOf(numberDislikes));
                }


            }
        });

        commentsRef = db.collection("Comments").document(postKey).collection("comment-id");
        commentsRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int numberComments = documentSnapshots.size();

                if (numberComments == 0){

                    numberCommentsLayout.setVisibility(View.GONE);
                }else {
                    numberCommentsLayout.setVisibility(View.VISIBLE);
                    numberCommentsTextivew.setText(String.valueOf(numberComments));
                }
            }
        });

        numberLikesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(FullScreenImage.this,Username_Likes_Activity.class);
                intent.putExtra("post_key",postKey);
                startActivity(intent);
            }
        });
        numberDislikesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(FullScreenImage.this,Username_Dislikes_Activity.class);
                intent.putExtra("post_key",postKey);
                startActivity(intent);
            }
        });


        numberCommentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScreenImage.this,CommentsActivity.class);
                intent.putExtra("keyComment", postKey);
                intent.putExtra("profileComment", MainPage.profielImage);
                intent.putExtra("username", MainPage.usernameInfo);
                startActivity(intent);
            }
        });


        likeFullscreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeFullscreenImage.setImageDrawable(getResources().getDrawable(R.drawable.likesomethingfull));
                dislikeFullscreenImage.setActivated(false);

                like_process = true;



                likesReference.collection("Likes").document(postKey).collection("like-id").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (like_process) {


                            if (documentSnapshots.exists()) {

                                likesReference.collection("Likes").document(postKey).collection("like-id").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                final DocumentReference newPost = likesReference.collection("Likes").document(postKey).collection("like-id").document(uid);
                                newPost.set(userLikeInfo);


                                FirebaseFirestore getIduserpost = postingDatabase;
                                getIduserpost.collection("Posting").document(postKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {



                                        String userpostUID = dataSnapshot.getString("uid");

                                        Map<String, Object> notifMap = new HashMap<>();
                                        notifMap.put("action", "liked");
                                        notifMap.put("uid", uid);
                                        notifMap.put("seen", false);
                                        notifMap.put("whatIS", "post");
                                        notifMap.put("timestamp", FieldValue.serverTimestamp());
                                        notifMap.put("post_key", postKey);
                                        CollectionReference notifSet = notificationReference.collection("Notification").document(userpostUID).collection("notif-id");
                                        notifSet.add(notifMap);


                                        if (e != null) {
                                            Log.e("likeERROR", e.getLocalizedMessage());
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

    }
}
