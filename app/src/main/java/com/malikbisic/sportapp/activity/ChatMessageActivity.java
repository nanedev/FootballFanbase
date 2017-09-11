package com.malikbisic.sportapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.malikbisic.sportapp.R;

public class ChatMessageActivity extends AppCompatActivity {
    private String mChatUser;
private Toolbar mChatToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);



        mChatUser = getIntent().getStringExtra("userId");
    }
}
