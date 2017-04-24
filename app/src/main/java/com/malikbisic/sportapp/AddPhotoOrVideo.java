package com.malikbisic.sportapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class AddPhotoOrVideo extends AppCompatActivity {

    private Intent myIntent;
    private ImageView photoSelected;
    private VideoView videoSelected;
    private EditText saySomething;
    private Button post;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_or_video);
        myIntent = getIntent();
        photoSelected = (ImageView) findViewById(R.id.post_image);
        videoSelected = (VideoView) findViewById(R.id.post_video);
        saySomething = (EditText) findViewById(R.id.tell_something_about_video_image);
        post = (Button) findViewById(R.id.btn_post_photo_or_video);


        if (MainPage.photoSelected){
            photoSelected.setVisibility(View.VISIBLE);
            videoSelected.setVisibility(View.GONE);
            photoSelected.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            Uri imageUri = Uri.parse(myIntent.getStringExtra("image-uri_selected"));
            photoSelected.setImageURI(imageUri);
        } else if (!MainPage.photoSelected) {
            photoSelected.setVisibility(View.GONE);
            videoSelected.setVisibility(View.VISIBLE);
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Buffering");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            try {
                // Start the MediaController
                MediaController mediacontroller = new MediaController(
                        AddPhotoOrVideo.this);
                mediacontroller.setAnchorView(videoSelected);
                // Get the URL from String VideoURL
                Uri video = Uri.parse(myIntent.getStringExtra("video-uri_selected"));
                videoSelected.setMediaController(mediacontroller);
                videoSelected.setVideoURI(video);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            videoSelected.requestFocus();
            videoSelected.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    pDialog.dismiss();
                    videoSelected.start();
                }
            });

        }


    }
}
