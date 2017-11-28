package com.malikbisic.sportapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecordAudio extends AppCompatActivity {
    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording, buttonUploadAudio, buttonPost;
    String AudioSavePathInDevice = null;
    TextView stateText;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    StorageReference mStorage;
    FirebaseFirestore postAudio;
    ProgressDialog mDialog;
    Uri uriAudio;
    EditText aboutAudio;
    FirebaseAuth mAuth;
    //komentar
    private static final String LOG_TAG = "record_log";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postAudio = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_record_audio);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        buttonStart = (Button) findViewById(R.id.button);
        buttonStop = (Button) findViewById(R.id.button2);
        buttonPlayLastRecordAudio = (Button) findViewById(R.id.button3);
        buttonStopPlayingRecording = (Button) findViewById(R.id.button4);
        buttonUploadAudio = (Button) findViewById(R.id.upload_audio);
        buttonPost = (Button) findViewById(R.id.post_btn);
        mDialog = new ProgressDialog(this);
        stateText = (TextView) findViewById(R.id.state_textview);
        aboutAudio = (EditText) findViewById(R.id.audioAbout);
        mAuth = FirebaseAuth.getInstance();
        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);
        buttonUploadAudio.setEnabled(false);
        buttonPost.setEnabled(false);
        mStorage = FirebaseStorage.getInstance().getReference();

        random = new Random();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);
                    buttonPlayLastRecordAudio.setEnabled(false);
                    buttonUploadAudio.setEnabled(false);
                    buttonPost.setEnabled(false);
                    stateText.setVisibility(View.VISIBLE);
                    stateText.setText("Recording..");
                    stateText.setTextColor(Color.parseColor("#1b5e20"));

                } else {
                    requestPermission();
                }

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonUploadAudio.setEnabled(true);
                buttonPost.setEnabled(false);

                stateText.setVisibility(View.VISIBLE);
                stateText.setText("Recording completed!");
                stateText.setTextColor(Color.parseColor("#d50000"));

            }
        });

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);
                buttonUploadAudio.setEnabled(false);
                buttonPost.setEnabled(false);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

                stateText.setVisibility(View.VISIBLE);
                stateText.setText("Recording playing!");
                stateText.setTextColor(Color.parseColor("#1a237e"));

            }
        });

        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonUploadAudio.setEnabled(true);
                buttonPost.setEnabled(false);
                stateText.setVisibility(View.INVISIBLE);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });

        buttonUploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStart.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(false);
                buttonStop.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(false);

                mDialog.setMessage("Uploading...");
                mDialog.show();
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("audio/mpeg")
                        .build();
                StorageReference filePath = mStorage.child("Audio").child(CreateRandomAudioFileName(5));
                Uri uri = Uri.fromFile(new File(AudioSavePathInDevice));
                filePath.putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uriAudio = taskSnapshot.getDownloadUrl();
                        mDialog.dismiss();
                        buttonStart.setEnabled(true);
                        buttonStopPlayingRecording.setEnabled(false);
                        buttonStop.setEnabled(false);
                        buttonPlayLastRecordAudio.setEnabled(false);
                        buttonPost.setEnabled(true);
                        buttonUploadAudio.setEnabled(false);
                        stateText.setVisibility(View.VISIBLE);
                        stateText.setTextSize(14.0f);
                        stateText.setTextColor(Color.parseColor("#263238"));
                        stateText.setText("Click Post to post file to wall");
                    }
                });

            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String country = MainPage.country.toString();
                final String clubLogo = MainPage.clubHeaderString;
                String about = aboutAudio.getText().toString().trim();
                mDialog.setMessage("Posting...");
                mDialog.show();


                /*newPost.child("audioFile").setValue(uriAudio.toString());
                newPost.child("username").setValue(MainPage.usernameInfo);
                newPost.child("profileImage").setValue(MainPage.profielImage);
                newPost.child("descForAudio").setValue(about);
                newPost.child("uid").setValue(mAuth.getCurrentUser().getUid());
                newPost.child("country").setValue(country);
                newPost.child("clubLogo").setValue(clubLogo);*/

                Map<String, Object> audioMap = new HashMap<>();
                audioMap.put("audioFile", uriAudio.toString());
                audioMap.put("username", MainPage.usernameInfo);
                audioMap.put("profileImage", MainPage.profielImage);
                audioMap.put("descForAudio", about);
                audioMap.put("uid", mAuth.getCurrentUser().getUid());
                audioMap.put("country", country);
                audioMap.put("clubLogo", clubLogo);
                audioMap.put("favoritePostClub", MainPage.myClubName);

                postAudio.collection("Posting").add(audioMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            String key = task.getResult().getId();
                            Map<String,Object> keyUpdate = new HashMap<>();
                            keyUpdate.put("key", key);
                            postAudio.collection("Posting").document(key).update(keyUpdate);
                        }
                    }
                });
                mDialog.dismiss();
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(false);
                buttonPost.setEnabled(false);
                buttonUploadAudio.setEnabled(false);
                Intent goToMain = new Intent(RecordAudio.this, MainPage.class);
                startActivity(goToMain);
            }
        });

    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(RecordAudio.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(RecordAudio.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RecordAudio.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


}
