package com.malikbisic.sportapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullScreenImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView image = (ImageView) findViewById(R.id.fullScreenImageView);

        Intent myIntet = getIntent();

        String uri = myIntet.getStringExtra("imageURL");

        Picasso.with(this).load(uri).into(image);
    }
}
