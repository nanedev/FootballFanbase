package com.malikbisic.sportapp.activity.firebase;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.GridView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.ImageAlbumAdapter;
import com.malikbisic.sportapp.utils.ImageAlbumName;
import com.malikbisic.sportapp.utils.ImageConstant;

import java.util.ArrayList;

public class SendImageChatActivity extends AppCompatActivity {

    Toolbar mChatToolbar;
    private ImageAlbumName utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private ImageAlbumAdapter adapter;
    private RecyclerView gridView;
    private int columnWidth;

    Intent myIntent;
    String myUID;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image_chat);

        mChatToolbar = (Toolbar) findViewById(R.id.chatImage_toolbar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Send image...");
        myIntent = getIntent();
        myUID = myIntent.getStringExtra("myUID");
        userID = myIntent.getStringExtra("userID");

        gridView = (RecyclerView) findViewById(R.id.grid_view);

        utils = new ImageAlbumName(this);


        // loading all image paths from SD card
        imagePaths = utils.getFilePaths();

        // Gridview adapter
        adapter = new ImageAlbumAdapter(SendImageChatActivity.this, imagePaths, myUID, userID);

        // setting grid view adapter
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gridView.setLayoutManager(manager);
        gridView.setAdapter(adapter);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent backToChat = new Intent(SendImageChatActivity.this, ChatMessageActivity.class);
                backToChat.putExtra("userId", userID);
                setResult(Activity.RESULT_OK, backToChat);
                finish();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backToChat = new Intent(SendImageChatActivity.this, ChatMessageActivity.class);
        backToChat.putExtra("userId", userID);
        startActivity(backToChat);
    }
}
