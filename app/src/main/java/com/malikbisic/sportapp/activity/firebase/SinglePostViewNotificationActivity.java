package com.malikbisic.sportapp.activity.firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.PictureDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.caverock.androidsvg.SVG;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.malikbisic.sportapp.fragment.firebase.ProfileFragment;
import com.malikbisic.sportapp.activity.api.SearchableCountry;
import com.malikbisic.sportapp.model.firebase.Post;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.malikbisic.sportapp.utils.PostingTimeAgo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZVideoPlayerStandard;
import de.hdodenhof.circleimageview.CircleImageView;


import static com.malikbisic.sportapp.activity.firebase.MainPage.uid;


public class SinglePostViewNotificationActivity extends AppCompatActivity {
    ArrayList<Post> postList = new ArrayList<>();
    public Button play_button;
    public MediaPlayer mPlayer;
    cn.jzvd.JZVideoPlayerStandard videoView;
    ImageView post_photo;
    MediaController mediaController;
    RelativeLayout audioLayout;
    ProgressDialog progressDialog;
    public Button pause_button;
    public SeekBar seekBar;
    public Button stop_button;
    ImageView like_button;
    ImageView dislike_button;
    FirebaseDatabase database;
    CollectionReference likeReference;
    CollectionReference userReference;
    CollectionReference numberCommentsReference;
    DocumentReference postingReference;
    CollectionReference profileUsers;
    FirebaseAuth mAuth;
    public TextView numberofLikes;
    public TextView numberOfDislikes;
    RelativeLayout layoutPhoto, layoutPhotoText, layoutAudioText, layoutVideoText, single_post_layout;
    FrameLayout layoutVideo;
    ProgressBar loadPhoto;
    TextView openSinglePost;
    ImageView arrow_down;
    TextView openComment;
    TextView post_username;
    ImageView post_profile_image;
    RelativeLayout layoutForPost;
    RelativeLayout backgroundImage;
    CircleImageView postBackgroundImage;
    CircleImageView post_clubLogo;
    TextView comments;
    TextView numberComments;
    String logo;
    String usersFavClub;
    String myClub;
    boolean isPremium;
    String key;
    String action;
    String whatIS;
    Intent myIntent;

    boolean pause_state;
    boolean play_state;
    boolean like_process = false;
    boolean dislike_process = false;
    boolean stop_state;
    int numberLikes;
    int numberDislikes;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private CollectionReference likesReference;
    private CollectionReference dislikeReference;
    TextView notificationCounterNumber;
    CollectionReference notificationReference;

    RelativeLayout rowLAyout;
    TextView timeAgoTxt;
    TextView likeBtnTekst;
    TextView dislikeBtnTekst;
    RelativeLayout postWithBackgroundLayout;
    TextView posttextWithbackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post_view_notification);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        play_button = (Button) findViewById(R.id.play_buttonSinglePost);
        pause_button = (Button) findViewById(R.id.pause_buttonSinglePost);
        stop_button = (Button) findViewById(R.id.stop_buttonSinglePost);
        FirebaseFirestore mReference = FirebaseFirestore.getInstance();

        mediaController = new MediaController(this);

        videoView = (cn.jzvd.JZVideoPlayerStandard) findViewById(R.id.posted_videoSinglePost);
        post_photo = (ImageView) findViewById(R.id.posted_imageSinglePost);
        audioLayout = (RelativeLayout) findViewById(R.id.layout_for_audio_playerSinglePost);
        mPlayer = new MediaPlayer();
        progressDialog = new ProgressDialog(this);
        seekBar = (SeekBar) findViewById(R.id.audio_seek_barSinglePost);
        like_button = (ImageView) findViewById(R.id.like_buttonSinglePost);
        dislike_button = (ImageView) findViewById(R.id.dislike_buttonSinglePost);
        numberofLikes = (TextView) findViewById(R.id.number_of_likesSinglePost);
        numberOfDislikes = (TextView) findViewById(R.id.number_of_dislikesSinglePost);
        single_post_layout = (RelativeLayout) findViewById(R.id.layout_for_only_postSinglePost);

        arrow_down = (ImageView) findViewById(R.id.down_arrowSinglePost);
        layoutPhotoText = (RelativeLayout) findViewById(R.id.layout_for_text_imageSinglePost);
        layoutPhoto = (RelativeLayout) findViewById(R.id.layout_for_imageSinglePost);
        layoutAudioText = (RelativeLayout) findViewById(R.id.layout_audio_textviewSinglePost);
        layoutVideo = (FrameLayout) findViewById(R.id.framelayoutSinglePost);
        //loadPhoto = (ProgressBar) mView.findViewById(R.id.post_photo_bar_load);
        layoutVideoText = (RelativeLayout) findViewById(R.id.layout_for_video_textSinglePost);
        timeAgoTxt = (TextView) findViewById(R.id.postAgoTime);

        likeReference = mReference.collection("Likes");
        dislikeReference = mReference.collection("Dislikes");
        userReference = mReference.collection("Users");
        numberCommentsReference = mReference.collection("Comments");
        notificationReference = mReference.collection("Notification");
        myIntent = getIntent();
        key = myIntent.getStringExtra("post_key");
        action = myIntent.getStringExtra("action");
        whatIS = myIntent.getStringExtra("whatIS");


        postingReference = mReference.collection("Posting").document(key);
        profileUsers = mReference.collection("Users");
        mAuth = FirebaseAuth.getInstance();
        openComment = (TextView) findViewById(R.id.comment_somethingSinglePost);
        post_username = (TextView) findViewById(R.id.username_wallSinglePost);
        post_profile_image = (ImageView) findViewById(R.id.profile_image_wallSinglePost);
        post_clubLogo = (CircleImageView) findViewById(R.id.clubLogoPostSinglePost);
        postWithBackgroundLayout = (RelativeLayout) findViewById(R.id.layout_for_only_postWithBackgroundSinglePost);
        posttextWithbackground = (TextView) findViewById(R.id.post_text_main_pageWithBackgroundSinglePost);
        comments = (TextView) findViewById(R.id.comments_textviewSinglePost);
        numberComments = (TextView) findViewById(R.id.number_commentsSinglePost);
        likeBtnTekst = (TextView) findViewById(R.id.likesomethingSinglePost);
        dislikeBtnTekst = (TextView) findViewById(R.id.disliketekstSinglePost);
        postBackgroundImage = (CircleImageView) findViewById(R.id.image_post_background);

        mDatabase = FirebaseDatabase.getInstance();
        likesReference = mReference.collection("Likes");
        dislikeReference = mReference.collection("Dislikes");
        rowLAyout = (RelativeLayout) findViewById(R.id.row_layout_relative_notif);

        retrieveDataPost();

    }

    public void retrieveDataPost() {
        postingReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                if (dataSnapshot.exists()) {

                    final Post model = dataSnapshot.toObject(Post.class);

                    setDescForAudio(model.getDescForAudio());
                    setDescForPhoto(model.getDescForPhoto());
                    setDescVideo(model.getDescVideo());
                    setProfileImage(getApplicationContext(), model.getProfileImage());
                    setUsername(model.getUsername());
                    setPhotoPost(getApplicationContext(), model.getPhotoPost());
                    setVideoPost(getApplicationContext(), model.getVideoPost());
                    setAudioFile(getApplicationContext(), model.getAudioFile());
                    setLikeBtn(key, SinglePostViewNotificationActivity.this);
                    setNumberLikes(key, SinglePostViewNotificationActivity.this);
                    setDesc(model.getDesc());
                    setDislikeBtn(key, SinglePostViewNotificationActivity.this);
                    setNumberComments(key, SinglePostViewNotificationActivity.this);
                    setNumberDislikes(key, SinglePostViewNotificationActivity.this);
                    setTimeAgo(model.getTime(), SinglePostViewNotificationActivity.this);
                    setCountry(SinglePostViewNotificationActivity.this, model.getCountry());
                    setDescWithBackground(model.getDescWithBackground());
                    setIdResource(model.getIdResource());

                    setClubLogo(getApplicationContext(), model.getClubLogo());


                    seekBar.setEnabled(true);
                    play_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            try {
                                mPlayer.prepareAsync();
                                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mPlayer.start();

                                        seekBar.setMax(mPlayer.getDuration());

                                        new Timer().scheduleAtFixedRate(new TimerTask() {
                                            @Override
                                            public void run() {
                                                seekBar.setProgress(mPlayer.getCurrentPosition());
                                                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                    @Override
                                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                        if (fromUser) {
                                                            mPlayer.seekTo(progress);
                                                        }
                                                    }

                                                    @Override
                                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                                    }

                                                    @Override
                                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                                    }
                                                });
                                            }
                                        }, 0, 100);

                                    }
                                });


                                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        Log.i("finished", "yes");

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            play_state = true;
                            if (!pause_state) {
                                mPlayer.start();
                                pause_state = false;
                            }

                        }


                    });


                    pause_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pause_state = true;
                            play_state = false;
                            if (mPlayer.isPlaying() && pause_state)
                                mPlayer.pause();
                            pause_state = false;

                        }
                    });

                    stop_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mPlayer != null) {
                                mPlayer.stop();
                                seekBar.setMax(0);


                            }
                        }
                    });

                /* openSinglePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openSinglePost = new Intent(MainPage.this, SinglePostViewActivity.class);
                        openSinglePost.putExtra("post_id", post_key);
                        startActivity(openSinglePost);
                        Log.i("linkPost", link_post);
                    }
                }); */

                    numberofLikes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent listUsername = new Intent(SinglePostViewNotificationActivity.this, Username_Likes_Activity.class);
                            listUsername.putExtra("post_key", key);
                            listUsername.putExtra("openActivityToBack", "mainPage");
                            startActivity(listUsername);
                        }
                    });
                    numberOfDislikes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent listUsername = new Intent(SinglePostViewNotificationActivity.this, Username_Dislikes_Activity.class);
                            listUsername.putExtra("post_key", key);
                            listUsername.putExtra("openActivityToBack", "mainPage");
                            startActivity(listUsername);
                        }
                    });


                    like_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            like_process = true;


                            likesReference.document(key).collection("like-id").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                                    if (like_process) {


                                        if (documentSnapshots.exists()) {

                                            likesReference.document(key).collection("like-id").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                            final DocumentReference newPost = likesReference.document(key).collection("like-id").document(uid);
                                            newPost.set(userLikeInfo);


                                            FirebaseFirestore getIduserpost = FirebaseFirestore.getInstance();
                                            getIduserpost.collection("Posting").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                                    String userpostUID = dataSnapshot.getString("uid");

                                                    Map<String, Object> notifMap = new HashMap<>();
                                                    notifMap.put("action", "liked");
                                                    notifMap.put("uid", uid);
                                                    notifMap.put("seen", false);
                                                    notifMap.put("whatIS", "post");
                                                    notifMap.put("timestamp", FieldValue.serverTimestamp());
                                                    notifMap.put("post_key", key);
                                                    CollectionReference notifSet = notificationReference.document(userpostUID).collection("notif-id");
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


                    post_photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent openFullScreen = new Intent(SinglePostViewNotificationActivity.this, FullScreenImage.class);
                            String tag = (String) post_photo.getTag();
                            openFullScreen.putExtra("imageURL", tag);
                            openFullScreen.putExtra("postKey", key);
                            openFullScreen.putExtra("title", model.getDescForPhoto());
                            startActivity(openFullScreen);
                        }
                    });

                    dislike_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dislike_process = true;
               /* ((MainPageAdapter.PostViewHolder) holder).setNumberDislikes(post_key, activity);*/


                            dislikeReference.document(key).collection("dislike-id").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                                    if (dislike_process) {


                                        if (documentSnapshots.exists()) {
                                            dislikeReference.document(key).collection("dislike-id").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                            final DocumentReference newPost = dislikeReference.document(key).collection("dislike-id").document(uid);
                                            newPost.set(userDislikeInfo);


                                            FirebaseFirestore getIduserpost = FirebaseFirestore.getInstance();
                                            getIduserpost.collection("Posting").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                                    String userpostUID = dataSnapshot.getString("uid");

                                                    Map<String, Object> notifMap = new HashMap<>();
                                                    notifMap.put("action", "disliked");
                                                    notifMap.put("uid", uid);
                                                    notifMap.put("seen", false);
                                                    notifMap.put("whatIS", "post");
                                                    notifMap.put("timestamp", FieldValue.serverTimestamp());
                                                    notifMap.put("post_key", key);
                                                    CollectionReference notifSet = notificationReference.document(userpostUID).collection("notif-id");
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

//                    comments.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent openCom = new Intent(SinglePostViewNotificationActivity.this, CommentsActivity.class);
//                            openCom.putExtra("keyComment", key);
//                            openCom.putExtra("profileComment", MainPage.profielImage);
//                            openCom.putExtra("username", MainPage.usernameInfo);
//                            startActivity(openCom);
//                        }
//                    });

                    numberComments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent openCom = new Intent(SinglePostViewNotificationActivity.this, CommentsActivity.class);
                            openCom.putExtra("keyComment", key);
                            openCom.putExtra("profileComment", MainPage.profielImage);
                            openCom.putExtra("username", MainPage.usernameInfo);
                            startActivity(openCom);
                        }
                    });


                    openComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent openCom = new Intent(SinglePostViewNotificationActivity.this, CommentsActivity.class);
                            openCom.putExtra("keyComment", key);
                            openCom.putExtra("profileComment", MainPage.profielImage);
                            openCom.putExtra("username", MainPage.usernameInfo);
                            startActivity(openCom);
                        }
                    });
                    final FirebaseFirestore postingDatabaseProfile = FirebaseFirestore.getInstance();
                    postingDatabaseProfile.collection("Posting").document(key).addSnapshotListener(SinglePostViewNotificationActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(final DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.contains("uid")) {
                                    if (mAuth.getCurrentUser().getUid().equals(dataSnapshot.getString("uid"))) {

                                        arrow_down.setVisibility(View.VISIBLE);

                                        arrow_down.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                final String[] items = {"Edit post", "Delete post", "Cancel"};
                                                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(SinglePostViewNotificationActivity.this, R.style.AppTheme_Dark_Dialog);

                                                dialog.setItems(items, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        if (items[i].equals("Edit post")) {
                                                            Intent openSinglePost = new Intent(SinglePostViewNotificationActivity.this, SinglePostViewActivity.class);
                                                            openSinglePost.putExtra("post_id", key);
                                                            startActivity(openSinglePost);
                                                        } else if (items[i].equals("Delete post")) {


                                                            postingDatabaseProfile.collection("Posting").document(key).delete().addOnSuccessListener(SinglePostViewNotificationActivity.this, new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(SinglePostViewNotificationActivity.this, "Post deleted", Toast.LENGTH_LONG).show();
                                                                    rowLAyout.setVisibility(View.GONE);
                                                                    Intent intent = new Intent(SinglePostViewNotificationActivity.this, MainPage.class);
                                                                    startActivity(intent);
                                                                    finish();


                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(SinglePostViewNotificationActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                                                                }
                                                            });


                                                        } else if (items[i].equals("Cancel")) {


                                                        }
                                                    }
                                                });

                                                dialog.create();
                                                dialog.show();
                                            }
                                        });
                                    } else
                                        arrow_down.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                    });


                    post_username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String username = post_username.getText().toString().trim();
                            Log.i("username", username);

                            profileUsers.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                                Intent intent = new Intent(SinglePostViewNotificationActivity.this, ProfileFragment.class);
                                                startActivity(intent);

                                            } else {

                                                DocumentReference profileInfo = profileUsers.document(uid);

                                                profileInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                                        Intent openUserProfile = new Intent(SinglePostViewNotificationActivity.this, UserProfileActivity.class);
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

                    post_profile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String username = post_username.getText().toString().trim();
                            Log.i("username", username);

                            profileUsers.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {

                                    for (DocumentSnapshot dataSnapshot1 : dataSnapshot.getDocuments()) {

                                        final UsersModel userInfo = dataSnapshot1.toObject(UsersModel.class);

                                        String usernameFirebase = userInfo.getUsername();
                                        Log.i("usernameFirebase", usernameFirebase);

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

                                                Intent intent = new Intent(SinglePostViewNotificationActivity.this, ProfileFragment.class);
                                                startActivity(intent);

                                            } else {

                                                DocumentReference profileInfo = profileUsers.document(uid);

                                                profileInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                                        Intent openUserProfile = new Intent(SinglePostViewNotificationActivity.this, UserProfileActivity.class);
                                                        openUserProfile.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
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
                } else {
                    Toast.makeText(SinglePostViewNotificationActivity.this, "Post not exists", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void setTimeAgo(Date time, Context ctx) {
        PostingTimeAgo getTimeAgo = new PostingTimeAgo();
        //Date time = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault()).parse(str_date);
        if (time != null) {
            long lastTime = time.getTime();
            String lastStringTime = getTimeAgo.getTimeAgo(lastTime, ctx);
            timeAgoTxt.setText(lastStringTime);
        }
    }


    public void setNumberLikes(final String post_key, Activity activity) {
        CollectionReference col = FirebaseFirestore.getInstance().collection("Likes").document(post_key).collection("like-id");
        col.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                numberLikes = documentSnapshots.size();
                if (numberLikes == 0) {
                    numberofLikes.setText("");
                } else {
                    numberofLikes.setText(String.valueOf(numberLikes));
                }
            }
        });


    }

    public void setDescWithBackground(String descWithBackground) {
        if (descWithBackground != null) {
            postWithBackgroundLayout.setVisibility(View.VISIBLE);
            posttextWithbackground.setText(descWithBackground);

        } else {
            postWithBackgroundLayout.setVisibility(View.GONE);
        }

    }

    public void setIdResource(int idResource) {
        if (idResource != 0) {
            postWithBackgroundLayout.setVisibility(View.VISIBLE);
            postWithBackgroundLayout.setBackgroundResource(idResource);

        } else {
            postWithBackgroundLayout.setVisibility(View.GONE);
        }
    }

    public void setNumberComments(String post_key, Activity activity) {

        numberCommentsReference.document(post_key).collection("comment-id").addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int numberOfComments = documentSnapshots.getDocuments().size();


                if (numberOfComments == 0) {

                    comments.setVisibility(View.GONE);
                    numberComments.setText("");
                } else if (numberOfComments == 1) {

                    comments.setText("Comment");
                    numberComments.setText(String.valueOf(numberOfComments));
                } else {
                    comments.setText("Comments");
                    numberComments.setText(String.valueOf(numberOfComments));

                }
            }
        });
    }


    public void setLikeBtn(final String post_key, Activity activity) {
        if (!mAuth.getCurrentUser().getUid().isEmpty()) {
            String uid = mAuth.getCurrentUser().getUid();
            Log.i("uid", uid);
            final DocumentReference doc = likeReference.document(post_key).collection("like-id").document(uid);
            doc.addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {


                        dislike_button.setClickable(false);
                        like_button.setActivated(true);
                        if (like_button.isActivated()) {

                            like_button.setImageResource(R.drawable.thumbup);
                            likeBtnTekst.setText("Liked");

                        }
                        Log.i("key like ima ", post_key);


                    } else {
                        dislike_button.setClickable(true);
                        like_button.setActivated(false);
                        if (!like_button.isActivated()) {
                            like_button.setImageResource(R.drawable.thumbupgreen);
                            likeBtnTekst.setText("Like");

                        }
                        Log.i("key like nema ", post_key);
                    }

                }
            });
        }
    }

    public void setNumberDislikes(String post_key, Activity activity) {
        CollectionReference col = dislikeReference.document(post_key).collection("dislike-id");
        col.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                numberDislikes = documentSnapshots.getDocuments().size();


                if (numberDislikes == 0) {
                    numberOfDislikes.setText("");
                } else {
                    numberOfDislikes.setText(String.valueOf(numberDislikes));
                }

            }
        });
    }


    public void setDislikeBtn(final String post_key, Activity activity) {
        String uid = mAuth.getCurrentUser().getUid();
        final DocumentReference doc = dislikeReference.document(post_key).collection("dislike-id").document(uid);
        doc.addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {
                    dislike_button.setActivated(true);
                    like_button.setClickable(false);

                    if (dislike_button.isActivated()) {
                        dislike_button.setImageResource(R.drawable.thumbdown);
                        dislikeBtnTekst.setText("Disliked");

                    }
                    Log.i("key dislike ima ", post_key);

                } else {
                    dislike_button.setActivated(false);
                    like_button.setClickable(true);
                    if (!dislike_button.isActivated()) {
                        dislike_button.setImageResource(R.drawable.thumbdowngreen);
                        dislikeBtnTekst.setText("Dislike");
                    }
                    Log.i("key dislike nema ", post_key);
                }
            }
        });
    }

    public void setDesc(String desc) {
        if (desc != null) {
            TextView single_post = (TextView) findViewById(R.id.post_text_main_pageSinglePost);
            rowLAyout.setVisibility(View.VISIBLE);
            single_post_layout.setVisibility(View.VISIBLE);
            single_post.setText(desc);
        } else {
            single_post_layout.setVisibility(View.GONE);
        }
    }


    public void setDescForAudio(String descForAudio) {

        TextView post_desc_for_audio = (TextView) findViewById(R.id.audio_textviewSinglePost);
        if (descForAudio != null) {
            rowLAyout.setVisibility(View.VISIBLE);
            layoutAudioText.setVisibility(View.VISIBLE);
            post_desc_for_audio.setText(descForAudio);
        } else {
            layoutAudioText.setVisibility(View.GONE);
            post_desc_for_audio.setVisibility(View.GONE);
        }
    }

    public void setDescForPhoto(String descForPhoto) {

        TextView post_desc_for_photo = (TextView) findViewById(R.id.text_for_imageSinglePost);

        if (descForPhoto != null) {
            rowLAyout.setVisibility(View.VISIBLE);
            layoutPhotoText.setVisibility(View.VISIBLE);
            post_desc_for_photo.setText(descForPhoto);
        } else {
            layoutPhotoText.setVisibility(View.GONE);
        }
    }

    public void setDescVideo(String descVideo) {
        TextView post_desc_for_video = (TextView) findViewById(R.id.text_for_videoSinglePost);

        if (descVideo != null) {
            rowLAyout.setVisibility(View.VISIBLE);
            layoutVideoText.setVisibility(View.VISIBLE);
            post_desc_for_video.setText(descVideo);
        } else {
            layoutVideoText.setVisibility(View.GONE);
        }

    }


    public void setUsername(String username) {

        post_username.setText(username);


    }

    public void setProfileImage(Context ctx, String profileImage) {

        Picasso.with(ctx).load(profileImage).into(post_profile_image, new Callback() {
            @Override
            public void onSuccess() {
                ProgressBar loadImage = (ProgressBar) findViewById(R.id.loadImageProgressWallSinglePost);
                loadImage.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });

    }

    public void setClubLogo(Context ctx, String clubLogo) {

        Picasso.with(ctx).load(clubLogo).into(post_clubLogo);
    }


    public void setCountry(Context ctx, String country) {
        if (country != null) {
            GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

            requestBuilder = Glide
                    .with(ctx)
                    .using(Glide.buildStreamModelLoader(Uri.class, ctx), InputStream.class)
                    .from(Uri.class)
                    .as(SVG.class)
                    .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                    .sourceEncoder(new StreamEncoder())
                    .cacheDecoder(new FileToStreamDecoder<SVG>(new SearchableCountry.SvgDecoder()))
                    .decoder(new SearchableCountry.SvgDecoder())
                    .animate(android.R.anim.fade_in);


            Uri uri = Uri.parse(country);
            requestBuilder
                    // SVG cannot be serialized so it's not worth to cache it
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .load(uri)
                    .into(postBackgroundImage);
            postBackgroundImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            postBackgroundImage.setAlpha(0.4f);
        }
    }


    public void setPhotoPost(Context ctx, String photoPost) {

        if (photoPost != null) {
            rowLAyout.setVisibility(View.VISIBLE);
            layoutPhoto.setVisibility(View.VISIBLE);

            Glide.with(ctx)
                    .load(photoPost)
                    .asBitmap()
                    .override(720, 640)
                    .centerCrop()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            ProgressBar loadImageProgress = (ProgressBar) findViewById(R.id.loadImageProgressWallImageSinglePost);
                            loadImageProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            post_photo.setImageBitmap(resource);
                        }

                    });
//                Picasso.with(ctx).load(photoPost).into(post_photo, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        //     loadPhoto.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
            post_photo.setTag(photoPost);
        } else {

            layoutPhoto.setVisibility(View.GONE);
            // loadPhoto.setVisibility(View.GONE);
        }

    }

    public void setVideoPost(Context ctx, String videoPost) {


        if (videoPost != null) {
            rowLAyout.setVisibility(View.VISIBLE);

            try {

                layoutVideo.setVisibility(View.VISIBLE);
                videoView.setUp(videoPost, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "proba");
                videoView.requestFocus();
            } catch (Exception e) {
                e.getMessage();
            }

        } else {
            layoutVideo.setVisibility(View.GONE);
        }
    }


    public void setAudioFile(Context context, String audioFile) {

        if (audioFile != null) {
            rowLAyout.setVisibility(View.VISIBLE);
            mPlayer.reset();
            audioLayout.setVisibility(View.VISIBLE);
            try {

                mPlayer.setDataSource(context, Uri.parse(audioFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            audioLayout.setVisibility(View.GONE);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
