package com.malikbisic.sportapp.activity.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
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
import com.malikbisic.sportapp.activity.StopAppServices;

import java.util.HashMap;
import java.util.Map;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;

public class FullScreenImage extends AppCompatActivity {
    FirebaseFirestore db;
    CollectionReference likeRef;
    CollectionReference dislikeRef;
    CollectionReference commentsRef;
    Intent intent;
    String postKey;
    String uri;
    FirebaseFirestore likesReference;
    FirebaseFirestore dislikesReference;
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
    String imageTitle;
    TextView titleImage;
    boolean firstImageClick = true;
    boolean secondImageClick = false;
    ImageView image;
    RelativeLayout parentLayout;
    RelativeLayout layoutImageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        likesReference = FirebaseFirestore.getInstance();
        notificationReference = FirebaseFirestore.getInstance();
        dislikesReference = FirebaseFirestore.getInstance();
        numberLikesTextview = (TextView) findViewById(R.id.likesinfullscreen);
        numberDislikesTextview = (TextView) findViewById(R.id.dislikesFullscreen);
        numberCommentsTextivew = (TextView) findViewById(R.id.numberCommentsFullscreen);
        likeFullscreenImage = (ImageView) findViewById(R.id.likeImageFullscreen);
        dislikeFullscreenImage = (ImageView) findViewById(R.id.dislikeImageFullscreen);
        titleImage = (TextView) findViewById(R.id.textfromposttoimage);
        commentFullscreenImage = (ImageView) findViewById(R.id.commentFullscreenimage);
        image = (ImageView) findViewById(R.id.fullScreenImageView);
        layoutImageTitle = (RelativeLayout) findViewById(R.id.imagetitlelayout);
        postingDatabase = FirebaseFirestore.getInstance();
        intent = getIntent();
        postKey = intent.getStringExtra("postKey");
        image = (ImageView) findViewById(R.id.fullScreenImageView);
        uri = intent.getStringExtra("imageURL");
        imageTitle = intent.getStringExtra("title");
        Glide.with(this).load(uri).diskCacheStrategy(SOURCE).centerCrop().override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL).into(image);
        numberLikesLayout = (RelativeLayout) findViewById(R.id.likeslayout);
        numberDislikesLayout = (RelativeLayout) findViewById(R.id.dislikeslayout);
        numberCommentsLayout = (RelativeLayout) findViewById(R.id.comentslayout);
        parentLayout = (RelativeLayout) findViewById(R.id.parentlayout);
        uid = mAuth.getCurrentUser().getUid();

        if (imageTitle != null) {
            titleImage.setText(imageTitle);
        }

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
                    numberLikesLayout.setVisibility(View.VISIBLE);
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

                if (numberComments == 0) {

                    numberCommentsLayout.setVisibility(View.GONE);
                } else {
                    numberCommentsLayout.setVisibility(View.VISIBLE);
                    numberCommentsTextivew.setText(String.valueOf(numberComments));
                }
            }
        });

        numberLikesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScreenImage.this, Username_Likes_Activity.class);
                intent.putExtra("post_key", postKey);
                startActivity(intent);
            }
        });
        numberDislikesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScreenImage.this, Username_Dislikes_Activity.class);
                intent.putExtra("post_key", postKey);
                startActivity(intent);
            }
        });


        numberCommentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScreenImage.this, CommentsActivity.class);
                intent.putExtra("keyComment", postKey);
                intent.putExtra("profileComment", MainPage.profielImage);
                intent.putExtra("username", MainPage.usernameInfo);
                startActivity(intent);
            }
        });


        final DocumentReference doc = likesReference.collection("Likes").document(postKey).collection("like-id").document(uid);
        doc.addSnapshotListener(FullScreenImage.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {

                    dislikeFullscreenImage.setClickable(false);
                    likeFullscreenImage.setActivated(true);


                } else {
                    dislikeFullscreenImage.setClickable(true);
                    likeFullscreenImage.setActivated(false);

                }

            }
        });

        final DocumentReference docDislike = dislikesReference.collection("Dislikes").document(postKey).collection("dislike-id").document(uid);
        docDislike.addSnapshotListener(FullScreenImage.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {
                    dislikeFullscreenImage.setActivated(true);
                    likeFullscreenImage.setClickable(false);


                } else {
                    dislikeFullscreenImage.setActivated(false);
                    likeFullscreenImage.setClickable(true);

                }
            }
        });

        likesReference.collection("Likes").document(postKey).collection("like-id").document(uid).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    likeFullscreenImage.setImageDrawable(getResources().getDrawable(R.drawable.likesomethingfull));

                }
            }
        });

        dislikesReference.collection("Dislikes").document(postKey).collection("dislike-id").document(uid).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    dislikeFullscreenImage.setImageDrawable(getResources().getDrawable(R.drawable.dislikesomethingfull));

                }
            }
        });


        likeFullscreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                like_process = true;


                likesReference.collection("Likes").document(postKey).collection("like-id").document(uid).addSnapshotListener(FullScreenImage.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (like_process) {

                            likeRef.addSnapshotListener(FullScreenImage.this, new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                    int numberLikes = documentSnapshots.size();
                                    if (numberLikes == 0) {
                                        numberLikesTextview.setText("");
                                        numberLikesLayout.setVisibility(View.GONE);
                                    } else {
                                        numberLikesTextview.setText(String.valueOf(numberLikes));
                                        numberLikesLayout.setVisibility(View.VISIBLE);
                                    }

                                }
                            });

                            if (documentSnapshots.exists()) {

                                likesReference.collection("Likes").document(postKey).collection("like-id").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        likeFullscreenImage.setImageDrawable(getResources().getDrawable(R.drawable.likesomethingg));
                                    }
                                });
                                like_process = false;


                                Log.i("nema like", "NEMA");

                            } else {
                                likeFullscreenImage.setImageDrawable(getResources().getDrawable(R.drawable.likesomethingfull));
                                Log.i("ima like", "IMA");
                                Map<String, Object> userLikeInfo = new HashMap<>();
                                userLikeInfo.clear();
                                userLikeInfo.put("username", MainPage.usernameInfo);
                                userLikeInfo.put("photoProfile", MainPage.profielImage);

                                final DocumentReference newPost = likesReference.collection("Likes").document(postKey).collection("like-id").document(uid);
                                newPost.set(userLikeInfo);

                                FirebaseFirestore getIduserpost = postingDatabase;
                                getIduserpost.collection("Posting").document(postKey).addSnapshotListener(FullScreenImage.this, new EventListener<DocumentSnapshot>() {
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


        dislikeFullscreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike_process = true;
               /* viewHolder.setNumberDislikes(post_key, activity);*/


                dislikesReference.collection("Dislikes").document(postKey).collection("dislike-id").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (dislike_process) {

                            dislikeRef.addSnapshotListener(FullScreenImage.this, new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                    int numberLikes = documentSnapshots.size();
                                    if (numberLikes == 0) {
                                        numberDislikesTextview.setText("");
                                        numberDislikesLayout.setVisibility(View.GONE);
                                    } else {
                                        numberDislikesTextview.setText(String.valueOf(numberLikes));
                                        numberDislikesLayout.setVisibility(View.VISIBLE);
                                    }

                                }
                            });
                            if (documentSnapshots.exists()) {
                                dislikesReference.collection("Dislikes").document(postKey).collection("dislike-id").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.i("deleteDislike", "complete");
                                        dislikeFullscreenImage.setImageDrawable(getResources().getDrawable(R.drawable.dislikesomething));

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("deletedislike", e.getLocalizedMessage());
                                    }
                                });
                                dislike_process = false;


                            } else {
                                dislikeFullscreenImage.setImageDrawable(getResources().getDrawable(R.drawable.dislikesomethingfull));
                                Map<String, Object> userDislikeInfo = new HashMap<>();
                                userDislikeInfo.put("username", MainPage.usernameInfo);
                                userDislikeInfo.put("photoProfile", MainPage.profielImage);

                                final DocumentReference newPost = dislikesReference.collection("Dislikes").document(postKey).collection("dislike-id").document(uid);
                                newPost.set(userDislikeInfo);


                                FirebaseFirestore getIduserpost = postingDatabase;
                                getIduserpost.collection("Posting").document(postKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                        String userpostUID = dataSnapshot.getString("uid");

                                        Map<String, Object> notifMap = new HashMap<>();
                                        notifMap.put("action", "disliked");
                                        notifMap.put("uid", uid);
                                        notifMap.put("seen", false);
                                        notifMap.put("whatIS", "post");
                                        notifMap.put("timestamp", FieldValue.serverTimestamp());
                                        notifMap.put("post_key", postKey);
                                        CollectionReference notifSet = notificationReference.collection("Notification").document(userpostUID).collection("notif-id");
                                        notifSet.add(notifMap);

                                    }

                                });


                                dislike_process = false;


                            }
                        }
                    }
                });

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firstImageClick) {
                    firstImageClick = false;
                    secondImageClick = true;
                    parentLayout.setVisibility(View.GONE);
                    layoutImageTitle.setVisibility(View.GONE);

                } else if (secondImageClick) {
                    firstImageClick = true;
                    secondImageClick = false;
                    parentLayout.setVisibility(View.VISIBLE);
                    layoutImageTitle.setVisibility(View.VISIBLE);
                }

            }
        });

        commentFullscreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScreenImage.this, CommentsActivity.class);
                intent.putExtra("keyComment", postKey);
                intent.putExtra("profileComment", MainPage.profielImage);
                intent.putExtra("username", MainPage.usernameInfo);
                startActivity(intent);
            }
        });
    }
}