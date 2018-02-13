package com.malikbisic.sportapp.activity.firebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malikbisic.sportapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class FullScreenImageFromChat extends AppCompatActivity {
Toolbar fullScreenToolbar;
Intent intent;
ImageView fullScreenImage;
TextView toolbarUsername;
RelativeLayout saveImageToGallery;
RelativeLayout closeImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_from_chat);
        fullScreenToolbar = (Toolbar) findViewById(R.id.fullscreentoolbar);
        setSupportActionBar(fullScreenToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        intent = getIntent();
        String username = intent.getStringExtra("username");
        final String image = intent.getStringExtra("imageString");

        toolbarUsername = (TextView) findViewById(R.id.userNameINToolbar);
        fullScreenImage = (ImageView) findViewById(R.id.imagefromChat);
        saveImageToGallery = (RelativeLayout) findViewById(R.id.savelayout);
        closeImage = (RelativeLayout) findViewById(R.id.closelayout);

        Picasso.with(this).load(image).into(fullScreenImage);
        toolbarUsername.setText(username);


        saveImageToGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              fullScreenImage.buildDrawingCache();
                Bitmap imageChat = fullScreenImage.getDrawingCache();
                saveFile(imageChat);
                Toast.makeText(FullScreenImageFromChat.this,"Image saved",Toast.LENGTH_SHORT).show();
            }
        });


        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScreenImageFromChat.this,ChatMessageActivity.class);
                startActivity(intent);

            }
        });



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

}
