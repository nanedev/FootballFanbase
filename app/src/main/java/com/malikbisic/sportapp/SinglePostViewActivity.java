package com.malikbisic.sportapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SinglePostViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post_view);

        Intent myIntent = getIntent();
        Toast.makeText(this, myIntent.getStringExtra("post_id"), Toast.LENGTH_LONG).show();
    }
}
