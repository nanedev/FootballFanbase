package com.malikbisic.sportapp.activity.firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.ImageAlbumAdapter;
import com.malikbisic.sportapp.adapter.firebase.MultiSelectImageAdapter;
import com.malikbisic.sportapp.utils.ImageAlbumName;
import com.malikbisic.sportapp.utils.ImageConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class SendImageChatActivity extends AppCompatActivity {

    Toolbar mChatToolbar;
    private ImageAlbumName utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private MultiSelectImageAdapter adapter;
    private RecyclerView gridView;
    private int columnWidth;

    FirebaseAuth mAuth;
    Intent myIntent;
    String myUID;
    String userID;
    int i = 0;

    int sizeImageSent;

    ArrayList<String> imageUri = new ArrayList<>();
    Map<String, Object> messageMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image_chat);

        adapter.checkedPath.clear();
        imageUri.clear();
        mChatToolbar = (Toolbar) findViewById(R.id.chatImage_toolbar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Send image...");
        myIntent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        myUID = mAuth.getCurrentUser().getUid();
        userID = myIntent.getStringExtra("userID");

        gridView = (RecyclerView) findViewById(R.id.grid_view);

        utils = new ImageAlbumName(this);


        // loading all image paths from SD card
        imagePaths = utils.getFilePaths();

        // Gridview adapter
        adapter = new MultiSelectImageAdapter(SendImageChatActivity.this, imagePaths, myUID, userID);

        // setting grid view adapter
        GridLayoutManager manager = new GridLayoutManager(this, 3);

        gridView.setLayoutManager(manager);
        gridView.setAdapter(adapter);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_send, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent backToChat = new Intent(SendImageChatActivity.this, ChatMessageActivity.class);
                backToChat.putExtra("userId", userID);
                setResult(Activity.RESULT_OK, backToChat);
                finish();
                return true;
            case R.id.send_images:

                sendImage();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void sendImage() {
        try {

            final ProgressDialog dialogg = new ProgressDialog(SendImageChatActivity.this);
            dialogg.setMessage("Sending...");
            dialogg.show();
            sizeImageSent = adapter.checkedPath.size();
            Log.i("size", String.valueOf(sizeImageSent));
            for (Object image : adapter.checkedPath.keySet()) {

                File imagePath = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    imagePath = new File(adapter.checkedPath.get(image));
                }
                Log.i("imagePath", imagePath.getPath());

                final Bitmap imageCompressBitmap = new Compressor(SendImageChatActivity.this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .compressToBitmap(imagePath);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageCompressBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] data = baos.toByteArray();
                StorageReference mFilePath = FirebaseStorage.getInstance().getReference();
                StorageReference photoPost = mFilePath.child("Chat_Image").child(imagePath.getName());
                UploadTask uploadTask = photoPost.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        if (taskSnapshot.getTask().isSuccessful()) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            i++;
                            imageUri.add(downloadUri.toString());


                            if (i == sizeImageSent) {

                                messageMap = new HashMap<>();
                                messageMap.put("message", "null");
                                messageMap.put("seen", false);
                                messageMap.put("galleryImage", imageUri);
                                messageMap.put("type", "gallery");
                                messageMap.put("time", FieldValue.serverTimestamp());
                                messageMap.put("from", myUID);

                                FirebaseFirestore mRootRef = FirebaseFirestore.getInstance();


                                mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).collection("message").add(messageMap);
                                mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).collection("message").add(messageMap);

                                Map<String, Object> chatUser = new HashMap<>();
                                chatUser.put("to", userID);
                                chatUser.put("typing", false);
                                Map<String, Object> mychatUser = new HashMap<>();
                                mychatUser.put("to", myUID);
                                mychatUser.put("typing", false);
                                mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).set(chatUser);
                                mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).set(mychatUser);
                                dialogg.dismiss();

                            }
                 /*   Intent goToMain = new Intent(_activity, SendImageChatActivity.class);
                    goToMain.putExtra("userId", userID);
                    _activity.startActivity(goToMain);*/


                    } else

                    {
                        String error = taskSnapshot.getError().getMessage();
                        Log.e("errorImage", error);
                        dialogg.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("errorPosting", e.getMessage());
                    dialogg.dismiss();

                }
            });
        }


    } catch(
    IOException e)

    {
        e.printStackTrace();
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
