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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.malikbisic.sportapp.activity.api.SearchableCountry;
import com.malikbisic.sportapp.model.firebase.Post;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.malikbisic.sportapp.utils.PostingTimeAgo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZVideoPlayerStandard;
import de.hdodenhof.circleimageview.CircleImageView;


public class MyPostsActivity extends AppCompatActivity {
    UsersModel model;
    FirebaseAuth mAuth;
    RecyclerView postRecyclerView;
    LinearLayoutManager linearLayoutManager;
    private FirebaseFirestore mReference, postingDatabase, likesReference, dislikeReference, notificationReference;
    boolean pause_state;
    boolean play_state;
    boolean like_process = false;
    boolean dislike_process = false;
    private FirebaseFirestore mDatabase;
    String uid;
    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        mAuth = FirebaseAuth.getInstance();
        myIntent = getIntent();
        mDatabase = FirebaseFirestore.getInstance();
        postingDatabase = FirebaseFirestore.getInstance();
        postRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_my_posts);
        postRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postRecyclerView.setLayoutManager(linearLayoutManager);
        postRecyclerView.setItemViewCacheSize(20);
        postRecyclerView.setDrawingCacheEnabled(true);
        postRecyclerView.setItemAnimator(new DefaultItemAnimator());

        likesReference = FirebaseFirestore.getInstance();
        dislikeReference = FirebaseFirestore.getInstance();

        notificationReference = FirebaseFirestore.getInstance();
        uid = MainPage.uid;
        CollectionReference myPostsReference = FirebaseFirestore.getInstance().collection("Posting");
        final String userID = myIntent.getStringExtra("userID");
        boolean isUserClicked = myIntent.getBooleanExtra("isClicked", false);
        final com.google.firebase.firestore.Query query;

        if (isUserClicked) {
          query = myPostsReference.whereEqualTo("uid",userID);
            isUserClicked = false;
        }else {
            query = myPostsReference.whereEqualTo("uid",mAuth.getCurrentUser().getUid());
        }
        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                FirestoreRecyclerOptions<Post> response = new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query,Post.class)
                        .build();

                FirestoreRecyclerAdapter<Post,PostViewHolder> adapter = new FirestoreRecyclerAdapter<Post,PostViewHolder>(response) {
                    @Override
                    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_posts_row,parent,false);
                        return new PostViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(final PostViewHolder viewHolder, int position, Post model) {

                        final String post_key = getSnapshots().getSnapshot(position).getId();
                        viewHolder.setDescForAudio(model.getDescForAudio());
                        viewHolder.setDescForPhoto(model.getDescForPhoto());
                        viewHolder.setDescVideo(model.getDescVideo());
                        viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setPhotoPost(getApplicationContext(), model.getPhotoPost());
                        viewHolder.setVideoPost(getApplicationContext(), model.getVideoPost());
                        viewHolder.setAudioFile(getApplicationContext(), model.getAudioFile());
                        viewHolder.setCountry(getApplicationContext(),model.getCountry());
                        viewHolder.setLikeBtn(post_key,MyPostsActivity.this);
                        viewHolder.setNumberLikes(post_key,MyPostsActivity.this);
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setDislikeBtn(post_key,MyPostsActivity.this);
                        viewHolder.setNumberComments(post_key,MyPostsActivity.this);
                        viewHolder.setNumberDislikes(post_key,MyPostsActivity.this);
                        viewHolder.setClubLogo(getApplicationContext(), model.getClubLogo());
                        viewHolder.seekBar.setEnabled(true);


                        viewHolder.play_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {

                                viewHolder.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                                try {
                                    viewHolder.mPlayer.prepareAsync();
                                    viewHolder.mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            viewHolder.mPlayer.start();

                                            viewHolder.seekBar.setMax(viewHolder.mPlayer.getDuration());

                                            new Timer().scheduleAtFixedRate(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    viewHolder.seekBar.setProgress(viewHolder.mPlayer.getCurrentPosition());
                                                    viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                        @Override
                                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                            if (fromUser) {
                                                                viewHolder.mPlayer.seekTo(progress);
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


                                    viewHolder.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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
                                    viewHolder.mPlayer.start();
                                    pause_state = false;
                                }

                            }


                        });



                        viewHolder.pause_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pause_state = true;
                                play_state = false;
                                if (viewHolder.mPlayer.isPlaying() && pause_state)
                                    viewHolder.mPlayer.pause();
                                pause_state = false;

                            }
                        });

                        viewHolder.stop_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (viewHolder.mPlayer != null) {
                                    viewHolder.mPlayer.stop();
                                    viewHolder.seekBar.setMax(0);


                                }
                            }
                        });

                        viewHolder.layoutForNumberLikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent listUsername = new Intent(MyPostsActivity.this, Username_Likes_Activity.class);
                                listUsername.putExtra("post_key", post_key);
                                startActivity(listUsername);
                            }
                        });

                        viewHolder.layoutForNumberDislikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent listUsername = new Intent(MyPostsActivity.this, Username_Dislikes_Activity.class);
                                listUsername.putExtra("post_key", post_key);
                                startActivity(listUsername);
                            }
                        });


                        viewHolder.like_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                like_process = true;



                                likesReference.collection("Likes").document(post_key).collection("like-id").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        if (like_process) {


                                            if (documentSnapshots.exists()) {

                                                likesReference.collection("Likes").document(post_key).collection("like-id").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                                final DocumentReference newPost = likesReference.collection("Likes").document(post_key).collection("like-id").document(uid);
                                                newPost.set(userLikeInfo);


                                                FirebaseFirestore getIduserpost = postingDatabase;
                                                getIduserpost.collection("Posting").document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {



                                                        String userpostUID = dataSnapshot.getString("uid");

                                                        Map<String, Object> notifMap = new HashMap<>();
                                                        notifMap.put("action", "liked");
                                                        notifMap.put("uid", uid);
                                                        notifMap.put("seen", false);
                                                        notifMap.put("whatIS", "post");
                                                        notifMap.put("timestamp", FieldValue.serverTimestamp());
                                                        notifMap.put("post_key", post_key);
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




                        viewHolder.post_photo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openFullScreen = new Intent(MyPostsActivity.this, FullScreenImage.class);
                                String tag = (String) viewHolder.post_photo.getTag();
                                openFullScreen.putExtra("imageURL", tag);
                                startActivity(openFullScreen);
                            }
                        });


                        viewHolder.dislike_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dislike_process = true;



                                dislikeReference.collection("Dislikes").document(post_key).collection("dislike-id").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        if (dislike_process) {


                                            if (documentSnapshots.exists()) {
                                                dislikeReference.collection("Dislikes").document(post_key).collection("dislike-id").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                                final DocumentReference newPost = dislikeReference.collection("Dislikes").document(post_key).collection("dislike-id").document(uid);
                                                newPost.set(userDislikeInfo);


                                                FirebaseFirestore getIduserpost = postingDatabase;
                                                getIduserpost.collection("Posting").document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                                        String userpostUID = dataSnapshot.getString("uid");

                                                        Map<String, Object> notifMap = new HashMap<>();
                                                        notifMap.put("action", "disliked");
                                                        notifMap.put("uid", uid);
                                                        notifMap.put("seen", false);
                                                        notifMap.put("whatIS", "post");
                                                        notifMap.put("timestamp", FieldValue.serverTimestamp());
                                                        notifMap.put("post_key", post_key);
                                                        CollectionReference notifSet = notificationReference.collection("Notification").document(userpostUID).collection("notif-id");
                                                        notifSet.add(notifMap);

                                                    }

                                                });


                                                dislike_process = false;


                                            }
                                        }
                                    }
                                });


                                /*
                                dislikeReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dislike_process) {
                                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                                dislikeReference.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                dislike_process = false;


                                            } else {

                                                DatabaseReference newPost = dislikeReference.child(post_key).child(mAuth.getCurrentUser().getUid());

                                                newPost.child("username").setValue(MainPage.usernameInfo);
                                                newPost.child("photoProfile").setValue(MainPage.profielImage);

                                                DatabaseReference getIduserpost = postingDatabase;
                                                getIduserpost.child(post_key).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String userpostUID = String.valueOf(dataSnapshot.child("uid").getValue());

                                                        DatabaseReference notifSet = notificationReference.child(userpostUID).push();
                                                        notifSet.child("action").setValue("disliked");
                                                        notifSet.child("uid").setValue(uid);
                                                        notifSet.child("seen").setValue(false);
                                                        notifSet.child("whatIS").setValue("post");
                                                        notifSet.child("post_key").setValue(post_key);

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });


                                                dislike_process = false;


                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }); */

                            }
                        });

                        viewHolder.layoutForNumberComments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openCom = new Intent(MyPostsActivity.this, CommentsActivity.class);
                                openCom.putExtra("keyComment", post_key);
                                openCom.putExtra("profileComment", MainPage.profielImage);
                                openCom.putExtra("username", MainPage.usernameInfo);
                                startActivity(openCom);
                            }
                        });

                        viewHolder.openComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent openCom = new Intent(getApplicationContext(), CommentsActivity.class);
                                openCom.putExtra("keyComment", post_key);
                                openCom.putExtra("profileComment", MainPage.profielImage);
                                openCom.putExtra("username", MainPage.usernameInfo);
                                startActivity(openCom);
                            }
                        });


                        final FirebaseFirestore postingDatabaseProfile = FirebaseFirestore.getInstance();
                        postingDatabaseProfile.collection("Posting").document(post_key).addSnapshotListener(MyPostsActivity.this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(final DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.contains("uid")) {
                                        if (mAuth.getCurrentUser().getUid().equals(dataSnapshot.getString("uid"))) {

                                            viewHolder.arrow_down.setVisibility(View.VISIBLE);

                                            viewHolder.arrow_down.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    final String[] items = {"Edit post", "Delete post", "Cancel"};
                                                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(viewHolder.mView.getContext());
                                                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            if (items[i].equals("Edit post")) {
                                                                Intent openSinglePost = new Intent(getApplicationContext(), SinglePostViewActivity.class);
                                                                openSinglePost.putExtra("post_id", post_key);
                                                                startActivity(openSinglePost);
                                                            } else if (items[i].equals("Delete post")) {


                                                                postingDatabaseProfile.collection("Posting").document(post_key).delete().addOnSuccessListener(MyPostsActivity.this, new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_LONG).show();

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
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
                                        } else viewHolder.arrow_down.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }

                        });




                    }





                };
                adapter.notifyDataSetChanged();
                postRecyclerView.setAdapter(adapter);
                adapter.startListening();

            }
        });

    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
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


        FirebaseFirestore likeReference, dislikeReference;
        FirebaseFirestore userReference;
        FirebaseFirestore numberCommentsReference;
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
        int numberLikes;
        int numberDislikes;
        RelativeLayout layoutForNumberLikes;
        TextView timeAgoTextView;
        RelativeLayout layoutForNumberComments;
        TextView likeBtnTxt;
        RelativeLayout layoutForNumberDislikes;
        TextView dislikeBtnText;
        RelativeLayout postWithBackgroundLayout;
        TextView posttextWithbackground;

        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            play_button = (Button) mView.findViewById(R.id.play_button_my_posts);
            pause_button = (Button) mView.findViewById(R.id.pause_button_my_posts);
            stop_button = (Button) mView.findViewById(R.id.stop_button_my_posts);

            mediaController = new MediaController(mView.getContext());

            videoView = (cn.jzvd.JZVideoPlayerStandard) mView.findViewById(R.id.posted_video_my_posts);
            post_photo = (ImageView) mView.findViewById(R.id.posted_image_my_posts);
            audioLayout = (RelativeLayout) mView.findViewById(R.id.layout_for_audio_player_my_posts);
            mPlayer = new MediaPlayer();
            progressDialog = new ProgressDialog(mView.getContext());
            seekBar = (SeekBar) mView.findViewById(R.id.audio_seek_bar_my_posts);
            like_button = (ImageView) mView.findViewById(R.id.like_button_my_posts);
            dislike_button = (ImageView) mView.findViewById(R.id.dislike_button_my_posts);
            numberofLikes = (TextView) mView.findViewById(R.id.number_of_likes_my_posts);
            numberOfDislikes = (TextView) mView.findViewById(R.id.number_of_dislikes_my_posts);
            single_post_layout = (RelativeLayout) mView.findViewById(R.id.layout_for_only_post_my_posts);

            arrow_down = (ImageView) mView.findViewById(R.id.down_arrow_my_posts);
            layoutPhotoText = (RelativeLayout) mView.findViewById(R.id.layout_for_text_image_my_posts);
            layoutPhoto = (RelativeLayout) mView.findViewById(R.id.layout_for_image_my_posts);
            layoutAudioText = (RelativeLayout) mView.findViewById(R.id.layout_audio_textview_my_posts);
            layoutVideo = (FrameLayout) mView.findViewById(R.id.framelayout_my_posts);
            //loadPhoto = (ProgressBar) mView.findViewById(R.id.post_photo_bar_load);
            layoutVideoText = (RelativeLayout) mView.findViewById(R.id.layout_for_video_text_my_posts);

            likeReference = FirebaseFirestore.getInstance(); //.child("Likes");
            dislikeReference = FirebaseFirestore.getInstance(); //.child("Dislikes");
            userReference = FirebaseFirestore.getInstance(); //.child("Users");
            numberCommentsReference = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            openComment = (TextView) mView.findViewById(R.id.comment_something_my_posts);
            post_username = (TextView) mView.findViewById(R.id.username_wall_my_posts);
            post_profile_image = (ImageView) mView.findViewById(R.id.profile_image_wall_my_posts);
            post_clubLogo = (CircleImageView) mView.findViewById(R.id.clubLogoPost_my_posts);
            postBackgroundImage = (ImageView) mView.findViewById(R.id.image_post_background_my_posts);
            comments = (TextView) mView.findViewById(R.id.comments_textview_my_posts);
            numberComments = (TextView) mView.findViewById(R.id.number_comments_my_posts);
            layoutForNumberLikes = (RelativeLayout) mView.findViewById(R.id.layoutlikenumberMyPosts);
            timeAgoTextView = (TextView) mView.findViewById(R.id.postAgoTimeMyPostst);
            layoutForNumberComments = (RelativeLayout) mView.findViewById(R.id.layoutcommentsNumberMyPosts);

            likeBtnTxt = (TextView) mView.findViewById(R.id.likesomethingMyPosts);
            layoutForNumberDislikes = (RelativeLayout) mView.findViewById(R.id.layoutnumberdislikesMyPosts);
            dislikeBtnText = (TextView) mView.findViewById(R.id.disliketekstMyPosts);
            postWithBackgroundLayout = (RelativeLayout) mView.findViewById(R.id.layout_for_only_postWithBackgroundMyPosts);
            posttextWithbackground = (TextView) mView.findViewById(R.id.post_text_main_pageWithBackgroundMyPOsts);
        }

        public void setNumberLikes(final String post_key, Activity activity) {

            CollectionReference col = likeReference.collection("Likes").document(post_key).collection("like-id");
            col.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (e == null) {
                        if (documentSnapshots.isEmpty()) {
                            layoutForNumberLikes.setVisibility(View.GONE);
                        }
                        numberLikes = documentSnapshots.size();
                        if (numberLikes == 0) {
                            numberofLikes.setText("");
                            layoutForNumberLikes.setVisibility(View.GONE);
                        } else {
                            numberofLikes.setText(String.valueOf(numberLikes));
                            layoutForNumberLikes.setVisibility(View.VISIBLE);
                        }
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
                timeAgoTextView.setText(lastStringTime);
            }
        }

        public void setNumberComments(String post_key, Activity activity) {
            numberCommentsReference.collection("Comments").document(post_key).collection("comment-id").addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (e == null) {
                        if (documentSnapshots.isEmpty()) {
                            comments.setVisibility(View.GONE);
                            layoutForNumberComments.setVisibility(View.GONE);
                        }
                        int numberOfComments = documentSnapshots.getDocuments().size();


                        if (numberOfComments == 0) {

                            comments.setVisibility(View.GONE);
                            numberComments.setText("");
                            layoutForNumberComments.setVisibility(View.GONE);
                        } else if (numberOfComments == 1) {

                            comments.setText("Comment");
                            comments.setVisibility(View.VISIBLE);
                            layoutForNumberComments.setVisibility(View.VISIBLE);
                            numberComments.setText(String.valueOf(numberOfComments));
                        } else {
                            comments.setText("Comments");
                            comments.setVisibility(View.VISIBLE);
                            layoutForNumberComments.setVisibility(View.VISIBLE);
                            numberComments.setText(String.valueOf(numberOfComments));

                        }
                    }
                }
            });
        }


        public void setLikeBtn(final String post_key, Activity activity) {
            if (mAuth.getCurrentUser() != null) {
                String uid = mAuth.getCurrentUser().getUid();
                Log.i("uid", uid);
                final DocumentReference doc = likeReference.collection("Likes").document(post_key).collection("like-id").document(uid);
                doc.addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                        if (documentSnapshot.exists()) {


                            dislike_button.setClickable(false);
                            like_button.setActivated(true);
                            if (like_button.isActivated()) {

                                like_button.setImageResource(R.drawable.thumbup);
                                likeBtnTxt.setText("Liked");
                                Log.i(" buttonLIKE ", "activate");

                            }
                            Log.i("key like ima ", "KLIKNO");


                        } else {
                            dislike_button.setClickable(true);
                            like_button.setActivated(false);
                            if (!like_button.isActivated()) {
                                like_button.setImageResource(R.drawable.thumbupgreen);
                                likeBtnTxt.setText("Like");
                                Log.i(" buttonLIKE ", "deactivate");

                            }
                            Log.i("key like nema ", "KLIKNO");
                        }

                    }
                });
            }
        }
        public void setNumberDislikes(String post_key, Activity activity) {

            CollectionReference col = dislikeReference.collection("Dislikes").document(post_key).collection("dislike-id");
            col.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (e == null) {
                        if (documentSnapshots.isEmpty()) {
                            layoutForNumberDislikes.setVisibility(View.GONE);
                        }
                        numberDislikes = documentSnapshots.getDocuments().size();


                        if (numberDislikes == 0) {
                            numberOfDislikes.setText("");
                            layoutForNumberDislikes.setVisibility(View.GONE);
                        } else {
                            numberOfDislikes.setText(String.valueOf(numberDislikes));
                            layoutForNumberDislikes.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });
        }


        public void setDislikeBtn(final String post_key, Activity activity) {
            String uid = mAuth.getCurrentUser().getUid();
            final DocumentReference doc = dislikeReference.collection("Dislikes").document(post_key).collection("dislike-id").document(uid);
            doc.addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {
                        dislike_button.setActivated(true);
                        like_button.setClickable(false);

                        if (dislike_button.isActivated()) {
                            dislike_button.setImageResource(R.drawable.thumbdown);
                            dislikeBtnText.setText("Disliked");

                        }
                        Log.i("key dislike ima ", post_key);

                    } else {
                        dislike_button.setActivated(false);
                        like_button.setClickable(true);
                        if (!dislike_button.isActivated()) {
                            dislike_button.setImageResource(R.drawable.thumbdowngreen);
                            dislikeBtnText.setText("Dislike");
                        }
                        Log.i("key dislike nema ", post_key);
                    }
                }
            });
        }


        public void setDesc(String desc) {
            if (desc != null) {
                TextView single_post = (TextView) mView.findViewById(R.id.post_text_main_page_my_posts);
                single_post_layout.setVisibility(View.VISIBLE);
                single_post.setText(desc);
            } else {
                single_post_layout.setVisibility(View.GONE);
            }
        }


        public void setDescForAudio(String descForAudio) {

            TextView post_desc_for_audio = (TextView) mView.findViewById(R.id.audio_textview_my_posts);
            if (descForAudio != null) {
                layoutAudioText.setVisibility(View.VISIBLE);
                post_desc_for_audio.setText(descForAudio);
            } else {
                layoutAudioText.setVisibility(View.GONE);
                post_desc_for_audio.setVisibility(View.GONE);
            }
        }

        public void setDescForPhoto(String descForPhoto) {

            TextView post_desc_for_photo = (TextView) mView.findViewById(R.id.text_for_image_my_posts);

            if (descForPhoto != null) {
                layoutPhotoText.setVisibility(View.VISIBLE);
                post_desc_for_photo.setText(descForPhoto);
            } else {
                layoutPhotoText.setVisibility(View.GONE);
            }
        }

        public void setDescVideo(String descVideo) {
            TextView post_desc_for_video = (TextView) mView.findViewById(R.id.text_for_video_my_posts);

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

            Picasso.with(ctx).load(profileImage).into(post_profile_image, new Callback() {
                @Override
                public void onSuccess() {
                    ProgressBar loadImage = mView.findViewById(R.id.loadImageProgressWallMyPosts);
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
                postBackgroundImage.setAlpha(0.99f);
            }
        }


        public void setPhotoPost(Context ctx, String photoPost) {


            if (photoPost != null) {
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
                                ProgressBar loadImageProgress = mView.findViewById(R.id.loadImageProgressWallImageMyPosts);
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
    }
}
