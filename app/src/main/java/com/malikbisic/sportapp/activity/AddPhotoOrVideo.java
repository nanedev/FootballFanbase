package com.malikbisic.sportapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;

import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

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
    private FirebaseAuth mAuth;


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
        mAuth = FirebaseAuth.getInstance();


        mFilePath = FirebaseStorage.getInstance().getReference();

        if (MainPage.photoSelected){
            photoSelected.setVisibility(View.VISIBLE);
            photoSelected.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            Uri imageUri = myIntent.getData();
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
                try {  Uri imageUri = myIntent.getData();
                File imagePath = new File(getRealPathFromURI(imageUri));
                Log.i("imagePath", imagePath.getPath());

                    Bitmap imageCompressBitmap = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .compressToBitmap(imagePath);

                final String aboutPhotoText = saySomething.getText().toString().trim();
                final String username = myIntent.getStringExtra("username");
                final String profileImage = myIntent.getStringExtra("profileImage");
                final String country = myIntent.getStringExtra("country");
                final String clubLogo = myIntent.getStringExtra("clubheader");

                Log.i("name", username);

                photoPost = mFilePath.child("Post_Photo").child(imageUri.getLastPathSegment());
                postingDialog.setMessage("Posting");
                postingDialog.show();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageCompressBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] data = baos.toByteArray();

                UploadTask uploadTask = photoPost.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        Map imageMap = new HashMap();
                        imageMap.put("descForPhoto", aboutPhotoText);
                        imageMap.put("username", username);
                        imageMap.put("profileImage", profileImage);
                        imageMap.put("photoPost", downloadUri.toString());
                        imageMap.put("uid", mAuth.getCurrentUser().getUid());
                        imageMap.put("country", country);
                        imageMap.put("clubLogo", clubLogo);
                        imageMap.put("favoritePostClub", MainPage.myClubName);

                        DatabaseReference newPost = postingDatabase.push();
                        newPost.updateChildren(imageMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null){
                                    Log.e("photoError", databaseError.getMessage().toString());
                                }
                            }
                        });
                        postingDialog.dismiss();
                        Intent goToMain = new Intent(AddPhotoOrVideo.this, MainPage.class);
                        startActivity(goToMain);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("errorPosting", e.getMessage());
                        postingDialog.dismiss();
                    }
                });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!MainPage.photoSelected){
                Uri videoUri = Uri.parse(myIntent.getStringExtra("video-uri_selected"));
                final String aboutVideoText = saySomething.getText().toString().trim();
                final String username = myIntent.getStringExtra("username");
                final String profileImage = myIntent.getStringExtra("profileImage");
                final String country = myIntent.getStringExtra("country");
                final String clubLogo = myIntent.getStringExtra("clubheader");
                postVideo = mFilePath.child("Post_Video").child(videoUri.getLastPathSegment());
                postingDialog.setMessage("Posting");
                postingDialog.show();

                postVideo.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        DatabaseReference newPost = postingDatabase.push();

                        Map videoMap = new HashMap();
                        videoMap.put("descVideo", aboutVideoText);
                        videoMap.put("username", username);
                        videoMap.put("profileImage", profileImage);
                        videoMap.put("videoPost", downloadUri.toString());
                        videoMap.put("uid", mAuth.getCurrentUser().getUid());
                        videoMap.put("country", country);
                        videoMap.put("clubLogo", clubLogo);
                        videoMap.put("favoritePostClub", MainPage.myClubName);

                        newPost.updateChildren(videoMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.e("VideoError", databaseError.getMessage().toString());
                                }
                            }
                        });
                        postingDialog.dismiss();
                        Intent goToMain = new Intent(AddPhotoOrVideo.this, MainPage.class);
                        startActivity(goToMain);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("errorPosting", e.getLocalizedMessage());
                        postingDialog.dismiss();
                    }
                });
            }


        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
