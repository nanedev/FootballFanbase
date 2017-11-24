package com.malikbisic.sportapp.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.CommentsActivity;
import com.malikbisic.sportapp.activity.FullScreenImage;
import com.malikbisic.sportapp.activity.MainPage;
import com.malikbisic.sportapp.activity.ProfileFragment;
import com.malikbisic.sportapp.activity.SinglePostViewActivity;
import com.malikbisic.sportapp.activity.UserProfileActivity;
import com.malikbisic.sportapp.activity.Username_Dislikes_Activity;
import com.malikbisic.sportapp.activity.Username_Likes_Activity;
import com.malikbisic.sportapp.adapter.MainPageAdapter;
import com.malikbisic.sportapp.model.Post;
import com.malikbisic.sportapp.model.UsersModel;
import com.malikbisic.sportapp.viewHolder.PostViewHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by korisnik on 20/10/2017.
 */

public class PremiumUsers {
    FirebaseFirestore postingDatabase = FirebaseFirestore.getInstance();
    boolean pause_state;
    boolean play_state;
    boolean like_process = false;
    boolean dislike_process = false;
    private FirebaseFirestore mReference, likesReference, dislikeReference, notificationReference, profileUsers;
    private FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;


    public void premiumUser(RecyclerView wallList, final Context ctx, final Activity activity) {
         com.google.firebase.firestore.Query query = postingDatabase.collection("Posting");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        notificationReference = FirebaseFirestore.getInstance();//.getReference().child("Notification");
        likesReference = FirebaseFirestore.getInstance(); //.getReference().child("Likes");
        dislikeReference = FirebaseFirestore.getInstance(); //.child("Dislikes");
        profileUsers = FirebaseFirestore.getInstance();
        //likesReference.keepSynced(true);
        //dislikeReference.keepSynced(true);

        final String uid = mAuth.getCurrentUser().getUid();
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        FirestoreRecyclerAdapter firebaseRecyclerAdapter = new FirestoreRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final PostViewHolder viewHolder, int position, Post model) {
                final String post_key = getItem(position).getKey();
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


                        likesReference.collection("Likes").document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                                if (like_process) {


                                    if (documentSnapshots.contains(mAuth.getCurrentUser().getUid())) {

                                        likesReference.document(mAuth.getCurrentUser().getUid()).delete();
                                        like_process = false;


                                    } else {

                                        Map<String, Object> userLikeInfo = new HashMap<>();
                                        userLikeInfo.put("username", MainPage.usernameInfo);
                                        userLikeInfo.put("photoProfile", MainPage.profielImage);

                                        final DocumentReference newPost = likesReference.document(mAuth.getCurrentUser().getUid());
                                        newPost.set(userLikeInfo);


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

                                            }


                                        });


                                        like_process = false;


                                    }
                                }
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
                                        if (like_process) {


                                            if (documentSnapshots.contains(mAuth.getCurrentUser().getUid())) {

                                                dislikeReference.document(mAuth.getCurrentUser().getUid()).delete();
                                                dislike_process = false;


                                            } else {

                                                Map<String, Object> userDislikeInfo = new HashMap<>();
                                                userDislikeInfo.put("username", MainPage.usernameInfo);
                                                userDislikeInfo.put("photoProfile", MainPage.profielImage);

                                                final DocumentReference newPost = dislikeReference.document(mAuth.getCurrentUser().getUid());
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
                });


    }

            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_row, parent);
                return new PostViewHolder(v);
            }
        };


        /*FirestoreR<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.wall_row,
                PostViewHolder.class,
                postingDatabase
        ) {


            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                final String post_key = getRef(position).getKey();
                final String link_post = getRef(position).toString();
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


                /*viewHolder.numberofLikes.setOnClickListener(new View.OnClickListener() {
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


                        likesReference.collection("Likes").document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshots, FirebaseFirestoreException e) {
                                if (like_process) {


                                    if (documentSnapshots.contains(mAuth.getCurrentUser().getUid())) {

                                        likesReference.document(mAuth.getCurrentUser().getUid()).delete();
                                        like_process = false;


                                    } else {

                                        Map<String, Object> userLikeInfo = new HashMap<>();
                                        userLikeInfo.put("username", MainPage.usernameInfo);
                                        userLikeInfo.put("photoProfile", MainPage.profielImage);

                                        final DocumentReference newPost = likesReference.document(mAuth.getCurrentUser().getUid());
                                        newPost.set(userLikeInfo);


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

                                            }


                                        });


                                        like_process = false;


                                    }
                                }
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


                        /*viewHolder.post_photo.setOnClickListener(new View.OnClickListener() {
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
                                        if (like_process) {


                                            if (documentSnapshots.contains(mAuth.getCurrentUser().getUid())) {

                                                dislikeReference.document(mAuth.getCurrentUser().getUid()).delete();
                                                dislike_process = false;


                                            } else {

                                                Map<String, Object> userDislikeInfo = new HashMap<>();
                                                userDislikeInfo.put("username", MainPage.usernameInfo);
                                                userDislikeInfo.put("photoProfile", MainPage.profielImage);

                                                final DocumentReference newPost = dislikeReference.document(mAuth.getCurrentUser().getUid());
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
                                });

                            }
                        });*/

                        /*viewHolder.comments.setOnClickListener(new View.OnClickListener() {
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
                });
            }}; */


                wallList.setAdapter(firebaseRecyclerAdapter);
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }
        }
