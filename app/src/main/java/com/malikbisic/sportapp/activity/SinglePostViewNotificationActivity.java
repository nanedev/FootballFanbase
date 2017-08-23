package com.malikbisic.sportapp.activity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikbisic.sportapp.R;

import de.hdodenhof.circleimageview.CircleImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


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

    }
}
