package com.malikbisic.sportapp.activity.firebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.BackgroundPostAdapter;

public class BackgroundPostActivity extends AppCompatActivity {


    private int backgrounds[] = {R.drawable.background_post_default,R.drawable.background_post_one,R.drawable.background_post_two,R.drawable.background_post_three,
    R.drawable.background_post_four,R.drawable.background_post_five,R.drawable.background_post_six};
RecyclerView backgroundsRecyclerview;
BackgroundPostAdapter adapter;
GridLayoutManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_post);

        backgroundsRecyclerview = (RecyclerView)findViewById(R.id.backgroundsrecyclerview);
        manager = new GridLayoutManager(BackgroundPostActivity.this,3);
        backgroundsRecyclerview.setHasFixedSize(true);
        backgroundsRecyclerview.setLayoutManager(manager);
        adapter = new BackgroundPostAdapter(BackgroundPostActivity.this,backgrounds);
        backgroundsRecyclerview.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        Intent backToOnlyPost = new Intent(BackgroundPostActivity.this, OnlyPostActivity.class);
        startActivity(backToOnlyPost);
    }
}
