package com.malikbisic.sportapp.activity;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageActivity extends AppCompatActivity {
    private String mChatUser;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    String mChatUsername;
    TextView mTitleView;
    TextView mLastSeenView;
    CircleImageView mProfileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mChatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mChatUser = getIntent().getStringExtra("userId");
        mChatUsername = getIntent().getStringExtra("username");
        // getSupportActionBar().setTitle(mChatUsername);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        mTitleView = (TextView) findViewById(R.id.chat_username);
        mLastSeenView = (TextView) findViewById(R.id.chat_last_seen);
        mProfileImg = (CircleImageView) findViewById(R.id.chat_imageview_id);

        mTitleView.setText(mChatUsername);
        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             String favoriteClubChat = dataSnapshot.child("favoriteClub").getValue().toString();

                mRootRef.child("UsersChat").child(favoriteClubChat).child(mChatUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String online = dataSnapshot.child("online").getValue().toString();
                        String image = dataSnapshot.child("profileImage").getValue().toString();

                        if (online.equals("true")){
                            mLastSeenView.setText("Online");
                        }else {
                            mLastSeenView.setText(online);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
          /*     */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
