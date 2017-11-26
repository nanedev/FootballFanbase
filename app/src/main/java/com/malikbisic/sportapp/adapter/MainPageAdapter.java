package com.malikbisic.sportapp.adapter;

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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.caverock.androidsvg.SVG;
import com.firebase.ui.FirebaseUI;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.CommentsActivity;
import com.malikbisic.sportapp.activity.FullScreenImage;
import com.malikbisic.sportapp.activity.MainPage;
import com.malikbisic.sportapp.activity.OnLoadMoreListener;
import com.malikbisic.sportapp.activity.ProfileFragment;
import com.malikbisic.sportapp.activity.SearchableCountry;
import com.malikbisic.sportapp.activity.SinglePostViewActivity;
import com.malikbisic.sportapp.activity.UserProfileActivity;
import com.malikbisic.sportapp.activity.Username_Dislikes_Activity;
import com.malikbisic.sportapp.activity.Username_Likes_Activity;
import com.malikbisic.sportapp.model.Post;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.UsersModel;
import com.malikbisic.sportapp.viewHolder.PostViewHolder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by korisnik on 26/09/2017.
 */

public class MainPageAdapter extends RecyclerView.Adapter<MainPageAdapter.PostViewHolder> {

    List<Post> postList;
    Context ctx;
    Activity activity;

    public static boolean photoSelected;
    public static String usernameInfo;
    public static String profielImage;
    private FirebaseFirestore mReference, postingDatabase, likesReference, dislikeReference, notificationReference;

    private static final int PHOTO_OPEN = 1;
    private static final int VIDEO_OPEN = 2;
    boolean pause_state;
    boolean play_state;

    Date currentDateOfUserMainPage;
    String getDateFromDatabaseMainPage;
    boolean like_process = false;
    boolean dislike_process = false;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore profileUsers;
    UsersModel model;
    DatabaseReference mUsers;
    boolean isPremium;
    String post_key;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    String key;


    public MainPageAdapter(List<Post> postList, Context ctx, Activity activity, RecyclerView recyclerView, String key) {
        this.postList = postList;
        this.ctx = ctx;
        this.activity = activity;
        this.key = key;
    }


    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_row, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder viewHolder, final int position) {


        Post model = postList.get(position);

        postingDatabase = FirebaseFirestore.getInstance();
        notificationReference = FirebaseFirestore.getInstance();//.getReference().child("Notification");
        likesReference = FirebaseFirestore.getInstance(); //.getReference().child("Likes");
        dislikeReference = FirebaseFirestore.getInstance(); //.child("Dislikes");
        profileUsers = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getCurrentUser().getUid();

        //DocumentSnapshot refDoc = postingDatabase.collection("Posting").document(model.getKey());

        final String post_key = model.getKey();
        viewHolder.setDescForAudio(model.getDescForAudio());
        viewHolder.setDescForPhoto(model.getDescForPhoto());
        viewHolder.setDescVideo(model.getDescVideo());
        viewHolder.setProfileImage(ctx, model.getProfileImage());
        viewHolder.setUsername(model.getUsername());
        viewHolder.setPhotoPost(ctx, model.getPhotoPost());
        viewHolder.setVideoPost(ctx, model.getVideoPost());
        viewHolder.setAudioFile(ctx, model.getAudioFile());
        viewHolder.setLikeBtn(post_key);
        viewHolder.setNumberLikes(post_key);
        viewHolder.setDesc(model.getDesc());
        viewHolder.setDislikeBtn(post_key);
        viewHolder.setNumberComments(post_key);
        viewHolder.setNumberDislikes(post_key);
        viewHolder.setClubLogo(ctx, model.getClubLogo());
        viewHolder.setCountry(ctx, model.getCountry());


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

                /*viewHolder.openSinglePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openSinglePost = new Intent(ctx, SinglePostViewActivity.class);
                        openSinglePost.putExtra("post_id", post_key);
                        activity.startActivity(openSinglePost);
                        Log.i("linkPost", link_post);
                    }
                });*/


        viewHolder.numberofLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listUsername = new Intent(ctx, Username_Likes_Activity.class);
                listUsername.putExtra("post_key", post_key);
                listUsername.putExtra("openActivityToBack", "mainPage");
                activity.startActivity(listUsername);
            }
        });

        viewHolder.numberOfDislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listUsername = new Intent(ctx, Username_Dislikes_Activity.class);
                listUsername.putExtra("post_key", post_key);
                listUsername.putExtra("openActivityToBack", "mainPage");
                activity.startActivity(listUsername);
            }
        });

        viewHolder.like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like_process = true;


                likesReference.collection("Likes").document(post_key).collection(uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (like_process) {


                            if (!documentSnapshots.isEmpty()) {

                                likesReference.collection("Likes").document(post_key).collection(uid).document(String.valueOf(FieldPath.documentId())).delete();
                                like_process = false;

                                Log.i("nema like", "NEMA");

                            } else {
                                Log.i("ima like", "IMA");
                                Map<String, Object> userLikeInfo = new HashMap<>();
                                userLikeInfo.put("username", MainPage.usernameInfo);
                                userLikeInfo.put("photoProfile", MainPage.profielImage);

                                final CollectionReference newPost = likesReference.collection("Likes").document(post_key).collection(uid);
                                newPost.add(userLikeInfo);


                                FirebaseFirestore getIduserpost = postingDatabase;
                                getIduserpost.collection(post_key).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {

                                        for (DocumentSnapshot snapshot : dataSnapshot.getDocuments()) {

                                            String userpostUID = snapshot.getString("uid");

                                            Map<String, Object> notifMap = new HashMap<>();
                                            notifMap.put("action", "liked");
                                            notifMap.put("uid", uid);
                                            notifMap.put("seen", false);
                                            notifMap.put("whatIS", "post");
                                            notifMap.put("post_key", post_key);
                                            DocumentReference notifSet = notificationReference.collection("Notification").document(userpostUID);
                                            notifSet.set(notifMap);

                                        }

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


                        /*.new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (like_process) {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        likesReference.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        like_process = false;


                                    } else {

                                        final DatabaseReference newPost = likesReference.child(post_key).child(mAuth.getCurrentUser().getUid());

                                        newPost.child("username").setValue(MainPage.usernameInfo);
                                        newPost.child("photoProfile").setValue(MainPage.profielImage);

                                        DatabaseReference getIduserpost = postingDatabase;
                                        getIduserpost.child(post_key).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String userpostUID = String.valueOf(dataSnapshot.child("uid").getValue());

                                                DatabaseReference notifSet = notificationReference.child(userpostUID).push();
                                                notifSet.child("action").setValue("like");
                                                notifSet.child("uid").setValue(uid);
                                                notifSet.child("seen").setValue(false);
                                                notifSet.child("whatIS").setValue("post");
                                                notifSet.child("post_key").setValue(post_key);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        like_process = false;


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.i("error", databaseError.getMessage());

                            }
                        });

                    }
                }); */


        viewHolder.post_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openFullScreen = new Intent(ctx, FullScreenImage.class);
                String tag = (String) viewHolder.post_photo.getTag();
                openFullScreen.putExtra("imageURL", tag);
                activity.startActivity(openFullScreen);
            }
        });

        viewHolder.dislike_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike_process = true;


                dislikeReference.collection("Dislikes").document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (dislike_process) {


                            if (e == null) {
                                if (documentSnapshots.contains(mAuth.getCurrentUser().getUid())) {

                                    dislikeReference.document(mAuth.getCurrentUser().getUid()).delete();
                                    dislike_process = false;


                                } else {

                                    Map<String, Object> userDislikeInfo = new HashMap<>();
                                    userDislikeInfo.put("username", MainPage.usernameInfo);
                                    userDislikeInfo.put("photoProfile", MainPage.profielImage);

                                    final DocumentReference newPost = dislikeReference.collection("Dislikes").document(mAuth.getCurrentUser().getUid());
                                    newPost.set(userDislikeInfo);


                                    FirebaseFirestore getIduserpost = postingDatabase;
                                    getIduserpost.collection(post_key).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {

                                            for (DocumentSnapshot snapshot : dataSnapshot.getDocuments()) {

                                                String userpostUID = snapshot.getString("uid");

                                                Map<String, Object> notifMap = new HashMap<>();
                                                notifMap.put("action", "disliked");
                                                notifMap.put("uid", uid);
                                                notifMap.put("seen", false);
                                                notifMap.put("whatIS", "post");
                                                notifMap.put("post_key", post_key);
                                                DocumentReference notifSet = notificationReference.collection("Notification").document(userpostUID);
                                                notifSet.set(notifMap);

                                            }

                                        }


                                    });


                                    dislike_process = false;


                                }
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

        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openCom = new Intent(ctx, CommentsActivity.class);
                openCom.putExtra("keyComment", post_key);
                openCom.putExtra("profileComment", MainPage.profielImage);
                openCom.putExtra("username", MainPage.usernameInfo);
                activity.startActivity(openCom);
            }
        });

        viewHolder.numberComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openCom = new Intent(ctx, CommentsActivity.class);
                openCom.putExtra("keyComment", post_key);
                openCom.putExtra("profileComment", MainPage.profielImage);
                openCom.putExtra("username", MainPage.usernameInfo);
                activity.startActivity(openCom);
            }
        });


        viewHolder.openComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openCom = new Intent(ctx, CommentsActivity.class);
                openCom.putExtra("keyComment", post_key);
                openCom.putExtra("profileComment", MainPage.profielImage);
                openCom.putExtra("username", MainPage.usernameInfo);
                activity.startActivity(openCom);
            }
        });

        final FirebaseFirestore postingDatabaseProfile = FirebaseFirestore.getInstance();
        postingDatabaseProfile.collection("Posting").document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

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
                                                Intent openSinglePost = new Intent(ctx, SinglePostViewActivity.class);
                                                openSinglePost.putExtra("post_id", post_key);
                                                activity.startActivity(openSinglePost);
                                            } else if (items[i].equals("Delete post")) {

                                                postingDatabaseProfile.document(post_key).delete();
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
            }

        });


        viewHolder.post_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = viewHolder.post_username.getText().toString().trim();
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

                                    ProfileFragment profileFragment = new ProfileFragment();

                                    FragmentTransaction manager = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

                                    manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                            R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                    Log.i("tacno", "true");

                                } else {

                                    DocumentReference profileInfo = profileUsers.document(uid);

                                    profileInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                            Intent openUserProfile = new Intent(ctx, UserProfileActivity.class);
                                            openUserProfile.putExtra("userID", uid);
                                            activity.startActivity(openUserProfile);
                                        }
                                    });
                                }
                            }
                        }
                    }

                });
            }
        });

        viewHolder.post_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = viewHolder.post_username.getText().toString().trim();
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

                                    ProfileFragment profileFragment = new ProfileFragment();

                                    FragmentTransaction manager = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

                                    manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                            R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                    Log.i("tacno", "true");

                                } else {

                                    DocumentReference profileInfo = profileUsers.document(uid);

                                    profileInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                            Intent openUserProfile = new Intent(ctx, UserProfileActivity.class);
                                            openUserProfile.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                            openUserProfile.putExtra("userID", uid);
                                            activity.startActivity(openUserProfile);
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


    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return postList.size();
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
        FirebaseDatabase database;
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


        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            play_button = (Button) mView.findViewById(R.id.play_button);
            pause_button = (Button) mView.findViewById(R.id.pause_button);
            stop_button = (Button) mView.findViewById(R.id.stop_button);

            mediaController = new MediaController(mView.getContext());

            videoView = (cn.jzvd.JZVideoPlayerStandard) mView.findViewById(R.id.posted_video);
            post_photo = (ImageView) mView.findViewById(R.id.posted_image);
            audioLayout = (RelativeLayout) mView.findViewById(R.id.layout_for_audio_player);
            mPlayer = new MediaPlayer();
            progressDialog = new ProgressDialog(mView.getContext());
            seekBar = (SeekBar) mView.findViewById(R.id.audio_seek_bar);
            like_button = (ImageView) mView.findViewById(R.id.like_button);
            dislike_button = (ImageView) mView.findViewById(R.id.dislike_button);
            numberofLikes = (TextView) mView.findViewById(R.id.number_of_likes);
            numberOfDislikes = (TextView) mView.findViewById(R.id.number_of_dislikes);
            single_post_layout = (RelativeLayout) mView.findViewById(R.id.layout_for_only_post);

            arrow_down = (ImageView) mView.findViewById(R.id.down_arrow);
            layoutPhotoText = (RelativeLayout) mView.findViewById(R.id.layout_for_text_image);
            layoutPhoto = (RelativeLayout) mView.findViewById(R.id.layout_for_image);
            layoutAudioText = (RelativeLayout) mView.findViewById(R.id.layout_audio_textview);
            layoutVideo = (FrameLayout) mView.findViewById(R.id.framelayout);
            //loadPhoto = (ProgressBar) mView.findViewById(R.id.post_photo_bar_load);
            layoutVideoText = (RelativeLayout) mView.findViewById(R.id.layout_for_video_text);
            database = FirebaseDatabase.getInstance();
            likeReference = FirebaseFirestore.getInstance(); //.child("Likes");
            dislikeReference = FirebaseFirestore.getInstance(); //.child("Dislikes");
            userReference = FirebaseFirestore.getInstance(); //.child("Users");
            numberCommentsReference = FirebaseFirestore.getInstance(); //.child("Comments");
            mAuth = FirebaseAuth.getInstance();
            openComment = (TextView) mView.findViewById(R.id.comment_something);
            post_username = (TextView) mView.findViewById(R.id.username_wall);
            post_profile_image = (ImageView) mView.findViewById(R.id.profile_image_wall);
            post_clubLogo = (CircleImageView) mView.findViewById(R.id.clubLogoPost);
            postBackgroundImage = (ImageView) mView.findViewById(R.id.image_post_background);
            comments = (TextView) mView.findViewById(R.id.comments_textview);
            numberComments = (TextView) mView.findViewById(R.id.number_comments);


        }

        public void checkPremium() {


        }

        public void onClick(View view) {


        }

        public void setNumberLikes(String post_key) {

            likeReference.collection("Likes").document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                    if (dataSnapshot.exists()) {
                        long numberLikes = dataSnapshot.getData().size();
                        if (numberLikes == 0) {
                            numberofLikes.setText("");
                        } else {
                            numberofLikes.setText(String.valueOf(numberLikes));
                        }

                    }
                }
            });
        }


        public void setNumberComments(String post_key) {

            numberCommentsReference.collection("Comments").document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                    if (dataSnapshot.exists()) {
                        long numberOfComments = dataSnapshot.getData().size();

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
                }
            });
        }


        public void setLikeBtn(final String post_key) {
            likeReference.collection("Likes").document(post_key).collection(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshot, FirebaseFirestoreException e) {

                        if (documentSnapshot.size() > 0) {
                            dislike_button.setClickable(false);
                            like_button.setActivated(true);
                            Log.i("key like ima ", post_key);

                        } else if (documentSnapshot.size() < 1){
                            dislike_button.setClickable(true);
                            like_button.setActivated(false);
                            Log.i("key like nema ", post_key);
                        }
                }
            });
        }

        public void setNumberDislikes(String post_key) {

            dislikeReference.collection("Dislikes").document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                    if (dataSnapshot.exists()) {
                        long numberDislikes = dataSnapshot.getData().size();
                        if (numberDislikes == 0) {
                            numberOfDislikes.setText("");
                        } else {
                            numberOfDislikes.setText(String.valueOf(numberDislikes));
                        }

                    }

                }
            });
        }


        public void setDislikeBtn(final String post_key) {
            dislikeReference.collection("Dislikes").document(post_key).collection(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshot, FirebaseFirestoreException e) {
                    {
                        if (!documentSnapshot.isEmpty()) {
                            dislike_button.setClickable(true);
                            like_button.setActivated(false);
                            Log.i("key like ima ", post_key);

                        } else {
                            dislike_button.setClickable(false);
                            like_button.setActivated(true);
                            Log.i("key like nema ", post_key);
                        }
                    }
                }
            });
        }

        public void setDesc(String desc) {
            if (desc != null) {
                TextView single_post = (TextView) mView.findViewById(R.id.post_text_main_page);
                single_post_layout.setVisibility(View.VISIBLE);
                single_post.setText(desc);
            } else {
                single_post_layout.setVisibility(View.GONE);
            }
        }


        public void setDescForAudio(String descForAudio) {

            TextView post_desc_for_audio = (TextView) mView.findViewById(R.id.audio_textview);
            if (descForAudio != null) {
                layoutAudioText.setVisibility(View.VISIBLE);
                post_desc_for_audio.setText(descForAudio);
            } else {
                layoutAudioText.setVisibility(View.GONE);
                post_desc_for_audio.setVisibility(View.GONE);
            }
        }

        public void setDescForPhoto(String descForPhoto) {

            TextView post_desc_for_photo = (TextView) mView.findViewById(R.id.text_for_image);

            if (descForPhoto != null) {
                layoutPhotoText.setVisibility(View.VISIBLE);
                post_desc_for_photo.setText(descForPhoto);
            } else {
                layoutPhotoText.setVisibility(View.GONE);
            }
        }

        public void setDescVideo(String descVideo) {
            TextView post_desc_for_video = (TextView) mView.findViewById(R.id.text_for_video);

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
                        .override(720, 640)
                        .centerCrop()
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
                    videoView.setUp(videoPost, cn.jzvd.JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "proba");
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

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}
