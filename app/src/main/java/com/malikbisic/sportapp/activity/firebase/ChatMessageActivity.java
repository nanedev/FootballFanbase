package com.malikbisic.sportapp.activity.firebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseOptions;
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
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.model.firebase.Message;
import com.malikbisic.sportapp.model.firebase.UserChat;
import com.malikbisic.sportapp.utils.GetTimeAgo;
import com.malikbisic.sportapp.adapter.firebase.MessageAdapter;
import com.malikbisic.sportapp.model.firebase.Messages;

import java.util.ArrayList;
import java.util.Date;
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
    private int mCurrentPage = 10;
    private int scrollPosition = 0;
    private SwipeRefreshLayout mRefreshLayout;
    boolean refreshing;
    DocumentSnapshot lastItem;
    DocumentSnapshot firstKey;
    DocumentSnapshot lastkey;
    int itemPos = 0;
    int loaditem = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

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
        mLinearLayout.setReverseLayout(true);

        mMessagesList.setLayoutManager(mLinearLayout);
        mAdapter = new MessageAdapter(messagesList, getApplicationContext(), this);

        loadMessages(this);
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

                        if (online.equals("true") && online != null) {
                            mLastSeenView.setText("Online");
                        } else {
                            Date onlineDate = dataSnapshot.getDate("online");
                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            long lastTime = onlineDate.getTime();
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
                mCurrentPage = mCurrentPage + 10;
                scrollPosition += 10;
                loadMessages(ChatMessageActivity.this);

                itemPos = 0;
               // loadMoreMessages(ChatMessageActivity.this);

            }


        });

    }
    private void loadMoreMessages(final Activity activity) {




        final CollectionReference messageRef = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
        final com.google.firebase.firestore.Query messageQuery = messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).startAfter(lastkey).limit(10);
        messageQuery.addSnapshotListener(ChatMessageActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

            }
        });


        final FirestoreRecyclerOptions<Messages> options = new FirestoreRecyclerOptions.Builder<Messages>()
                .setQuery(messageQuery, Messages.class).build();

        FirestoreRecyclerAdapter<Messages, MessageAdapter.MessageViewHolder> adapter = new FirestoreRecyclerAdapter<Messages, MessageAdapter.MessageViewHolder>(options) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void onBindViewHolder(final MessageAdapter.MessageViewHolder holder, int position, Messages model) {
                FirebaseAuth  mAutH = FirebaseAuth.getInstance();


                messageQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        lastkey = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    }
                });
                String current_user_id = mAutH.getCurrentUser().getUid();


                String from_user = model.getFrom();
                if (from_user != null) {

                    if (from_user.equals(current_user_id)) {

                        holder.messagetTextTexview.setBackgroundColor(Color.WHITE);
                        holder.messagetTextTexview.setTextColor(R.color.black);
                        holder.layout.setGravity(Gravity.RIGHT);
                        holder.messagetTextTexview.setTypeface(holder.messagetTextTexview.getTypeface(), Typeface.BOLD);
                        holder.profileImageImg.setVisibility(View.GONE);

                    } else {
                        holder.messagetTextTexview.setBackgroundResource(R.drawable.message_text_background);
                        holder.messagetTextTexview.setTextColor(Color.WHITE);
                        holder.layout.setGravity(Gravity.LEFT);
                        holder.messagetTextTexview.setTypeface(holder.messagetTextTexview.getTypeface(), Typeface.BOLD);
                        holder.profileImageImg.setVisibility(View.VISIBLE);

                        FirebaseFirestore displayImage = FirebaseFirestore.getInstance();

                        displayImage.collection("Users").document(from_user).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                UserChat model2 = dataSnapshot.toObject(UserChat.class);
                                String profileImage = model2.getProfileImage();

                                holder.setProfileImageImg(activity, profileImage);
                            }
                        });
                    }
                    if (mRefreshLayout.isRefreshing()){


                    }
                    mRefreshLayout.setRefreshing(false);



                }

                holder.messagetTextTexview.setText(model.getMessage());
                if (model.getTime() != null) {
                    String time = DateUtils.formatDateTime(activity, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                    holder.timeTextView.setText(time);
                }
            }

            @Override
            public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
                return new MessageAdapter.MessageViewHolder(v);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getLocalizedMessage());
            }
        };

        mMessagesList.setAdapter(adapter);

        adapter.startListening();


    }

    private void checkAllLoaded(){

    }

    private void loadMessages(final Activity activity) {


        final CollectionReference messageRef = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
           final com.google.firebase.firestore.Query messageQuery = messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(mCurrentPage);

        final FirestoreRecyclerOptions<Messages> options = new FirestoreRecyclerOptions.Builder<Messages>()
                .setQuery(messageQuery, Messages.class).build();
        messageQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots.size() != 0) {
                    lastkey = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    CollectionReference messageRef = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
                    messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).addSnapshotListener(ChatMessageActivity.this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot querySnapshot2, FirebaseFirestoreException s) {
                            lastItem = querySnapshot2.getDocuments().get(querySnapshot2.size() - 1);;
                            if (lastkey.getId().equals(lastItem.getId())){
                                mRefreshLayout.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
        final FirestoreRecyclerAdapter<Messages, MessageAdapter.MessageViewHolder> adapter = new FirestoreRecyclerAdapter<Messages, MessageAdapter.MessageViewHolder>(options) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void onBindViewHolder(final MessageAdapter.MessageViewHolder holder, final int position, Messages model) {
             FirebaseAuth  mAutH = FirebaseAuth.getInstance();





                String current_user_id = mAutH.getCurrentUser().getUid();


                String from_user = model.getFrom();
                if (from_user != null) {

                    if (from_user.equals(current_user_id)) {

                        holder.messagetTextTexview.setBackgroundColor(Color.WHITE);
                        holder.messagetTextTexview.setTextColor(R.color.black);
                        holder.layout.setGravity(Gravity.RIGHT);
                        holder.messagetTextTexview.setTypeface(holder.messagetTextTexview.getTypeface(), Typeface.BOLD);
                        holder.profileImageImg.setVisibility(View.GONE);

                    } else {
                        holder.messagetTextTexview.setBackgroundResource(R.drawable.message_text_background);
                        holder.messagetTextTexview.setTextColor(Color.WHITE);
                        holder.layout.setGravity(Gravity.LEFT);
                        holder.messagetTextTexview.setTypeface(holder.messagetTextTexview.getTypeface(), Typeface.BOLD);
                        holder.profileImageImg.setVisibility(View.VISIBLE);

                        FirebaseFirestore displayImage = FirebaseFirestore.getInstance();

                        displayImage.collection("Users").document(from_user).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                UserChat model2 = dataSnapshot.toObject(UserChat.class);
                                String profileImage = model2.getProfileImage();

                                holder.setProfileImageImg(activity, profileImage);
                            }
                        });
                    }



                    Log.i("position", String.valueOf(position));
                    if (mRefreshLayout.isRefreshing()) {
                        mMessagesList.smoothScrollToPosition(getItemCount() - 6);
                    }

                    mMessagesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                         int totalItem = mLinearLayout.getItemCount();
                         int lastVisible = mLinearLayout.findLastVisibleItemPosition();

                            if(mRefreshLayout.isRefreshing()  && totalItem <= (lastVisible + 10) )
                            {
                                mMessagesList.smoothScrollToPosition(lastVisible + 15);
                            }
                        }
                    });
                    mRefreshLayout.setRefreshing(false);
                   // mMessagesList.smoothScrollToPosition(0);




                }

                holder.messagetTextTexview.setText(model.getMessage());
                if (model.getTime() != null) {
                    String time = DateUtils.formatDateTime(activity, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                    holder.timeTextView.setText(time);
                }
            }

            @Override
            public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
                return new MessageAdapter.MessageViewHolder(v);
            }
        };

        mMessagesList.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition =
                         mLinearLayout.findFirstCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                Log.i("newItem", String.valueOf(itemCount));
                if (lastVisiblePosition == 0 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessagesList.scrollToPosition(positionStart);
                }
            }
        });
        adapter.startListening();

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
