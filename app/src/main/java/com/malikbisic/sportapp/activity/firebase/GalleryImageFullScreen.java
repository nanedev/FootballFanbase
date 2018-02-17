package com.malikbisic.sportapp.activity.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.GalleryViewPagerAdapter;
import com.malikbisic.sportapp.model.firebase.GalleryImageModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

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
        onViewCreated(savedInstanceState);
        myIntent = getIntent();
        positionClickImage = myIntent.getIntExtra("position", 0);
        imagesList = myIntent.getStringArrayListExtra("images");
        username = myIntent.getStringExtra("username");
        userID = myIntent.getStringExtra("userID");

        imageViewPager = (ViewPager) findViewById(R.id.fullScreenPage);
        adapter = new GalleryViewPagerAdapter(this, imagesList);
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

        Log.i("gallery", String.valueOf(imagesList));

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = imageViewPager.getCurrentItem();

                saveFile(getBitmapFromURL(imagesList.get(pos)));
            }
        });

        closeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryImageFullScreen.this, ChatMessageActivity.class);
                intent.putExtra("userId", userID);
                intent.putExtra("username", username);
                startActivity(intent);

            }
        });


    }

    public void onViewCreated(Bundle savedInstanceState) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public void saveFile(Bitmap b) {
        try {

            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/FootballFanBase/");

            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = File.createTempFile(
                    String.valueOf(Calendar.getInstance().getTimeInMillis()),
                    ".jpeg",                     /* suffix */
                    storageDir                   /* directory */
            );


            FileOutputStream writeStream = new FileOutputStream(imageFile);

            b.compress(Bitmap.CompressFormat.JPEG, 100, writeStream);
            writeStream.flush();
            writeStream.close();
            Toast.makeText(GalleryImageFullScreen.this, "Image saved", Toast.LENGTH_SHORT).show();

            addPicToGallery(imageFile);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void addPicToGallery(File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
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
