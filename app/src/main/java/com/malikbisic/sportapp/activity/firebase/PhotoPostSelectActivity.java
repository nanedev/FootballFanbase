package com.malikbisic.sportapp.activity.firebase;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.SelectImagePostAdapter;
import com.malikbisic.sportapp.utils.ImageAlbumName;

import java.util.ArrayList;
import java.util.Map;

public class PhotoPostSelectActivity extends AppCompatActivity {

    ArrayList<String> imageUri = new ArrayList<>();
    Map<String, Object> messageMap;
    private ImageAlbumName utils;

    Toolbar imagePostToolbar;

    String username, profileImage, country, clubHeader;
    Intent myIntent;

    SelectImagePostAdapter adapter;
    RecyclerView imageRecView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_post_select);
        imagePostToolbar = (Toolbar) findViewById(R.id.postImage_toolbar);
        setSupportActionBar(imagePostToolbar);
        myIntent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Gallery...");
        utils = new ImageAlbumName(this);
        imageRecView = (RecyclerView) findViewById(R.id.imagepost_recview);
        imageUri = utils.getFilePaths();
        username = myIntent.getStringExtra("username");
        profileImage = myIntent.getStringExtra("profileImage");
        clubHeader = myIntent.getStringExtra("clubheader");
        country = myIntent.getStringExtra("country");
        adapter = new SelectImagePostAdapter(this, imageUri, username, profileImage, country, clubHeader);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        imageRecView.setLayoutManager(layoutManager);
        imageRecView.setAdapter(adapter);

        username = myIntent.getStringExtra("username");
        profileImage = myIntent.getStringExtra("profileImage");
        clubHeader = myIntent.getStringExtra("clubheader");
        country = myIntent.getStringExtra("country");

    }
}
