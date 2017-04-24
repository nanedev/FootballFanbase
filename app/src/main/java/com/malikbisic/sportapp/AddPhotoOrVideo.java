package com.malikbisic.sportapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

public class AddPhotoOrVideo extends AppCompatActivity {

    private Intent myIntent;
    private ImageView photoSelected;
    private VideoView videoSelected;
    private EditText saySomething;
    private Button post;

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
        } else if (MainPage.photoSelected == false){
            photoSelected.setVisibility(View.GONE);
            videoSelected.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(myIntent.getStringExtra("video-uri_selected"));
            videoSelected.setVideoURI(videoUri);

        }


    }
}
