package com.malikbisic.sportapp.activity.firebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.malikbisic.sportapp.listener.OnLoadMoreListener;
import com.malikbisic.sportapp.model.firebase.Message;
import com.malikbisic.sportapp.model.firebase.UserChat;
import com.malikbisic.sportapp.utils.GetTimeAgo;
import com.malikbisic.sportapp.adapter.firebase.MessageAdapter;
import com.malikbisic.sportapp.model.firebase.Messages;
import com.malikbisic.sportapp.utils.ImageAlbumName;
import com.malikbisic.sportapp.utils.RoundedTransformation;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
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

    ImageView mChatAddBtn;
    ImageButton mChatSendBtn;
    public static EmojiconEditText mChatMessageView;
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
    public static RecyclerView recyclerViewAlbum;


    ImageView proba;
    public static FrameLayout emoticons;
    RelativeLayout botomChatLay;
    boolean firstClickGallery = true;
    boolean secondClickGallery = false;
    boolean firstClickSmile = true;
    boolean secondClickSmile = false;
    boolean firstTimeEnterMessageClicked = true;
    boolean secondTimeEnterMessageClicked = false;
    ImageView smajlic;
    Animation slideUpAnimation;
    Animation slideDownAnimation;
    SwipeRefreshLayout layout;
    ImageButton captureImage;
    public static int CAMERA_REQUEST = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);
        smajlic = (ImageView) findViewById(R.id.smileImage);
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
        captureImage = (ImageButton) findViewById(R.id.cameraImage);

        mChatUser = getIntent().getStringExtra("userId");
        mChatUsername = getIntent().getStringExtra("username");
        recyclerViewAlbum = (RecyclerView) findViewById(R.id.album);
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anmation_drom_down_to_top);

        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_from_top_to_bottom);


        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
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
        mChatAddBtn = (ImageView) findViewById(R.id.plus_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.send_message);
        mChatMessageView = (EmojiconEditText) findViewById(R.id.chat_text);
        mMessagesList = (RecyclerView) findViewById(R.id.messageList);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mLinearLayout.setStackFromEnd(false);
        mMessagesList.setLayoutManager(mLinearLayout);
        mAdapter = new MessageAdapter(messagesList, getApplicationContext(), this, mMessagesList);
        mMessagesList.setAdapter(mAdapter);

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){


                handler.postDelayed(this, delay);
            }
        }, delay);
        loadMessages(ChatMessageActivity.this);
        checkAllLoaded();

        mRefreshLayout.setEnabled(false);



        smajlic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstClickSmile) {
                    firstClickSmile = false;
                    secondClickSmile = true;
                    firstClickGallery = true;
                    secondClickGallery = false;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mChatMessageView.getWindowToken(), 0);


                    emoticons.startAnimation(slideUpAnimation);
                    emoticons.setVisibility(View.VISIBLE);
                    recyclerViewAlbum.setVisibility(View.GONE);
                } else if (secondClickSmile) {
                    firstClickSmile = true;
                    secondClickSmile = false;
                    firstClickGallery = true;
                    secondClickGallery = false;
          /*  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mChatMessageView.getWindowToken(), 0);
                    emoticons.setVisibility(View.GONE);
                    recyclerViewAlbum.setVisibility(View.GONE);


                }
            }
        });
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstClickGallery) {
                    firstClickGallery = false;
                    secondClickGallery = true;
                    firstClickSmile = true;
                    secondClickSmile = false;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mChatMessageView.getWindowToken(), 0);
                    recyclerViewAlbum.setVisibility(View.VISIBLE);
                    emoticons.setVisibility(View.GONE);
                } else if (secondClickGallery) {

                    firstClickGallery = true;
                    secondClickGallery = false;
                    firstClickSmile = true;
                    secondClickSmile = false;
                    recyclerViewAlbum.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mChatMessageView.getWindowToken(), 0);
                    emoticons.setVisibility(View.GONE);
                }


            }
        });

        mChatMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emoticons.setVisibility(View.GONE);
                recyclerViewAlbum.setVisibility(View.GONE);
                firstClickSmile = true;
                firstClickGallery = true;

            }
        });




       /* mChatMessageView.setOnTouchListener(new View.OnTouchListener() {
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
        });*/
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


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshing = true;
                mCurrentPage = mCurrentPage + 10;
                scrollPosition += 10;


                itemPos = 0;
                 loadMoreMessages(ChatMessageActivity.this);

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

        if (requestCode == CAMERA_REQUEST) {
            Bitmap image = null;
            Uri imageData = null;
            if (resultCode == RESULT_OK) {
                image = (Bitmap) data.getExtras().get("data");
                imageData = data.getData();
                Intent openCameraSend = new Intent(ChatMessageActivity.this, CaptureImageSendChatActivity.class);
                openCameraSend.setData(imageData);
                openCameraSend.putExtra("imagedata", image);
                openCameraSend.putExtra("userID", mChatUser);
                startActivityForResult(openCameraSend, 1);
            }
        }
    }

    private void loadMoreMessages(final Activity activity) {

        final CollectionReference messageRef2 = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
        final com.google.firebase.firestore.Query messageQuery2 = messageRef2.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).startAfter(lastkey).limit(10);

        messageQuery2.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots.size() != 0) {
                    final DocumentSnapshot lastkey = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    CollectionReference messageRef = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
                    messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).addSnapshotListener(ChatMessageActivity.this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot querySnapshot2, FirebaseFirestoreException s) {
                            lastItem = querySnapshot2.getDocuments().get(querySnapshot2.size() - 1);
                            ;
                            if (lastkey.getId().equals(lastItem.getId())) {

                                if (lastkey.getId().equals(lastItem.getId())) {
                                    mAdapter.isFullLoaded(true);
                                } else {
                                    mAdapter.isFullLoaded(false);
                                }
                            }
                        }
                    });
                }
            }
        });

        final CollectionReference messageRef = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
        final com.google.firebase.firestore.Query messageQuery = messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).startAfter(lastkey).limit(10);
        messageQuery.get().addOnCompleteListener(ChatMessageActivity.this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentChange snap : task.getResult().getDocumentChanges()){
                    if (snap.getType() == DocumentChange.Type.ADDED){
                        Messages model = snap.getDocument().toObject(Messages.class);
                        messagesList.add(0, model);
                        mAdapter.notifyDataSetChanged();
                        lastkey = task.getResult().getDocuments().get(task.getResult().size() -1);
                        mAdapter.setIsLoading(false);
                    }
                }
            }
        });

/*
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


    }*/
    }

    private void checkAllLoaded() {

    }

    @Override
    protected void onStart() {
        super.onStart();

       /* mAdapter.setOnLoadMore(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {


                mMessagesList.post(new Runnable() {
                    @Override
                    public void run() {
                        messagesList.add(null);
                        mAdapter.notifyItemInserted(messagesList.size() - 1);
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadMoreMessages(ChatMessageActivity.this);


                    }
                }, 5000);

            }
        }); */

    }

    private void loadMessages(final Activity activity) {


        final CollectionReference messageRef = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
        final com.google.firebase.firestore.Query messageQuery = messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(400);

        final FirestoreRecyclerOptions<Messages> options = new FirestoreRecyclerOptions.Builder<Messages>()
                .setQuery(messageQuery, Messages.class).build();
        messageQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots.size() != 0) {
                   final DocumentSnapshot lastkey = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    CollectionReference messageRef = FirebaseFirestore.getInstance().collection("Messages").document(mCurrentUserId).collection("chat-user").document(mChatUser).collection("message");
                    messageRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).addSnapshotListener(ChatMessageActivity.this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot querySnapshot2, FirebaseFirestoreException s) {
                            lastItem = querySnapshot2.getDocuments().get(querySnapshot2.size() - 1);
                            ;
                            if (lastkey.getId().equals(lastItem.getId())) {

                                if (lastkey.getId().equals(lastItem.getId())) {
                                    mAdapter.isFullLoaded(true);
                                } else {
                                    mAdapter.isFullLoaded(false);
                                }
                            }
                        }
                    });
                }
            }
        });

        messageQuery.addSnapshotListener(ChatMessageActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                //messagesList.clear();
                for (DocumentChange snapshot : querySnapshot.getDocumentChanges()){
                    if (snapshot.getType() == DocumentChange.Type.ADDED) {
                        Messages model = snapshot.getDocument().toObject(Messages.class);
                        messagesList.add(model);

                    }
                }
                mAdapter.notifyDataSetChanged();
                lastkey = querySnapshot.getDocuments().get(querySnapshot.size() -1);



            }
        });

        /*
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
                        holder.layoutToUser.setVisibility(View.VISIBLE);
                        holder.layoutFromUser.setVisibility(View.GONE);
                        holder.layoutImageFromUser.setVisibility(View.GONE);

                        if (type.equals("text")) {
                            holder.layoutToUser.setVisibility(View.VISIBLE);
                            holder.layoutFromUser.setVisibility(View.GONE);
                            holder.layoutImageFromUser.setVisibility(View.GONE);
                            holder.layoutImageToUser.setVisibility(View.GONE);
                            holder.messageTextTOUser.setText(model.getMessage());
                            if (model.getTime() != null) {
                                String time = DateUtils.formatDateTime(ChatMessageActivity.this, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                                holder.timeTextViewToUser.setText(time);
                            }
                        } else if (type.equals("image")) {
                            holder.layoutImageToUser.setVisibility(View.VISIBLE);
                            holder.layoutFromUser.setVisibility(View.GONE);
                            holder.layoutImageFromUser.setVisibility(View.GONE);
                            holder.layoutToUser.setVisibility(View.GONE);
                            holder.userProfileForIMage.setVisibility(View.GONE);
                            Picasso.with(ChatMessageActivity.this).setIndicatorsEnabled(false);
                            Picasso.with(ChatMessageActivity.this).load(model.getMessage()).transform(new RoundedTransformation(20, 3)).fit().centerCrop().into(holder.messageImageViewToUser);

                            if (model.getTime() != null) {
                                String time = DateUtils.formatDateTime(ChatMessageActivity.this, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                                holder.timeImageTOUser.setText(time);
                            }
                        }

                    } else {
                        holder.layoutFromUser.setVisibility(View.VISIBLE);
                        holder.profileImageImg.setVisibility(View.VISIBLE);
                        holder.layoutToUser.setVisibility(View.GONE);
                        holder.layoutImageToUser.setVisibility(View.GONE);
                        holder.layoutImageFromUser.setVisibility(View.GONE);
                        holder.userProfileForIMage.setVisibility(View.GONE);

                        if (type.equals("text")) {
                            holder.layoutFromUser.setVisibility(View.VISIBLE);
                            holder.layoutToUser.setVisibility(View.GONE);
                            holder.layoutImageToUser.setVisibility(View.GONE);
                            holder.layoutImageFromUser.setVisibility(View.GONE);
                            holder.userProfileForIMage.setVisibility(View.GONE);
                            holder.messageTextFromUser.setText(model.getMessage());
                            if (model.getTime() != null) {
                                String time = DateUtils.formatDateTime(ChatMessageActivity.this, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                                holder.timeTextViewFromUser.setText(time);
                            }
                        } else if (type.equals("image")) {
                            holder.userProfileForIMage.setVisibility(View.VISIBLE);
                            holder.layoutImageFromUser.setVisibility(View.VISIBLE);
                            holder.layoutToUser.setVisibility(View.GONE);
                            holder.layoutImageToUser.setVisibility(View.GONE);
                            holder.layoutFromUser.setVisibility(View.GONE);
                            Picasso.with(ChatMessageActivity.this).setIndicatorsEnabled(false);
                            Picasso.with(ChatMessageActivity.this).load(model.getMessage()).transform(new RoundedTransformation(20, 3)).fit().centerCrop().into(holder.messageImageViewFromUser);

                            if (model.getTime() != null) {
                                String time = DateUtils.formatDateTime(ChatMessageActivity.this, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                                holder.timeImageFromUser.setText(time);
                            }

                            FirebaseFirestore displayImage = FirebaseFirestore.getInstance();

                            displayImage.collection("Users").document(from_user).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                    UserChat model2 = dataSnapshot.toObject(UserChat.class);
                                    String profileImage = model2.getProfileImage();

                                    holder.setProfileImageForImage(activity, profileImage);
                                }
                            });
                        }


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


                mMessagesList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emoticons.setVisibility(View.GONE);
                        recyclerViewAlbum.setVisibility(View.GONE);
                        Toast.makeText(ChatMessageActivity.this, "desavaliseista", Toast.LENGTH_LONG).show();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mChatMessageView.getWindowToken(), 0);

                    }
                });

                holder.messageImageViewFromUser.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String[] items = {"Save Image to Gallery", "Cancel"};
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity, R.style.AppTheme_Dark_Dialog);

                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (items[i].equals("Save Image to Gallery")) {

                                    ProgressDialog dialog1 = new ProgressDialog(activity);
                                    dialog1.setMessage("Saving...");
                                    dialog1.show();
                                    holder.messageImageViewFromUser.buildDrawingCache();
                                    Bitmap imageChat = holder.messageImageViewFromUser.getDrawingCache();
                                    saveFile(imageChat);
                                    dialog1.dismiss();
                                } else if (items[i].equals("Cancel")) {


                                }
                            }
                        });

                        dialog.create();
                        dialog.show();

                        return true;
                    }
                });

                holder.messageImageViewToUser.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String[] items = {"Save Image to Gallery", "Cancel"};
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity, R.style.AppTheme_Dark_Dialog);

                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (items[i].equals("Save Image to Gallery")) {

                                    ProgressDialog dialog1 = new ProgressDialog(activity);
                                    dialog1.setMessage("Saving...");
                                    dialog1.show();
                                    holder.messageImageViewToUser.buildDrawingCache();
                                    Bitmap imageChat = holder.messageImageViewToUser.getDrawingCache();
                                    saveFile(imageChat);
                                    dialog1.dismiss();
                                } else if (items[i].equals("Cancel")) {


                                }
                            }
                        });

                        dialog.create();
                        dialog.show();

                        return true;
                    }
                });
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

        adapter.startListening();*/
    }

    public void saveFile(Bitmap b) {
        try {

            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/FootballFanBase/");

            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = File.createTempFile(
                    String.valueOf(Calendar.getInstance().getTimeInMillis()),
                    ".jpeg",                     /* suffix */
                    storageDir                   /* directory */
            );


            FileOutputStream writeStream = new FileOutputStream(imageFile);

            b.compress(Bitmap.CompressFormat.JPEG, 100, writeStream);
            writeStream.flush();
            writeStream.close();

            addPicToGallery(imageFile);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void addPicToGallery(File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
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
