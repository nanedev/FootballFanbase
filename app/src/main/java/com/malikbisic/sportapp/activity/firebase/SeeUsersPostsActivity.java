package com.malikbisic.sportapp.activity.firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.PictureDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.api.SearchableCountry;
import com.malikbisic.sportapp.model.firebase.Post;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZVideoPlayerStandard;
import de.hdodenhof.circleimageview.CircleImageView;


public class SeeUsersPostsActivity extends AppCompatActivity {
    UsersModel model;
    FirebaseAuth mAuth;
    RecyclerView postRecyclerView;
    LinearLayoutManager linearLayoutManager;
    private DatabaseReference mReference, postingDatabase, likesReference, dislikeReference,notificationReference;
    boolean pause_state;
    boolean play_state;
    boolean like_process = false;
    boolean dislike_process = false;
    private FirebaseDatabase mDatabase;
    Intent getUserUid;
    String getUserUidString;
String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_users_posts);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        postingDatabase = FirebaseDatabase.getInstance().getReference().child("Posting");
        postRecyclerView = (RecyclerView) findViewById(R.id.see_users_posts_recycler_view);
        postRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postRecyclerView.setLayoutManager(linearLayoutManager);
        postRecyclerView.setItemViewCacheSize(20);
        postRecyclerView.setDrawingCacheEnabled(true);
        postRecyclerView.setItemAnimator(new DefaultItemAnimator());
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notification");
        likesReference = mDatabase.getReference().child("Likes");
        dislikeReference = mDatabase.getReference().child("Dislikes");
        likesReference.keepSynced(true);
        dislikeReference.keepSynced(true);
        getUserUid = getIntent();
        uid = MainPage.uid;
        getUserUidString =  getUserUid.getStringExtra("userProfileUid");


        DatabaseReference myPostsReference = FirebaseDatabase.getInstance().getReference().child("Posting");
        final Query query = myPostsReference
                .orderByChild("uid")
                .equalTo(getUserUidString);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseRecyclerAdapter<Post, SeeUsersPostsActivity.PostViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Post, SeeUsersPostsActivity.PostViewHolder>(
                        Post.class,
                        R.layout.see_users_posts_row,
                        SeeUsersPostsActivity.PostViewHolder.class,
                        query
                ) {
                    @Override
                    protected void populateViewHolder(final SeeUsersPostsActivity.PostViewHolder viewHolder, Post model, int position) {

                        final String post_key = getRef(position).getKey();
                        viewHolder.setDescForAudio(model.getDescForAudio());
                        viewHolder.setDescForPhoto(model.getDescForPhoto());
                        viewHolder.setDescVideo(model.getDescVideo());
                        viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setPhotoPost(getApplicationContext(), model.getPhotoPost());
                        viewHolder.setVideoPost(getApplicationContext(), model.getVideoPost());
                        viewHolder.setAudioFile(getApplicationContext(), model.getAudioFile());
                        viewHolder.setLikeBtn(post_key);
                        viewHolder.setNumberLikes(post_key);
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setDislikeBtn(post_key);
                        viewHolder.setNumberComments(post_key);
                        viewHolder.setNumberDislikes(post_key);
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

                /*viewHolder.openSinglePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openSinglePost = new Intent(MainPage.this, SinglePostViewActivity.class);
                        openSinglePost.putExtra("post_id", post_key);
                        startActivity(openSinglePost);
                        Log.i("linkPost", link_post);
                    }
                }); */

                        viewHolder.numberofLikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent listUsername = new Intent(SeeUsersPostsActivity.this, Username_Likes_Activity.class);
                                listUsername.putExtra("post_key", post_key);
                                startActivity(listUsername);
                            }
                        });

                        viewHolder.numberOfDislikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent listUsername = new Intent(SeeUsersPostsActivity.this, Username_Dislikes_Activity.class);
                                listUsername.putExtra("post_key", post_key);
                                startActivity(listUsername);
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

                                                DatabaseReference newPost = likesReference.child(post_key).child(mAuth.getCurrentUser().getUid());

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
                                Intent openFullScreen = new Intent(SeeUsersPostsActivity.this, FullScreenImage.class);
                                String tag = (String) viewHolder.post_photo.getTag();
                                openFullScreen.putExtra("imageURL", tag);
                                startActivity(openFullScreen);
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
                                Intent openCom = new Intent(SeeUsersPostsActivity.this, CommentsActivity.class);
                                openCom.putExtra("keyComment", post_key);
                                openCom.putExtra("profileComment", MainPage.profielImage);
                                startActivity(openCom);
                            }
                        });

                        viewHolder.numberComments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openCom = new Intent(SeeUsersPostsActivity.this, CommentsActivity.class);
                                openCom.putExtra("keyComment", post_key);
                                openCom.putExtra("profileComment", MainPage.profielImage);
                                startActivity(openCom);
                            }
                        });


                        viewHolder.openComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openCom = new Intent(SeeUsersPostsActivity.this, CommentsActivity.class);
                                openCom.putExtra("keyComment", post_key);
                                openCom.putExtra("profileComment", MainPage.profielImage);
                                startActivity(openCom);
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
                                                            Intent openSinglePost = new Intent(SeeUsersPostsActivity.this, SinglePostViewActivity.class);
                                                            openSinglePost.putExtra("post_id", post_key);
                                                            startActivity(openSinglePost);
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


                    }
                };


                postRecyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        FirebaseDatabase database;
        DatabaseReference likeReference, dislikeReference;
        DatabaseReference userReference;
        DatabaseReference numberCommentsReference;
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
            play_button = (Button) mView.findViewById(R.id.play_button_users_posts);
            pause_button = (Button) mView.findViewById(R.id.pause_button_users_posts);
            stop_button = (Button) mView.findViewById(R.id.stop_button_users_posts);

            mediaController = new MediaController(mView.getContext());

            videoView = (cn.jzvd.JZVideoPlayerStandard) mView.findViewById(R.id.posted_video_users_posts);
            post_photo = (ImageView) mView.findViewById(R.id.posted_image_users_posts);
            audioLayout = (RelativeLayout) mView.findViewById(R.id.layout_for_audio_player_users_posts);
            mPlayer = new MediaPlayer();
            progressDialog = new ProgressDialog(mView.getContext());
            seekBar = (SeekBar) mView.findViewById(R.id.audio_seek_bar_users_posts);
            like_button = (ImageView) mView.findViewById(R.id.like_button_users_posts);
            dislike_button = (ImageView) mView.findViewById(R.id.dislike_button_users_posts);
            numberofLikes = (TextView) mView.findViewById(R.id.number_of_likes_users_posts);
            numberOfDislikes = (TextView) mView.findViewById(R.id.number_of_dislikes_users_posts);
            single_post_layout = (RelativeLayout) mView.findViewById(R.id.layout_for_only_post_users_posts);

            arrow_down = (ImageView) mView.findViewById(R.id.down_arrow_users_posts);
            layoutPhotoText = (RelativeLayout) mView.findViewById(R.id.layout_for_text_image_users_posts);
            layoutPhoto = (RelativeLayout) mView.findViewById(R.id.layout_for_image_users_posts);
            layoutAudioText = (RelativeLayout) mView.findViewById(R.id.layout_audio_textview_users_posts);
            layoutVideo = (FrameLayout) mView.findViewById(R.id.framelayout_users_posts);
            //loadPhoto = (ProgressBar) mView.findViewById(R.id.post_photo_bar_load);
            layoutVideoText = (RelativeLayout) mView.findViewById(R.id.layout_for_video_text_users_posts);
            database = FirebaseDatabase.getInstance();
            likeReference = database.getReference().child("Likes");
            dislikeReference = database.getReference().child("Dislikes");
            userReference = database.getReference().child("Users");
            numberCommentsReference = database.getReference().child("Comments");
            mAuth = FirebaseAuth.getInstance();
            openComment = (TextView) mView.findViewById(R.id.comment_something_users_posts);
            post_username = (TextView) mView.findViewById(R.id.username_wall_users_posts);
            post_profile_image = (ImageView) mView.findViewById(R.id.profile_image_wall_users_posts);
            post_clubLogo = (CircleImageView) mView.findViewById(R.id.clubLogoPost_users_posts);
            postBackgroundImage = (ImageView) mView.findViewById(R.id.image_post_background_users_posts);
            comments = (TextView) mView.findViewById(R.id.comments_textview_users_posts);
            numberComments = (TextView) mView.findViewById(R.id.number_comments_users_posts);
            likeReference.keepSynced(true);
            dislikeReference.keepSynced(true);


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
                TextView single_post = (TextView) mView.findViewById(R.id.post_text_main_page_users_posts);
                single_post_layout.setVisibility(View.VISIBLE);
                single_post.setText(desc);
            } else {
                single_post_layout.setVisibility(View.GONE);
            }
        }


        public void setDescForAudio(String descForAudio) {

            TextView post_desc_for_audio = (TextView) mView.findViewById(R.id.audio_textview_users_posts);
            if (descForAudio != null) {
                layoutAudioText.setVisibility(View.VISIBLE);
                post_desc_for_audio.setText(descForAudio);
            } else {
                layoutAudioText.setVisibility(View.GONE);
                post_desc_for_audio.setVisibility(View.GONE);
            }
        }

        public void setDescForPhoto(String descForPhoto) {

            TextView post_desc_for_photo = (TextView) mView.findViewById(R.id.text_for_image_users_posts);

            if (descForPhoto != null) {
                layoutPhotoText.setVisibility(View.VISIBLE);
                post_desc_for_photo.setText(descForPhoto);
            } else {
                layoutPhotoText.setVisibility(View.GONE);
            }
        }

        public void setDescVideo(String descVideo) {
            TextView post_desc_for_video = (TextView) mView.findViewById(R.id.text_for_video_users_posts);

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

    }
}
