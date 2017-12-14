package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.malikbisic.sportapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class SinglePostViewActivity extends AppCompatActivity{
    private String post_key = null;

    private CollectionReference postReference;
    private CollectionReference editPost;
    private ImageView post_image;
    private ImageView profile_image;
    private TextView username;
    private cn.jzvd.JZVideoPlayerStandard post_video;
    private EditText post_text_video;
    private EditText post_text_image;
    private EditText post_text_audio;
    private EditText post_only_text;
    private RelativeLayout layoutAudio;
    private RelativeLayout layoutImage;
    Toolbar editPostToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post_view);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        postReference = FirebaseFirestore.getInstance().collection("Posting");
        editPost = FirebaseFirestore.getInstance().collection("Posting");
        profile_image = (ImageView) findViewById(R.id.profile_image_wall);
        username = (TextView) findViewById(R.id.username_wall);
        post_image = (ImageView) findViewById(R.id.posted_image);
        post_video = (cn.jzvd.JZVideoPlayerStandard) findViewById(R.id.posted_video);
        post_text_image = (EditText) findViewById(R.id.text_for_image);
        post_text_video = (EditText) findViewById(R.id.text_for_video);
        post_text_audio = (EditText) findViewById(R.id.audio_textview);
        layoutAudio = (RelativeLayout) findViewById(R.id.layout_for_audio_player);
        layoutImage = (RelativeLayout) findViewById(R.id.layout_for_image);
        post_only_text = (EditText)  findViewById(R.id.post_text_main_page);
        editPostToolbar = (Toolbar) findViewById(R.id.editpostTooblar);
        setSupportActionBar(editPostToolbar);
        getSupportActionBar().setTitle("Edit post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Intent myIntent = getIntent();
        post_key = myIntent.getStringExtra("post_id");

        postReference.document(post_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                if (dataSnapshot.exists()) {



                    String profileImage = dataSnapshot.getString("profileImage");
                    String profileUsername = dataSnapshot.getString("username");
                    String postImage = dataSnapshot.getString("photoPost");
                    String postVideo = dataSnapshot.getString("videoPost");
                    String descVideo = dataSnapshot.getString("descVideo");
                    String descImage = dataSnapshot.getString("descForPhoto");
                    String postAudio = dataSnapshot.getString("audioFile");
                    String postText = dataSnapshot.getString("desc");

                    Picasso.with(SinglePostViewActivity.this).load(profileImage).into(profile_image);
                    username.setText(profileUsername);

                    if (postImage != null) {
                        layoutImage.setVisibility(View.VISIBLE);
                        post_text_video.setVisibility(View.GONE);
                        post_text_audio.setVisibility(View.GONE);
                        post_only_text.setVisibility(View.GONE);
                        post_text_image.setVisibility(View.VISIBLE);
                        post_text_image.setText(descImage);
                        Picasso.with(SinglePostViewActivity.this).load(postImage).into(post_image);
                    } else {

                        layoutImage.setVisibility(View.GONE);
                    }

                    if (postText != null) {
                        post_text_video.setVisibility(View.GONE);
                        post_text_audio.setVisibility(View.GONE);
                        post_text_image.setVisibility(View.GONE);

                        post_only_text.setText(postText);

                    }

                    if (postVideo != null) {
                        post_video.setVisibility(View.VISIBLE);
                        post_text_audio.setVisibility(View.GONE);
                        post_text_image.setVisibility(View.GONE);
                        post_only_text.setVisibility(View.GONE);
                        post_text_video.setText(descVideo);
                        post_video.setUp(postVideo, cn.jzvd.JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "proba");
                    } else {
                        post_video.setVisibility(View.GONE);
                    }

                    if (postAudio != null) {
                        post_text_image.setVisibility(View.GONE);
                        post_text_video.setVisibility(View.GONE);
                        Log.i("file", postAudio);
                    } else {
                        layoutAudio.setVisibility(View.GONE);
                    }
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_post_text, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.save_edit_text) {
           final DocumentReference editPostComplete =  editPost.document(post_key);
            final String newTextVideo = post_text_video.getText().toString().trim();
            final String newTextImage = post_text_image.getText().toString().trim();
            final String newTextAudio = post_text_audio.getText().toString().trim();
            final String newText = post_only_text.getText().toString().trim();
            editPostComplete.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
               if (documentSnapshot.exists()){

                   if (documentSnapshot.contains("desc")){
                   editPostComplete.update("desc",newText,"time",FieldValue.serverTimestamp()).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           Toast.makeText(SinglePostViewActivity.this,"uhuuu",Toast.LENGTH_SHORT);
                       }
                   });
                   }

                   if (documentSnapshot.contains("descForPhoto")) {
                       editPostComplete.update("descForPhoto", newTextImage, "time", FieldValue.serverTimestamp()).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(SinglePostViewActivity.this, "uhuuu", Toast.LENGTH_SHORT);
                           }
                       });

                   }
                   if (documentSnapshot.contains("descForAudio")) {
                       editPostComplete.update("descForAudio", newTextAudio, "time", FieldValue.serverTimestamp()).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(SinglePostViewActivity.this, "uhuuu", Toast.LENGTH_SHORT);
                           }
                       });
                   }

                   if (documentSnapshot.contains("descVideo")) {

                       editPostComplete.update("descVideo", newTextVideo, "time", FieldValue.serverTimestamp()).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(SinglePostViewActivity.this, "uhuuu", Toast.LENGTH_SHORT);
                           }
                       });
                   }





                   Intent backToMainPage = new Intent(SinglePostViewActivity.this, MainPage.class);
                   startActivity(backToMainPage);
                   finish();


               }
                }
            });



         /*   editPostComplete.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                    if (dataSnapshot.contains("descVideo")) {
                        Map<String, Object> descVideoMap = new HashMap<>();
                        descVideoMap.put("descVideo", newTextVideo);
                       DocumentReference edit = FirebaseFirestore.getInstance().collection("Posting").document(post_key);
                       edit.update(descVideoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Toast.makeText(SinglePostViewActivity.this, "Successfuly", Toast.LENGTH_LONG).show();
                               }
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Log.e("errorUpdate", e.getLocalizedMessage());
                           }
                       });
                    }

                    if (dataSnapshot.getString("desc")) {

                  FirebaseFirestore.getInstance().collection("Posting").document(post_key).update(
                    "desc",newText,
                          "time",FieldValue.serverTimestamp()
                  );
                        Intent backToMainPage = new Intent(SinglePostViewActivity.this, MainPage.class);
                        startActivity(backToMainPage);
                        finish();

                    }

                    if (dataSnapshot.contains("descForPhoto")) {
                        Map<String, Object> descPhotoMap = new HashMap<>();
                        descPhotoMap.put("descForPhoto", newTextImage);
                        DocumentReference edit = FirebaseFirestore.getInstance().collection("Posting").document(post_key);
                        edit.update(descPhotoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(SinglePostViewActivity.this, "Successfuly", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("errorUpdate", e.getLocalizedMessage());
                            }
                        });
                    }

                    if (dataSnapshot.contains("descForAudio")) {

                        Map<String, Object> descAudioMap = new HashMap<>();
                        descAudioMap.put("descForAudio", newTextAudio);
                       DocumentReference edit = FirebaseFirestore.getInstance().collection("Posting").document(post_key);
                       edit.update(descAudioMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Toast.makeText(SinglePostViewActivity.this, "Successfuly", Toast.LENGTH_LONG).show();
                               }
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Log.e("errorUpdate", e.getLocalizedMessage());
                           }
                       });

                    }



                }
            });*/

        }



        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent backToMainPage = new Intent(SinglePostViewActivity.this, MainPage.class);
        startActivity(backToMainPage);
        finish();
    }
}
