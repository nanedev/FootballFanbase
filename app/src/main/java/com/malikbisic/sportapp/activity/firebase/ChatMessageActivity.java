package com.malikbisic.sportapp.activity.firebase;

import android.content.Context;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.utils.GetTimeAgo;
import com.malikbisic.sportapp.adapter.firebase.MessageAdapter;
import com.malikbisic.sportapp.model.firebase.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageActivity extends AppCompatActivity {
    private String mChatUser;
    private Toolbar mChatToolbar;
    private FirebaseFirestore mRootRef;
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
    String lastkey = "";
    String firstKey = "";
    int itemPos = 0;
    int loaditem = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        mRootRef = FirebaseFirestore.getInstance();
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
        checkAllLoaded();

        mTitleView.setText(mChatUsername);
        mRootRef.collection("Users").document(mChatUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                String favoriteClubChat = dataSnapshot.getString("favoriteClub");

                mRootRef.collection("UsersChat").document(favoriteClubChat).collection("user-id").document(mChatUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e1) {
                        String online = dataSnapshot.getString("online");
                        String image = dataSnapshot.getString("profileImage");

                        if (online.equals("true")) {
                            mLastSeenView.setText("Online");
                        } else {
                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            long lastTime = Long.parseLong(online);
                            String lastStringTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                            mLastSeenView.setText(lastStringTime);
                        }
                    }
                });

            }
        });

        mRootRef.collection("Chat").document(mCurrentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e1) {

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.contains(mChatUser)) {
                        Map chatAddMap = new HashMap();
                        chatAddMap.put("seen", false);
                        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                        Map chatUserMap = new HashMap();
                        chatUserMap.put(mCurrentUserId + "/" + mChatUser, chatAddMap);
                        chatUserMap.put(mChatUser + "/" + mCurrentUserId, chatAddMap);

                        mRootRef.collection("Chat").document(mCurrentUserId).collection(mChatUser).add(chatAddMap);
                        mRootRef.collection("Chat").document(mChatUser).collection(mCurrentUserId).add(chatAddMap);


                    }
                }
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

                itemPos = 0;
                loadMoreMessages();

            }


        });

    }
    private void loadMoreMessages() {
        /*

        final DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(lastkey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                messagesList.add(itemPos++, message);

                if (itemPos == 1){
                    String getLastKey = dataSnapshot.getKey();
                    lastkey = getLastKey;

                    if (lastkey.equals(firstKey)){
                        mRefreshLayout.setEnabled(false);
                    }

                }



                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
                mLinearLayout.scrollToPositionWithOffset(10, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); */
    }

    private void checkAllLoaded(){
/*
        final DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                loaditem++;
                if (loaditem == 1) {
                    firstKey = dataSnapshot.getKey();

                    Log.i("firstKey", firstKey);
                }
                    Log.i("firstLAST", lastkey);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); */

    }

    private void loadMessages() {


        final CollectionReference messageRef = mRootRef.collection("messages").document(mCurrentUserId).collection(mChatUser);
        com.google.firebase.firestore.Query messageQuery = messageRef.limit(TOTAL_ITEMS_TO_LOAD);


        messageQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()){
                    Messages message = snapshot.toObject(Messages.class);

                    itemPos++;
                    if (itemPos == 1){
                        String getLastKey = snapshot.getId();
                        lastkey = getLastKey;
                    }
                    messagesList.add(message);

                    mMessagesList.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });


    }

    private void sendMessage() {
        String message = mChatMessageView.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", FieldValue.serverTimestamp());
            messageMap.put("from", mCurrentUserId);

            mRootRef.collection("messages").document(mCurrentUserId).collection(mChatUser).add(messageMap);
            mRootRef.collection("messages").document(mChatUser).collection(mCurrentUserId).add(messageMap);

            mChatMessageView.setText("");


        }
    }
}
