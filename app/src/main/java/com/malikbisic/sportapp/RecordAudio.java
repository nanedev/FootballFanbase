package com.malikbisic.sportapp;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class RecordAudio extends AppCompatActivity implements View.OnTouchListener {
    Button holdToRecordBtn;
    TextView recordingState;
    private MediaRecorder mRecorder;
    String mFileName = null;
    StorageReference audioReference;
    StorageReference filePath;
    private ProgressDialog mDialog;

    private static final String LOG_TAG = "record_log";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        holdToRecordBtn = (Button) findViewById(R.id.record_voice_btn);
        recordingState = (TextView) findViewById(R.id.record_state);

        mDialog = new ProgressDialog(this);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";

        holdToRecordBtn.setOnTouchListener(this);

        audioReference = FirebaseStorage.getInstance().getReference();

    }


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v.getId() == R.id.record_voice_btn) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                startRecording();
                recordingState.setText("Recording started....");

            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                stopRecording();
                recordingState.setText("Recording stopped!");
                uploadAudio();
            }

        }


        return false;
    }

    private void uploadAudio() {
        mDialog.setMessage("Uploading audio...");
        mDialog.show();
        filePath = audioReference.child("Audio").child("new_audio.3gp");
        Uri uri = Uri.fromFile(new File(mFileName));
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mDialog.dismiss();
            }
        });
    }
}
