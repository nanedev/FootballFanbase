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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.firebase.Message;
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
    DocumentSnapshot lastkey;
    DocumentSnapshot firstKey;
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
        mMessagesList.setLayoutManager(mLinearLayout);
        mAdapter = new MessageAdapter(messagesList, getApplicationContext(), this);
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
               // loadMoreMessages();

            }


        });

    }
    private void loadMoreMessages() {


        final CollectionReference messageRef = mRootRef.collection("messages").document(mCurrentUserId).collection(mChatUser);
        com.google.firebase.firestore.Query messageQuery = messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).endAt(lastkey).limit(10);
        messageQuery.addSnapshotListener(ChatMessageActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()) {
                    Messages message = snapshot.toObject(Messages.class);
                    messagesList.add(itemPos++, message);


                        DocumentSnapshot getLastKey = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size()-1);
                        lastkey = getLastKey;

                        if (lastkey.equals(firstKey)) {
                            mRefreshLayout.setEnabled(false);
                        }


                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
                    mLinearLayout.scrollToPositionWithOffset(10, 0);
                }
            }
        });


    }

    private void checkAllLoaded(){

        final CollectionReference messageRef = mRootRef.collection("messages").document(mCurrentUserId).collection(mChatUser);
        messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.ASCENDING).addSnapshotListener(ChatMessageActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException s) {
                loaditem++;
                if (loaditem == 1) {


                    Log.i("firstKey", String.valueOf(firstKey));
                }
                Log.i("firstLAST", String.valueOf(lastkey));


            }
        });

    }

    private void loadMessages() {


        CollectionReference messageRef = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
        messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.ASCENDING).limit(10).addSnapshotListener(ChatMessageActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {


                for (DocumentChange snapshot : documentSnapshots.getDocumentChanges()) {

                    if (snapshot.getType() == DocumentChange.Type.ADDED) {
                        Messages message = snapshot.getDocument().toObject(Messages.class);

                        messagesList.add(message);

                        mMessagesList.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.setRefreshing(false);

                        //lastItem = documentSnapshots.getDocuments().get(documentSnapshots.size());

                    }
                }

               /* com.google.firebase.firestore.Query newitem = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
                newitem.orderBy("time", com.google.firebase.firestore.Query.Direction.ASCENDING).startAt(documentSnapshots.getDocuments()).limit(10);
                newitem.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        for (DocumentChange snapshot : documentSnapshots.getDocumentChanges()) {

                            if (snapshot.getType() == DocumentChange.Type.ADDED) {
                                Messages message = snapshot.getDocument().toObject(Messages.class);

                                messagesList.add(message);

                                mMessagesList.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                                mAdapter.notifyDataSetChanged();
                                mRefreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });*/
            }
        });


    }

    private void sendMessage() {
        String message = mChatMessageView.getText().toString();
        if (!TextUtils.isEmpty(message)) {

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", FieldValue.serverTimestamp());
            messageMap.put("from", mCurrentUserId);

            mRootRef.collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message").add(messageMap);
            mRootRef.collection("Messages").document(mChatUser).collection("chat-user").document(mCurrentUserId).collection("message").add(messageMap);

            Map chatUser = new HashMap();
            chatUser.put("to", mChatUser);
            Map mychatUser = new HashMap();
            chatUser.put("to", mCurrentUserId);
            mRootRef.collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).set(chatUser);
            mRootRef.collection("Messages").document(mChatUser).collection("chat-user").document(mCurrentUserId).set(mychatUser);

            mChatMessageView.setText("");


        }
    }
}
