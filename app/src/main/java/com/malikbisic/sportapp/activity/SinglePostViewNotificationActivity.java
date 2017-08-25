package com.malikbisic.sportapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.PictureDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.caverock.androidsvg.SVG;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.Post;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.UsersModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static com.malikbisic.sportapp.activity.MainPage.uid;


public class SinglePostViewNotificationActivity extends AppCompatActivity {

    public Button play_button;
    public MediaPlayer mPlayer;
    JCVideoPlayerStandard videoView;
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
    DatabaseReference likeReference;
    DatabaseReference userReference;
    DatabaseReference numberCommentsReference;
    DatabaseReference postingReference;
    DatabaseReference profileUsers;
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
    ImageView postBackgroundImage;
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

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, likesReference, dislikeReference;
    TextView notificationCounterNumber;
    DatabaseReference notificationReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post_view_notification);

        play_button = (Button) findViewById(R.id.play_buttonSinglePost);
        pause_button = (Button) findViewById(R.id.pause_buttonSinglePost);
        stop_button = (Button) findViewById(R.id.stop_buttonSinglePost);

        mediaController = new MediaController(this);

        videoView = (JCVideoPlayerStandard) findViewById(R.id.posted_videoSinglePost);
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
        database = FirebaseDatabase.getInstance();
        likeReference = database.getReference().child("Likes");
        dislikeReference = database.getReference().child("Dislikes");
        userReference = database.getReference().child("Users");
        numberCommentsReference = database.getReference().child("Comments");
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notification");
        myIntent = getIntent();
        key = myIntent.getStringExtra("post_key");
        action = myIntent.getStringExtra("action");
        whatIS = myIntent.getStringExtra("whatIS");
        postingReference = FirebaseDatabase.getInstance().getReference().child("Posting").child(key);
        profileUsers = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        openComment = (TextView) findViewById(R.id.comment_somethingSinglePost);
        post_username = (TextView) findViewById(R.id.username_wallSinglePost);
        post_profile_image = (ImageView) findViewById(R.id.profile_image_wallSinglePost);
        post_clubLogo = (CircleImageView) findViewById(R.id.clubLogoPostSinglePost);
        postBackgroundImage = (ImageView) findViewById(R.id.image_post_backgroundSinglePost);
        comments = (TextView) findViewById(R.id.comments_textviewSinglePost);
        numberComments = (TextView) findViewById(R.id.number_commentsSinglePost);
        likeReference.keepSynced(true);
        dislikeReference.keepSynced(true);

        mDatabase = FirebaseDatabase.getInstance();
        likesReference = mDatabase.getReference().child("Likes");
        dislikeReference = mDatabase.getReference().child("Dislikes");
        likesReference.keepSynced(true);
        dislikeReference.keepSynced(true);

        retrieveDataPost();

    }

    public void retrieveDataPost(){
        postingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                    Post model = dataSnapshot.getValue(Post.class);

                    setDescForAudio(model.getDescForAudio());
                    setDescForPhoto(model.getDescForPhoto());
                    setDescVideo(model.getDescVideo());
                    setProfileImage(getApplicationContext(), model.getProfileImage());
                    setUsername(model.getUsername());
                    setPhotoPost(getApplicationContext(), model.getPhotoPost());
                    setVideoPost(getApplicationContext(), model.getVideoPost());
                    setAudioFile(getApplicationContext(), model.getAudioFile());
                    setLikeBtn(key);
                    setNumberLikes(key);
                    setDesc(model.getDesc());
                    setDislikeBtn(key);
                    setNumberComments(key);
                    setNumberDislikes(key);

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
                                                 seekBar.setProgress( mPlayer.getCurrentPosition());
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
                            if ( mPlayer.isPlaying() && pause_state)
                                 mPlayer.pause();
                            pause_state = false;

                        }
                    });

                     stop_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if ( mPlayer != null) {
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
                            startActivity(listUsername);
                        }
                    });

                     numberOfDislikes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent listUsername = new Intent(SinglePostViewNotificationActivity.this, Username_Dislikes_Activity.class);
                            listUsername.putExtra("post_key", key);
                            startActivity(listUsername);
                        }
                    });


                     like_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            like_process = true;


                            likesReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (like_process) {
                                        if (dataSnapshot.child(key).hasChild(mAuth.getCurrentUser().getUid())) {

                                            likesReference.child(key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            like_process = false;


                                        } else {

                                            DatabaseReference newPost = likesReference.child(key).child(mAuth.getCurrentUser().getUid());

                                            newPost.child("username").setValue(MainPage.usernameInfo);
                                            newPost.child("photoProfile").setValue(MainPage.profielImage);
                                            like_process = false;

                                            Log.i("keyPost", key);

                                            DatabaseReference getIduserpost = FirebaseDatabase.getInstance().getReference().child("Posting");
                                            getIduserpost.child(key).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();

                                                    String userpostUID = String.valueOf(value.get("uid"));
                                                    Log.i("keyUID", userpostUID);

                                                    DatabaseReference notifSet = notificationReference.child(userpostUID).push();
                                                    notifSet.child("action").setValue("like");
                                                    notifSet.child("uid").setValue(uid);
                                                    notifSet.child("seen").setValue(false);
                                                    notifSet.child("whatIS").setValue("post");
                                                    notifSet.child("post_key").setValue(key);

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    Log.i("error", databaseError.getMessage());

                                }
                            });

                        }
                    });


                     post_photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent openFullScreen = new Intent(SinglePostViewNotificationActivity.this, FullScreenImage.class);
                            String tag = (String)  post_photo.getTag();
                            openFullScreen.putExtra("imageURL", tag);
                            startActivity(openFullScreen);
                        }
                    });

                     dislike_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dislike_process = true;
                            dislikeReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dislike_process) {
                                        if (dataSnapshot.child(key).hasChild(mAuth.getCurrentUser().getUid())) {

                                            dislikeReference.child(key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            dislike_process = false;


                                        } else {

                                            DatabaseReference newPost = dislikeReference.child(key).child(mAuth.getCurrentUser().getUid());

                                            newPost.child("username").setValue(MainPage.usernameInfo);
                                            newPost.child("photoProfile").setValue(MainPage.profielImage);

                                            dislike_process = false;

                                            DatabaseReference getIduserpost = FirebaseDatabase.getInstance().getReference().child("Posting");
                                            getIduserpost.child(key).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String userpostUID = String.valueOf(dataSnapshot.child("uid").getValue());

                                                    DatabaseReference notifSet = notificationReference.child(userpostUID).push();
                                                    notifSet.child("action").setValue("like");
                                                    notifSet.child("uid").setValue(uid);
                                                    notifSet.child("seen").setValue(false);
                                                    notifSet.child("whatIS").setValue("post");
                                                    notifSet.child("post_key").setValue(key);


                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

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
                    });

                     comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent openCom = new Intent(SinglePostViewNotificationActivity.this, CommentsActivity.class);
                            openCom.putExtra("keyComment", key);
                            openCom.putExtra("profileComment", MainPage.profielImage);
                            startActivity(openCom);
                        }
                    });

                     numberComments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent openCom = new Intent(SinglePostViewNotificationActivity.this, CommentsActivity.class);
                            openCom.putExtra("keyComment", key);
                            openCom.putExtra("profileComment", MainPage.profielImage);
                            startActivity(openCom);
                        }
                    });


                     openComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent openCom = new Intent(SinglePostViewNotificationActivity.this, CommentsActivity.class);
                            openCom.putExtra("keyComment", key);
                            openCom.putExtra("profileComment", MainPage.profielImage);
                            startActivity(openCom);
                        }
                    });

                    postingReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(key).child("uid").exists()) {
                                if (mAuth.getCurrentUser().getUid().equals(dataSnapshot.child(key).child("uid").getValue().toString())) {
                                     arrow_down.setVisibility(View.VISIBLE);

                                     arrow_down.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            final String[] items = {"Edit post", "Delete post", "Cancel"};
                                            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder( SinglePostViewNotificationActivity.this);
                                            dialog.setItems(items, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    if (items[i].equals("Edit post")) {
                                                        Intent openSinglePost = new Intent(SinglePostViewNotificationActivity.this, SinglePostViewActivity.class);
                                                        openSinglePost.putExtra("post_id", key);
                                                        startActivity(openSinglePost);
                                                    } else if (items[i].equals("Delete post")) {

                                                        postingReference.child(key).removeValue();
                                                    } else if (items[i].equals("Cancel")) {


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


                     post_username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String username =  post_username.getText().toString().trim();
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
                                                        R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).commit();
                                                Log.i("tacno", "true");

                                            } else {

                                                DatabaseReference profileInfo = profileUsers.child(uid);

                                                profileInfo.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        Intent openUserProfile = new Intent(SinglePostViewNotificationActivity.this, UserProfileActivity.class);
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

                     post_profile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String username =  post_username.getText().toString().trim();
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
                                                        R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                                Log.i("tacno", "true");

                                            } else {

                                                DatabaseReference profileInfo = profileUsers.child(uid);

                                                profileInfo.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                                        Intent openUserProfile = new Intent(SinglePostViewNotificationActivity.this, UserProfileActivity.class);
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





        public void setNumberLikes(String post_key) {

            likeReference.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    long numberLikes = dataSnapshot.getChildrenCount();
                    if (numberLikes == 0) {
                        numberofLikes.setText("");
                    } else {
                        numberofLikes.setText(String.valueOf(numberLikes));
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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setLikeBtn(final String post_key) {
            likeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                        dislike_button.setClickable(false);
                        like_button.setActivated(true);

                    } else {
                        dislike_button.setClickable(true);
                        like_button.setActivated(false);
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
                    long numberDislikes = dataSnapshot.getChildrenCount();
                    if (numberDislikes == 0) {
                        numberOfDislikes.setText("");
                    } else {
                        numberOfDislikes.setText(String.valueOf(numberDislikes));
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

                        dislike_button.setActivated(true);
                        like_button.setClickable(false);

                    } else {
                        dislike_button.setActivated(false);
                        like_button.setClickable(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setDesc(String desc) {
            if (desc != null) {
                TextView single_post = (TextView) findViewById(R.id.post_text_main_pageSinglePost);
                single_post_layout.setVisibility(View.VISIBLE);
                single_post.setText(desc);
            } else {
                single_post_layout.setVisibility(View.GONE);
            }
        }


        public void setDescForAudio(String descForAudio) {

            TextView post_desc_for_audio = (TextView) findViewById(R.id.audio_textviewSinglePost);
            if (descForAudio != null) {
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
                layoutPhotoText.setVisibility(View.VISIBLE);
                post_desc_for_photo.setText(descForPhoto);
            } else {
                layoutPhotoText.setVisibility(View.GONE);
            }
        }

        public void setDescVideo(String descVideo) {
            TextView post_desc_for_video = (TextView) findViewById(R.id.text_for_videoSinglePost);

            if (descVideo != null) {
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

            Picasso.with(ctx).load(profileImage).into(post_profile_image);

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
                layoutPhoto.setVisibility(View.VISIBLE);

                Glide.with(ctx)
                        .load(photoPost)
                        .asBitmap()
                        .override(720,640)
                        .centerCrop()
                        .into(new SimpleTarget< Bitmap >() {
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

                try {

                    layoutVideo.setVisibility(View.VISIBLE);
                    videoView.setUp(videoPost, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "proba");
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
