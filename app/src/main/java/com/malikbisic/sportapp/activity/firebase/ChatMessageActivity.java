package com.malikbisic.sportapp.activity.firebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
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

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.adapter.firebase.ImageAlbumAdapter;
import com.malikbisic.sportapp.model.firebase.Message;
import com.malikbisic.sportapp.model.firebase.UserChat;
import com.malikbisic.sportapp.utils.GetTimeAgo;
import com.malikbisic.sportapp.adapter.firebase.MessageAdapter;
import com.malikbisic.sportapp.model.firebase.Messages;
import com.malikbisic.sportapp.utils.ImageAlbumName;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
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
    EmojiconEditText mChatMessageView;
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
    boolean online;

    private ImageAlbumName utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private ImageAlbumAdapter adapter;
    private RecyclerView recyclerViewAlbum;


    ImageView proba;
    FrameLayout emoticons;
    RelativeLayout botomChatLay;
    boolean firstImageClick = true;
    boolean secondImageClick = false;

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
        recyclerViewAlbum = (RecyclerView) findViewById(R.id.album);
        emoticons = (FrameLayout) findViewById(R.id.emojicons);
        botomChatLay = (RelativeLayout) findViewById(R.id.chatdole);

        mChatUser = getIntent().getStringExtra("userId");
        mChatUsername = getIntent().getStringExtra("username");
        recyclerViewAlbum = (RecyclerView) findViewById(R.id.album);

        utils = new ImageAlbumName(this);

        imagePaths = utils.getFilePaths();

        // Gridview adapter
        adapter = new ImageAlbumAdapter(ChatMessageActivity.this, imagePaths, mAuth.getCurrentUser().getUid(), mChatUser);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewAlbum.setLayoutManager(manager);
        recyclerViewAlbum.setAdapter(adapter);
        // getSupportActionBar().setTitle(mChatUsername);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        mTitleView = (TextView) findViewById(R.id.chat_username);
        mLastSeenView = (TextView) findViewById(R.id.chat_last_seen);
        mProfileImg = (CircleImageView) findViewById(R.id.chat_imageview_id);
        mChatAddBtn = (ImageButton) findViewById(R.id.plus_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.send_message);
        mChatMessageView = (EmojiconEditText) findViewById(R.id.chat_text);
        mMessagesList = (RecyclerView) findViewById(R.id.messageList);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);
        mLinearLayout.setReverseLayout(true);




        mChatMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emoticons.setVisibility(View.GONE);
                recyclerViewAlbum.setVisibility(View.GONE);
                mChatMessageView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.smajlic, 0);
            }
        });

        mMessagesList.setLayoutManager(mLinearLayout);
        mAdapter = new MessageAdapter(messagesList, getApplicationContext(), this);

        loadMessages(this);
        checkAllLoaded();

        mChatMessageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (firstImageClick) {

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        firstImageClick = false;
                        secondImageClick = true;
                        if (event.getRawX() >= (mChatMessageView.getRight() - mChatMessageView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            // your action here
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(mChatMessageView.getWindowToken(), 0);
                            recyclerViewAlbum.setVisibility(View.GONE);
                            emoticons.setVisibility(View.VISIBLE);
                            mChatMessageView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.smajlicpopunjeni2, 0);
                            Toast.makeText(ChatMessageActivity.this, "clicked", Toast.LENGTH_LONG).show();
                            return true;
                        }
                    }

                } else if (secondImageClick) {

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        firstImageClick = true;
                        secondImageClick = false;

                        if (event.getRawX() >= (mChatMessageView.getRight() - mChatMessageView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            // your action here
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            emoticons.setVisibility(View.GONE);
                            recyclerViewAlbum.setVisibility(View.GONE);
                            mChatMessageView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.smajlic, 0);
                            Toast.makeText(ChatMessageActivity.this, "clicked", Toast.LENGTH_LONG).show();
                            return true;
                        }
                    }
                }


                return false;
            }
        });
        //neki kom
        mChatMessageView.addTextChangedListener(new TextWatcher() {

            /**
             * This notify that, within s,
             * the count characters beginning at start are about to be replaced by new text with length
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * This notify that, somewhere within s, the text has been changed.
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
            }

            /**
             * This notify that, within s, the count characters beginning at start have just
             * replaced old text that had length
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
        });

        setEmojiconFragment(false);
        mTitleView.setText(mChatUsername);
        mRootRef.collection("Users").document(mChatUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                String favoriteClubChat = dataSnapshot.getString("favoriteClub");

                mRootRef.collection("UsersChat").document(favoriteClubChat).collection("user-id").document(mChatUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e1) {

                        if (dataSnapshot.get("online") instanceof String) {
                            online = Boolean.parseBoolean(String.valueOf(dataSnapshot.getString("online")));
                        } else {
                            online = Boolean.parseBoolean(String.valueOf(dataSnapshot.getDate("online")));
                        }
                        String image = dataSnapshot.getString("profileImage");

                        if (online) {
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
                        chatAddMap.put("timestamp", FieldValue.serverTimestamp());

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

        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstImageClick){
                    firstImageClick = false;
                    secondImageClick = true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mChatMessageView.getWindowToken(), 0);
                    recyclerViewAlbum.setVisibility(View.VISIBLE);
                }else if (secondImageClick){

                    firstImageClick = true;
                    secondImageClick = false;
                    recyclerViewAlbum.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mChatMessageView.getWindowToken(), 0);
                }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (requestCode == RESULT_OK) {
                mChatUser = data.getStringExtra("userId");
            }
        }
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
                FirebaseAuth mAutH = FirebaseAuth.getInstance();


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
                    if (mRefreshLayout.isRefreshing()) {
                        mMessagesList.smoothScrollToPosition(getItemCount() - 6);

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

    private void checkAllLoaded() {

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
                            lastItem = querySnapshot2.getDocuments().get(querySnapshot2.size() - 1);
                            ;
                            if (lastkey.getId().equals(lastItem.getId())) {
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
                FirebaseAuth mAutH = FirebaseAuth.getInstance();


                String current_user_id = mAutH.getCurrentUser().getUid();


                String from_user = model.getFrom();
                String type = model.getType();
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

                            if (mRefreshLayout.isRefreshing() && totalItem <= (lastVisible + 10)) {
                                mMessagesList.smoothScrollToPosition(lastVisible + 15);
                            }
                        }
                    });
                    mRefreshLayout.setRefreshing(false);
                    // mMessagesList.smoothScrollToPosition(0);


                }

                if (type.equals("text")) {
                    holder.messagetTextTexview.setVisibility(View.VISIBLE);
                    holder.messagetTextTexview.setText(model.getMessage());
                    holder.messageImageView.setVisibility(View.GONE);
                } else if (type.equals("image")) {
                    holder.messageImageView.setVisibility(View.VISIBLE);
                    Glide.with(activity).load(model.getMessage()).into(holder.messageImageView);
                    holder.messagetTextTexview.setVisibility(View.GONE);
                }

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
                Log.i("newItem", String.valueOf(positionStart));
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

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //some code....
                break;
            case MotionEvent.ACTION_UP:
                v.performClick();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Set the Emoticons in Fragment.
     *
     * @param useSystemDefault
     */
    private void setEmojiconFragment(boolean useSystemDefault) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    /**
     * It called, when click on icon of Emoticons.
     *
     * @param emojicon
     */
    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        EmojiconsFragment.input(mChatMessageView, emojicon);
    }

    /**
     * It called, when backspace button of Emoticons pressed
     *
     * @param view
     */
    @Override
    public void onEmojiconBackspaceClicked(View view) {

        EmojiconsFragment.backspace(mChatMessageView);
    }

}
