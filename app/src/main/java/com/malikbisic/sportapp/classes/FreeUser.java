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
import android.view.View;
import android.widget.SeekBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.CommentsActivity;
import com.malikbisic.sportapp.activity.firebase.FullScreenImage;
import com.malikbisic.sportapp.activity.firebase.MainPage;
import com.malikbisic.sportapp.fragment.firebase.ProfileFragment;
import com.malikbisic.sportapp.activity.firebase.SinglePostViewActivity;
import com.malikbisic.sportapp.activity.firebase.UserProfileActivity;
import com.malikbisic.sportapp.activity.firebase.Username_Dislikes_Activity;
import com.malikbisic.sportapp.activity.firebase.Username_Likes_Activity;
import com.malikbisic.sportapp.model.firebase.Post;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.malikbisic.sportapp.viewHolder.firebase.PostViewHolder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by korisnik on 20/10/2017.
 */

public class FreeUser {
    DatabaseReference postingDatabase = FirebaseDatabase.getInstance().getReference().child("Posting");
    boolean pause_state;
    boolean play_state;
    boolean like_process = false;
    boolean dislike_process = false;
    private DatabaseReference mReference, likesReference, dislikeReference, notificationReference, profileUsers;
    private FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;

    public void freeUsers(RecyclerView wallList, final Context ctx, final Activity activity, UsersModel model) {

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notification");
        likesReference = mDatabase.getReference().child("Likes");
        dislikeReference = mDatabase.getReference().child("Dislikes");
        profileUsers = FirebaseDatabase.getInstance().getReference();
        likesReference.keepSynced(true);
        dislikeReference.keepSynced(true);

        final String uid = mAuth.getCurrentUser().getUid();

        final String myClub = model.getFavoriteClub();
        DatabaseReference checkFavClub = FirebaseDatabase.getInstance().getReference().child("Posting");
        final Query query = checkFavClub
                .limitToFirst(10)
                .orderByChild("favoritePostClub")
                .startAt(myClub);

        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.wall_row,
                PostViewHolder.class,
                query
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


                viewHolder.numberofLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent listUsername = new Intent(ctx, Username_Likes_Activity.class);
                        listUsername.putExtra("post_key", post_key);
                        activity.startActivity(listUsername);
                    }
                });

                viewHolder.numberOfDislikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent listUsername = new Intent(ctx, Username_Dislikes_Activity.class);
                        listUsername.putExtra("post_key", post_key);
                        activity.startActivity(listUsername);
                    }
                });


                viewHolder.like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        like_process = true;


                        likesReference.addValueEventListener(new ValueEventListener() {
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
                });


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

                postingDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(post_key).child("uid").exists()) {
                            if (mAuth.getCurrentUser().getUid().equals(dataSnapshot.child(post_key).child("uid").getValue().toString())) {

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

                                                    postingDatabase.child(post_key).removeValue();
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


                viewHolder.post_username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.post_username.getText().toString().trim();
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

                                            /* ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true"); */

                                            Intent intent = new Intent(activity, ProfileFragment.class);
                                            activity.startActivity(intent);

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {


                                                    Intent openUserProfile = new Intent(ctx, UserProfileActivity.class);
                                                    openUserProfile.putExtra("userID", uid);
                                                    activity.startActivity(openUserProfile);
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

                viewHolder.post_profile_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.post_username.getText().toString().trim();
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

                                             /* ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true"); */

                                            Intent intent = new Intent(activity, ProfileFragment.class);
                                            activity.startActivity(intent);

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {


                                                    Intent openUserProfile = new Intent(ctx, UserProfileActivity.class);
                                                    openUserProfile.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                                    openUserProfile.putExtra("userID", uid);
                                                    activity.startActivity(openUserProfile);
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
        };


        wallList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }


}