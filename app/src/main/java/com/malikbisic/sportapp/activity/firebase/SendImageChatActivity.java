package com.malikbisic.sportapp.activity.firebase;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
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
    private GridView gridView;
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

        gridView = (GridView) findViewById(R.id.grid_view);

        utils = new ImageAlbumName(this);

        // Initilizing Grid View
        InitilizeGridLayout();

        // loading all image paths from SD card
        imagePaths = utils.getFilePaths();

        // Gridview adapter
        adapter = new ImageAlbumAdapter(SendImageChatActivity.this, imagePaths,
                columnWidth, myUID, userID);

        // setting grid view adapter
        gridView.setAdapter(adapter);
    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                ImageConstant.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((utils.getScreenWidth() - ((ImageConstant.NUM_OF_COLUMNS + 1) * padding)) / ImageConstant.NUM_OF_COLUMNS);

        gridView.setNumColumns(ImageConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backToChat = new Intent(SendImageChatActivity.this, ChatMessageActivity.class);
        backToChat.putExtra("userId", userID);
        startActivity(backToChat);
    }
}
