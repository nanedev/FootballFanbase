package com.malikbisic.sportapp.activity.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image_full_screen);
        myIntent = getIntent();
        positionClickImage = myIntent.getIntExtra("position", 0);
        imagesList = myIntent.getStringArrayListExtra("images");

        imageViewPager = (ViewPager) findViewById(R.id.fullScreenPage);
        adapter = new GalleryViewPagerAdapter(this, imagesList);
        imageViewPager.setAdapter(adapter);
        imageViewPager.setCurrentItem(positionClickImage, false);
        imageViewPager.addOnPageChangeListener(this);

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
