package com.malikbisic.sportapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddPhotoOrVideo extends AppCompatActivity implements View.OnClickListener {

    private Intent myIntent;
    private ImageView photoSelected;
    private VideoView videoSelected;
    private EditText saySomething;
    private Button post;
    ProgressDialog pDialog;
    ProgressDialog postingDialog;

    private StorageReference mFilePath;
    private StorageReference photoPost;
    private StorageReference postVideo;
    private FirebaseStorage mStorage;
    private DatabaseReference postingDatabase;
    private FirebaseDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_or_video);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        myIntent = getIntent();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        postingDatabase = FirebaseDatabase.getInstance().getReference().child("Posting");
        photoSelected = (ImageView) findViewById(R.id.post_image);
        videoSelected = (VideoView) findViewById(R.id.post_video);
        saySomething = (EditText) findViewById(R.id.tell_something_about_video_image);
        post = (Button) findViewById(R.id.btn_post_photo_or_video);
        post.setOnClickListener(this);
        postingDialog = new ProgressDialog(this);


        mFilePath = FirebaseStorage.getInstance().getReference();

        if (MainPage.photoSelected){
            photoSelected.setVisibility(View.VISIBLE);
            photoSelected.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            Uri imageUri = Uri.parse(myIntent.getStringExtra("image-uri_selected"));
            photoSelected.setImageURI(imageUri);

        } else if (!MainPage.photoSelected) {
            photoSelected.setVisibility(View.GONE);
            videoSelected.setVisibility(View.VISIBLE);
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Buffering");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            try {
                // Start the MediaController
                MediaController mediacontroller = new MediaController(
                        AddPhotoOrVideo.this);
                mediacontroller.setAnchorView(videoSelected);
                // Get the URL from String VideoURL
                Uri video = Uri.parse(myIntent.getStringExtra("video-uri_selected"));
                videoSelected.setMediaController(mediacontroller);
                videoSelected.setVideoURI(video);
                videoSelected.pause();

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            videoSelected.requestFocus();
            videoSelected.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    pDialog.dismiss();
                    //videoSelected.start();
                }
            });

        }


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_post_photo_or_video){

            if (MainPage.photoSelected) {
                Uri imageUri = Uri.parse(myIntent.getStringExtra("image-uri_selected"));
                final String aboutPhotoText = saySomething.getText().toString().trim();
                final String username = MainPage.usernameInfo;
                final String profileImage = MainPage.profielImage;

                Log.i("name", username);

                photoPost = mFilePath.child("Post_Photo").child(imageUri.getLastPathSegment());
                postingDialog.setMessage("Posting");
                postingDialog.show();

                photoPost.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        DatabaseReference newPost = postingDatabase.push();
                        newPost.child("descForPhoto").setValue(aboutPhotoText);
                        newPost.child("username").setValue(username);
                        newPost.child("profileImage").setValue(profileImage);
                        newPost.child("photoPost").setValue(downloadUri.toString());
                        postingDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("errorPosting", e.getMessage());
                        postingDialog.dismiss();
                    }
                });
            } else if (!MainPage.photoSelected){
                Uri videoUri = Uri.parse(myIntent.getStringExtra("video-uri_selected"));
                final String aboutVideoText = saySomething.getText().toString().trim();
                final String username = MainPage.usernameInfo;
                final String profileImage = MainPage.profielImage;

                postVideo = mFilePath.child("Post_Video").child(videoUri.getLastPathSegment());
                postingDialog.setMessage("Posting");
                postingDialog.show();

                postVideo.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        DatabaseReference newPost = postingDatabase.push();
                        newPost.child("descVideo").setValue(aboutVideoText);
                        newPost.child("username").setValue(username);
                        newPost.child("profileImage").setValue(profileImage);
                        newPost.child("videoPost").setValue(downloadUri.toString());
                        postingDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("errorPosting", e.getMessage());
                        postingDialog.dismiss();
                    }
                });
            }
        }

    }
}
