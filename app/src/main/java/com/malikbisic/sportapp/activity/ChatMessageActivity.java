package com.malikbisic.sportapp.activity;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.MessageAdapter;
import com.malikbisic.sportapp.model.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageActivity extends AppCompatActivity {
    private String mChatUser;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    String mChatUsername;
    TextView mTitleView;
    TextView mLastSeenView;
    CircleImageView mProfileImg;
    FirebaseAuth mAuth;
    private String mCurrentUserId;
    ImageButton mChatAddBtn;
    ImageButton mChatSendBtn;
    EditText mChatMessageView;
    RecyclerView mMessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    MessageAdapter mAdapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private SwipeRefreshLayout mRefreshLayout;
    boolean refreshing;
    String lastkey;

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
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("userId");
        mChatUsername = getIntent().getStringExtra("username");
        // getSupportActionBar().setTitle(mChatUsername);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        mTitleView = (TextView) findViewById(R.id.chat_username);
        mLastSeenView = (TextView) findViewById(R.id.chat_last_seen);
        mProfileImg = (CircleImageView) findViewById(R.id.chat_imageview_id);
        mChatAddBtn = (ImageButton) findViewById(R.id.plus_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.send_message);
        mChatMessageView = (EditText) findViewById(R.id.chat_text);
        mMessagesList = (RecyclerView) findViewById(R.id.messageList);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);
        mLinearLayout.setStackFromEnd(true);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mAdapter = new MessageAdapter(messagesList, getApplicationContext());
        mMessagesList.setAdapter(mAdapter);

        loadMessages();

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

                        if (online.equals("true")) {
                            mLastSeenView.setText("Online");
                        } else {
                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            long lastTime = Long.parseLong(online);
                            String lastStringTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                            mLastSeenView.setText(lastStringTime);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("Chat").child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(mChatUser)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing = true;
                mCurrentPage++;
                messagesList.clear();


                DatabaseReference limitRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
                Query limitQuery = limitRef.orderByKey().endAt(lastkey).limitToLast(TOTAL_ITEMS_TO_LOAD);
                limitQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Messages message = snapshot.getValue(Messages.class);
                            messagesList.add(message);
                            mMessagesList.smoothScrollToPosition((int) snapshot.getChildrenCount() + 1);
                            lastkey = snapshot.getKey();
                            mAdapter.notifyDataSetChanged();
                            mRefreshLayout.setRefreshing(false);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        });

    }

    private void loadMessages() {


        final DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(TOTAL_ITEMS_TO_LOAD);

        messageQuery.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    refreshing = false;
                    Messages message = snap.getValue(Messages.class);
                    messagesList.add(message);
                    lastkey = snap.getKey();

                    mMessagesList.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = mChatMessageView.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;
            DatabaseReference userMessagePush = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();

            String push_id = userMessagePush.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);


            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
            mChatMessageView.setText("");
            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null)
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                }
            });


        }
    }
}
