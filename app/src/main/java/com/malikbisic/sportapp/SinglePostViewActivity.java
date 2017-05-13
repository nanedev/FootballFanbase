package com.malikbisic.sportapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class SinglePostViewActivity extends AppCompatActivity {
    private String post_key = null;

    private DatabaseReference postReference;
    private DatabaseReference editPost;
    private ImageView post_image;
    private ImageView profile_image;
    private TextView username;
    private JCVideoPlayerStandard post_video;
    private EditText post_text_video;
    private EditText post_text_image;
    private EditText post_text_audio;
    private RelativeLayout layoutAudio;
    private RelativeLayout layoutImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post_view);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        postReference = FirebaseDatabase.getInstance().getReference().child("Posting");
        editPost = FirebaseDatabase.getInstance().getReference().child("Posting");
        profile_image = (ImageView) findViewById(R.id.profile_image_wall);
        username = (TextView) findViewById(R.id.username_wall);
        post_image = (ImageView) findViewById(R.id.posted_image);
        post_video = (JCVideoPlayerStandard) findViewById(R.id.posted_video);
        post_text_image = (EditText) findViewById(R.id.text_for_image);
        post_text_video = (EditText) findViewById(R.id.text_for_video);
        post_text_audio = (EditText) findViewById(R.id.audio_textview);
        layoutAudio = (RelativeLayout) findViewById(R.id.layout_for_audio_player);
        layoutImage = (RelativeLayout) findViewById(R.id.layout_for_image);


        Intent myIntent = getIntent();
        post_key = myIntent.getStringExtra("post_id");

        postReference.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String profileImage = (String) dataSnapshot.child("profileImage").getValue();
                String profileUsername = (String) dataSnapshot.child("username").getValue();
                String postImage = (String) dataSnapshot.child("photoPost").getValue();
                String postVideo = (String) dataSnapshot.child("videoPost").getValue();
                String descVideo = (String) dataSnapshot.child("descVideo").getValue();
                String descImage = (String) dataSnapshot.child("descForPhoto").getValue();
                String postAudio = (String) dataSnapshot.child("audioFile").getValue();

                Picasso.with(SinglePostViewActivity.this).load(profileImage).into(profile_image);
                username.setText(profileUsername);

                if (postImage != null) {
                    layoutImage.setVisibility(View.VISIBLE);
                    post_text_video.setVisibility(View.GONE);
                    post_text_audio.setVisibility(View.GONE);
                    Picasso.with(SinglePostViewActivity.this).load(postImage).into(post_image);
                } else {

                    layoutImage.setVisibility(View.GONE);
                }

                post_text_image.setText(descImage);
                post_text_video.setText(descVideo);

                if (postVideo != null) {
                    post_video.setVisibility(View.VISIBLE);
                    post_text_audio.setVisibility(View.GONE);
                    post_text_image.setVisibility(View.GONE);
                    post_video.setUp(postVideo, JCVideoPlayer.SCREEN_LAYOUT_NORMAL, "proba");
                } else {
                    post_video.setVisibility(View.GONE);
                }

                if (postAudio != null) {
                    post_text_image.setVisibility(View.GONE);
                    post_text_video.setVisibility(View.GONE);
                    Log.i("file", postAudio);
                } else {
                    layoutAudio.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_post_text, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_edit_text) {
           DatabaseReference editPostComplete =  editPost.child(post_key);
            String newText = post_text_video.getText().toString().trim();

            editPostComplete.child("descVideo").setValue(newText);
        }



        return super.onOptionsItemSelected(item);


    }
}
