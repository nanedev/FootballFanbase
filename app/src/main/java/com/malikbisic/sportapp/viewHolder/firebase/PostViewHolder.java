package com.malikbisic.sportapp.viewHolder.firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.PictureDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.api.SearchableCountry;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import cn.jzvd.JZVideoPlayerStandard;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 20/10/2017.
 */
public class PostViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public Button play_button;
    public MediaPlayer mPlayer;
    cn.jzvd.JZVideoPlayerStandard videoView;
    public ImageView post_photo;
    MediaController mediaController;
    RelativeLayout audioLayout;
    ProgressDialog progressDialog;
    public Button pause_button;
    public SeekBar seekBar;
    public Button stop_button;
    public ImageView like_button;
    public ImageView dislike_button;
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
    //public TextView openSinglePost;
    public ImageView arrow_down;
    public TextView openComment;
    public TextView post_username;
    public ImageView post_profile_image;
    RelativeLayout layoutForPost;
    RelativeLayout backgroundImage;
    ImageView postBackgroundImage;
    CircleImageView post_clubLogo;
    public TextView comments;
    public TextView numberComments;
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
        likeReference = database.getReference().child("Likes");
        dislikeReference = database.getReference().child("Dislikes");
        userReference = database.getReference().child("Users");
        numberCommentsReference = database.getReference().child("Comments");
        mAuth = FirebaseAuth.getInstance();
        openComment = (TextView) mView.findViewById(R.id.comment_something);
        post_username = (TextView) mView.findViewById(R.id.username_wall);
        post_profile_image = (ImageView) mView.findViewById(R.id.profile_image_wall);
        post_clubLogo = (CircleImageView) mView.findViewById(R.id.clubLogoPost);
        postBackgroundImage = (ImageView) mView.findViewById(R.id.image_post_background);
        comments = (TextView) mView.findViewById(R.id.comments_textview);
        numberComments = (TextView) mView.findViewById(R.id.number_comments);
        likeReference.keepSynced(true);
        dislikeReference.keepSynced(true);


    }

    public void checkPremium() {


    }

    public void onClick(View view) {


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
                    Log.i("key like ima ", post_key);

                } else {
                    dislike_button.setClickable(true);
                    like_button.setActivated(false);
                    Log.i("key like nema ", post_key);
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
                    Log.i("key dislike ima ", post_key);

                } else {
                    dislike_button.setActivated(false);
                    like_button.setClickable(true);
                    Log.i("key dislike nema ", post_key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
