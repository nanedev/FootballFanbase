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
import android.widget.Toast;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.CommentsActivity;
import com.malikbisic.sportapp.activity.FullScreenImage;
import com.malikbisic.sportapp.activity.GetTimeAgo;
import com.malikbisic.sportapp.activity.MainPage;
import com.malikbisic.sportapp.activity.OnLoadMoreListener;
import com.malikbisic.sportapp.activity.PostingTimeAgo;
import com.malikbisic.sportapp.activity.ProfileFragment;
import com.malikbisic.sportapp.activity.SearchableCountry;
import com.malikbisic.sportapp.activity.SinglePostViewActivity;
import com.malikbisic.sportapp.activity.UserProfileActivity;
import com.malikbisic.sportapp.activity.Username_Dislikes_Activity;
import com.malikbisic.sportapp.activity.Username_Likes_Activity;
import com.malikbisic.sportapp.model.Post;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.UsersModel;
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

public class MainPageAdapter extends RecyclerView.Adapter {

    List<Post> postList;
    Context ctx;
    Activity activity;
    String uid;
    boolean value;

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
    private static final int ITEM_VIEW = 0;
    private static final int ITEM_LOADING = 1;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    String key;
    LayoutInflater mLInflater;
    boolean isLoading;


    public MainPageAdapter(List<Post> postList, Context ctx, Activity activity, RecyclerView recyclerView, String key) {
        this.postList = postList;
        this.ctx = ctx;
        this.activity = activity;
        this.key = key;
        this.value = value;
        mLInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                    lastVisibleItem = llm.findLastVisibleItemPosition();
                    totalItemCount = llm.getItemCount();

                    if(!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold))
                    {
                        if(onLoadMoreListener != null)
                        {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading =true;
                    }

                }
            });

        }
    }

    public void refreshAdapter(boolean value, List<Post> postList) {
        this.value = value;
        this.postList = postList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position) != null ? ITEM_VIEW : ITEM_LOADING;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == ITEM_VIEW) {
            View v = mLInflater.inflate(R.layout.wall_row, parent, false);
            return new PostViewHolder(v);
        } else if (viewType == ITEM_LOADING) {
            View v1 = mLInflater.inflate(R.layout.progressbar_item, parent, false);
            return new ProgressViewHolder(v1);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        int getViewType = holder.getItemViewType();
        final Post model = postList.get(position);

        postingDatabase = FirebaseFirestore.getInstance();
        notificationReference = FirebaseFirestore.getInstance();//.getReference().child("Notification");
        likesReference = FirebaseFirestore.getInstance(); //.getReference().child("Likes");
        dislikeReference = FirebaseFirestore.getInstance(); //.child("Dislikes");
        profileUsers = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }

        //DocumentSnapshot refDoc = postingDatabase.collection("Posting").document(model.getKey());
        if (getViewType == ITEM_VIEW) {

            final String post_key = model.getKey();
            ((MainPageAdapter.PostViewHolder) holder).setDescForAudio(model.getDescForAudio());
            ((MainPageAdapter.PostViewHolder) holder).setDescForPhoto(model.getDescForPhoto());
            ((MainPageAdapter.PostViewHolder) holder).setDescVideo(model.getDescVideo());
            ((MainPageAdapter.PostViewHolder) holder).setProfileImage(ctx, model.getProfileImage());
            ((MainPageAdapter.PostViewHolder) holder).setUsername(model.getUsername());
            ((MainPageAdapter.PostViewHolder) holder).setPhotoPost(ctx, model.getPhotoPost());
            ((MainPageAdapter.PostViewHolder) holder).setVideoPost(ctx, model.getVideoPost());
            ((MainPageAdapter.PostViewHolder) holder).setAudioFile(ctx, model.getAudioFile());
            ((MainPageAdapter.PostViewHolder) holder).setLikeBtn(post_key, activity);
            ((MainPageAdapter.PostViewHolder) holder).setNumberLikes(post_key, activity);
            ((MainPageAdapter.PostViewHolder) holder).setDesc(model.getDesc());
            ((MainPageAdapter.PostViewHolder) holder).setDislikeBtn(post_key, activity);
            ((MainPageAdapter.PostViewHolder) holder).setNumberComments(post_key, activity);
            ((MainPageAdapter.PostViewHolder) holder).setNumberDislikes(post_key, activity);
            ((MainPageAdapter.PostViewHolder) holder).setClubLogo(ctx, model.getClubLogo());
            ((MainPageAdapter.PostViewHolder) holder).setCountry(ctx, model.getCountry());
            ((MainPageAdapter.PostViewHolder) holder).setTimeAgo(model.getTime(), ctx);


            ((MainPageAdapter.PostViewHolder) holder).seekBar.setEnabled(true);
            ((MainPageAdapter.PostViewHolder) holder).play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    ((MainPageAdapter.PostViewHolder) holder).mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    try {
                        ((MainPageAdapter.PostViewHolder) holder).mPlayer.prepareAsync();
                        ((MainPageAdapter.PostViewHolder) holder).mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                ((MainPageAdapter.PostViewHolder) holder).mPlayer.start();

                                ((MainPageAdapter.PostViewHolder) holder).seekBar.setMax(((MainPageAdapter.PostViewHolder) holder).mPlayer.getDuration());

                                new Timer().scheduleAtFixedRate(new TimerTask() {
                                    @Override
                                    public void run() {
                                        ((MainPageAdapter.PostViewHolder) holder).seekBar.setProgress(((MainPageAdapter.PostViewHolder) holder).mPlayer.getCurrentPosition());
                                        ((MainPageAdapter.PostViewHolder) holder).seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                            @Override
                                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                if (fromUser) {
                                                    ((MainPageAdapter.PostViewHolder) holder).mPlayer.seekTo(progress);
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


                        ((MainPageAdapter.PostViewHolder) holder).mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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
                        ((MainPageAdapter.PostViewHolder) holder).mPlayer.start();
                        pause_state = false;
                    }

                }


            });


            ((MainPageAdapter.PostViewHolder) holder).pause_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pause_state = true;
                    play_state = false;
                    if (((MainPageAdapter.PostViewHolder) holder).mPlayer.isPlaying() && pause_state)
                        ((MainPageAdapter.PostViewHolder) holder).mPlayer.pause();
                    pause_state = false;

                }
            });

            ((MainPageAdapter.PostViewHolder) holder).stop_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (((MainPageAdapter.PostViewHolder) holder).mPlayer != null) {
                        ((MainPageAdapter.PostViewHolder) holder).mPlayer.stop();
                        ((MainPageAdapter.PostViewHolder) holder).seekBar.setMax(0);


                    }
                }
            });

                /*((MainPageAdapter.PostViewHolder) holder).openSinglePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openSinglePost = new Intent(ctx, SinglePostViewActivity.class);
                        openSinglePost.putExtra("post_id", post_key);
                        activity.startActivity(openSinglePost);
                        Log.i("linkPost", link_post);
                    }
                });*/


            ((MainPageAdapter.PostViewHolder) holder).numberofLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent listUsername = new Intent(ctx, Username_Likes_Activity.class);
                    listUsername.putExtra("post_key", post_key);
                    listUsername.putExtra("openActivityToBack", "mainPage");
                    activity.startActivity(listUsername);
                }
            });

            ((MainPageAdapter.PostViewHolder) holder).numberOfDislikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent listUsername = new Intent(ctx, Username_Dislikes_Activity.class);
                    listUsername.putExtra("post_key", post_key);
                    listUsername.putExtra("openActivityToBack", "mainPage");
                    activity.startActivity(listUsername);
                }
            });

            ((MainPageAdapter.PostViewHolder) holder).like_button.setOnClickListener(new View.OnClickListener() {
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


            ((MainPageAdapter.PostViewHolder) holder).post_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openFullScreen = new Intent(ctx, FullScreenImage.class);
                    String tag = (String) ((MainPageAdapter.PostViewHolder) holder).post_photo.getTag();
                    openFullScreen.putExtra("postKey", post_key);
                    openFullScreen.putExtra("imageURL", tag);
                    openFullScreen.putExtra("title", model.getDescForPhoto());
                    activity.startActivity(openFullScreen);
                }
            });

            ((MainPageAdapter.PostViewHolder) holder).dislike_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dislike_process = true;
               /* ((MainPageAdapter.PostViewHolder) holder).setNumberDislikes(post_key, activity);*/


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

            ((MainPageAdapter.PostViewHolder) holder).comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openCom = new Intent(ctx, CommentsActivity.class);
                    openCom.putExtra("keyComment", post_key);
                    openCom.putExtra("profileComment", MainPage.profielImage);
                    openCom.putExtra("username", MainPage.usernameInfo);
                    activity.startActivity(openCom);
                }
            });

            ((MainPageAdapter.PostViewHolder) holder).numberComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openCom = new Intent(ctx, CommentsActivity.class);
                    openCom.putExtra("keyComment", post_key);
                    openCom.putExtra("profileComment", MainPage.profielImage);
                    openCom.putExtra("username", MainPage.usernameInfo);
                    activity.startActivity(openCom);
                }
            });


            ((MainPageAdapter.PostViewHolder) holder).openComment.setOnClickListener(new View.OnClickListener() {
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
            postingDatabaseProfile.collection("Posting").document(post_key).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(final DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.contains("uid")) {
                            if (mAuth.getCurrentUser().getUid().equals(dataSnapshot.getString("uid"))) {

                                ((MainPageAdapter.PostViewHolder) holder).arrow_down.setVisibility(View.VISIBLE);

                                ((MainPageAdapter.PostViewHolder) holder).arrow_down.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        final String[] items = {"Edit post", "Delete post", "Cancel"};
                                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity);
                                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                if (items[i].equals("Edit post")) {
                                                    Intent openSinglePost = new Intent(ctx, SinglePostViewActivity.class);
                                                    openSinglePost.putExtra("post_id", post_key);
                                                    activity.startActivity(openSinglePost);
                                                } else if (items[i].equals("Delete post")) {


                                                    postingDatabaseProfile.collection("Posting").document(post_key).delete().addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {


                                                            notifyItemRemoved(((MainPageAdapter.PostViewHolder) holder).getAdapterPosition());
                                                            Toast.makeText(activity.getApplicationContext(), "deleted", Toast.LENGTH_LONG).show();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(activity.getApplicationContext(), e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
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
                                ((MainPageAdapter.PostViewHolder) holder).arrow_down.setVisibility(View.INVISIBLE);
                        }
                    }
                }

            });


            ((MainPageAdapter.PostViewHolder) holder).post_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String username = ((MainPageAdapter.PostViewHolder) holder).post_username.getText().toString().trim();
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

                                        DocumentReference profileInfo = profileUsers.collection("Users").document(uid);

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

            ((MainPageAdapter.PostViewHolder) holder).post_profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String username = ((MainPageAdapter.PostViewHolder) holder).post_username.getText().toString().trim();
                    Log.i("username", username);

                    profileUsers.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                        ProfileFragment profileFragment = new ProfileFragment();

                                        FragmentTransaction manager = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

                                        manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                        Log.i("tacno", "true");

                                    } else {

                                        DocumentReference profileInfo = profileUsers.collection("Users").document(uid);

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
        } else if (getViewType == ITEM_LOADING) {


            if (isLoading) {

                ((ProgressViewHolder) holder).progressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.spinnerLoad),
                        android.graphics.PorterDuff.Mode.MULTIPLY);
                ((ProgressViewHolder) holder).progressBar.setVisibility(
                        View.VISIBLE);
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            } else{
                    ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
                }

        }

    }





    public void setOnLoadMore(OnLoadMoreListener onLoadMore)
    {
        onLoadMoreListener = onLoadMore;
    }


    public void setIsLoading(boolean param)
    {
        isLoading = param;
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
        int numberLikes;
        int numberDislikes;

        TextView timeAgoTextView;

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
            timeAgoTextView = (TextView) mView.findViewById(R.id.postAgoTime);


        }

        public void checkPremium() {


        }

        public void onClick(View view) {


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

        public void setNumberLikes(final String post_key, Activity activity) {

            CollectionReference col = likeReference.collection("Likes").document(post_key).collection("like-id");
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


           /* col.get().addOnCompleteListener(activity, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> querySnapshot) {

                    numberLikes = querySnapshot.getResult().size();


                    if (numberLikes == 0) {
                        numberofLikes.setText("");
                    } else {
                        numberofLikes.setText(String.valueOf(numberLikes));
                    }
                }

            });*/
        }


        public void setNumberComments(String post_key, Activity activity) {
            numberCommentsReference.collection("Comments").document(post_key).collection("comment-id").addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
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

      /*      numberCommentsReference.collection("Comments").document(post_key).collection("comment-id").get().addOnCompleteListener(activity, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> querySnapshot) {


                        int numberOfComments = querySnapshot.getResult().size();

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
            });*/
        }


        public void setLikeBtn(final String post_key, Activity activity) {
            String uid = mAuth.getCurrentUser().getUid();
            Log.i("uid", uid);
            final DocumentReference doc = likeReference.collection("Likes").document(post_key).collection("like-id").document(uid);
            doc.addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {

                        dislike_button.setClickable(false);
                        like_button.setActivated(true);
                        Log.i("key like ima ", post_key);


                    } else {
                        dislike_button.setClickable(true);
                        like_button.setActivated(false);
                        Log.i("key like nema ", post_key);
                    }

                }
            });
        }

        public void setNumberDislikes(String post_key, Activity activity) {

            CollectionReference col = dislikeReference.collection("Dislikes").document(post_key).collection("dislike-id");
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
            /*     col.get().addOnCompleteListener(activity, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> querySnapshot) {

                    numberDislikes = querySnapshot.getResult().size();


                    if (numberDislikes == 0) {
                        numberOfDislikes.setText("");
                    } else {
                        numberOfDislikes.setText(String.valueOf(numberDislikes));
                    }
                }

            });*/
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
                        Log.i("key dislike ima ", post_key);

                    } else {
                        dislike_button.setActivated(false);
                        like_button.setClickable(true);
                        Log.i("key dislike nema ", post_key);
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
