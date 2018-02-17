package com.malikbisic.sportapp.activity.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.GalleryViewPagerAdapter;
import com.malikbisic.sportapp.model.firebase.GalleryImageModel;

import java.util.ArrayList;

public class GalleryImageFullScreen extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    int positionClickImage;
    public static ArrayList<String> imagesList;

    Intent myIntent;

    ViewPager imageViewPager;
    GalleryViewPagerAdapter adapter;
    public static ImageView saveImage;
    public static ImageView closeGallery;
    String username;
    String userID;

    Toolbar fullScreenToolbar;
    TextView toolbarUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image_full_screen);
        myIntent = getIntent();
        positionClickImage = myIntent.getIntExtra("position", 0);
        imagesList = myIntent.getStringArrayListExtra("images");
        username = myIntent.getStringExtra("username");
        userID = myIntent.getStringExtra("userID");

        imageViewPager = (ViewPager) findViewById(R.id.fullScreenPage);
        adapter = new GalleryViewPagerAdapter(this, imagesList, userID, username);
        imageViewPager.setAdapter(adapter);
        imageViewPager.setCurrentItem(positionClickImage, false);
        imageViewPager.addOnPageChangeListener(this);

        saveImage = (ImageView) findViewById(R.id.saveBtn);
        closeGallery = (ImageView) findViewById(R.id.closeBtn);

        fullScreenToolbar = (Toolbar) findViewById(R.id.fullscreentoolbar);
        setSupportActionBar(fullScreenToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        toolbarUsername = (TextView) findViewById(R.id.userNameINToolbar);
        toolbarUsername.setText(username);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent backToChat = new Intent(GalleryImageFullScreen.this, ChatMessageActivity.class);
                backToChat.putExtra("userId", userID);
                backToChat.putExtra("username", username);
                startActivity(backToChat);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
