package com.malikbisic.sportapp.activity.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.fragment.firebase.NotificationFragment;
import com.malikbisic.sportapp.fragment.firebase.ProfileFragment;
import com.malikbisic.sportapp.model.firebase.CommentsInCommentsModel;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CommentsInComments extends AppCompatActivity implements View.OnClickListener,TextWatcher,EmojiconsFragment.OnEmojiconBackspaceClickedListener,EmojiconGridFragment.OnEmojiconClickedListener {
    FirebaseFirestore mReference;
    DocumentReference setCommentRef;
    CollectionReference getCommentRef;
    CollectionReference postingDatabase, notificationReference;
    ImageView sendComment;
    EmojiconEditText writeCommentInComment;
    RecyclerView commentsInComments;
    FirebaseAuth auth;
    Intent myIntent;
    boolean isSystemComment;
    String key;
    String keyNotif;
    String keyPost;
    String keyNotifPush;
    String profileImage;
    String username;
    FirebaseFirestore profileUsers;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    RelativeLayout sendCommentInComments;
    ImageButton smajliBtn;
    boolean firstClickSmile = true;
    boolean secondClickSmile = false;
    Animation slideUpAnimation;
    FrameLayout emoticonsCommentInComments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_in_comments);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        myIntent = getIntent();
        auth = FirebaseAuth.getInstance();
        mReference = FirebaseFirestore.getInstance();
        sendComment = (ImageView) findViewById(R.id.sendCommentInCommentBtn);
        key = myIntent.getStringExtra("keyComment");
        keyNotif = myIntent.getStringExtra("keyComment3");
        keyNotifPush = myIntent.getStringExtra("post_key");
        keyPost = myIntent.getStringExtra("keyPost");
        profileImage = myIntent.getStringExtra("profileComment");
        username = myIntent.getStringExtra("username");
        profileUsers = FirebaseFirestore.getInstance();
        isSystemComment = myIntent.getBooleanExtra("isCommentSystem", false);
        if (key == null && keyNotifPush != null ){
            key = keyNotifPush;
        } else if (key == null && keyNotif != null && keyNotifPush == null){
            key = keyNotif;
        }


        Log.i("commentsIn", "comments");
        if (!NotificationFragment.isNotificationClicked) {
            getCommentRef = mReference.collection("CommentsInComments").document(key).collection("reply-id");
        } else  {
            getCommentRef = mReference.collection("CommentsInComments").document(keyNotif).collection("reply-id");
            NotificationFragment.isNotificationClicked = false;
        }
        postingDatabase = mReference.collection("Posting");
        notificationReference = mReference.collection("Notification");
        writeCommentInComment = (EmojiconEditText) findViewById(R.id.writeCommentInComments);
        commentsInComments = (RecyclerView) findViewById(R.id.rec_view_comments_in_comments);
smajliBtn = (ImageButton) findViewById(R.id.smileImageCommentsInComments);
emoticonsCommentInComments = (FrameLayout) findViewById(R.id.emojiconsCommentsinComments);
        sendCommentInComments = (RelativeLayout) findViewById(R.id.sendCommentInComments);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        sendCommentInComments.setOnClickListener(this);
        smajliBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstClickSmile) {
                    firstClickSmile = false;
                    secondClickSmile = true;

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    emoticonsCommentInComments.startAnimation(slideUpAnimation);
                    emoticonsCommentInComments.setVisibility(View.VISIBLE);


                } else if (secondClickSmile) {
                    firstClickSmile = true;
                    secondClickSmile = false;

                    writeCommentInComment.clearFocus();
          /*  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
                    emoticonsCommentInComments.setVisibility(View.GONE);


                }
            }
        });
        writeCommentInComment.addTextChangedListener(this);
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anmation_drom_down_to_top);
        writeCommentInComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeCommentInComment.setFocusable(true);
                emoticonsCommentInComments.setVisibility(View.GONE);

                firstClickSmile = true;

            }
        });

        writeCommentInComment.clearFocus();
        writeCommentInComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    emoticonsCommentInComments.setVisibility(View.GONE);

                    firstClickSmile = true;


                }

            }
        });
        setEmojiconFragment(false);

        final Query query = getCommentRef.orderBy("timestamp", Query.Direction.ASCENDING);

        commentsInComments.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentsInComments.setLayoutManager(linearLayoutManager);
     sendComment.setRotation(300);

        final FirestoreRecyclerOptions<CommentsInCommentsModel> response = new FirestoreRecyclerOptions.Builder<CommentsInCommentsModel>()
                .setQuery(query, CommentsInCommentsModel.class)
                .build();


        final FirestoreRecyclerAdapter populate = new FirestoreRecyclerAdapter<CommentsInCommentsModel, CommentsInCommentsViewHolder>(response) {
            @Override
            public CommentsInCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_in_comments_row, parent, false);
                return new CommentsInCommentsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final CommentsInCommentsViewHolder viewHolder, int position, CommentsInCommentsModel model) {
                final String post_key_comments = getSnapshots().getSnapshot(position).getId();
                viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setTextComment(model.getTextComment());

                viewHolder.profileImageImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.usernameTxt.getText().toString().trim();
                        Log.i("username", username);

                        profileUsers.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {

                                for (DocumentSnapshot dataSnapshot1 : dataSnapshot.getDocuments()) {

                                    final UsersModel userInfo = dataSnapshot1.toObject(UsersModel.class);

                                    String usernameFirebase = userInfo.getUsername();

                                    if (username.equals(usernameFirebase)) {
                                        final String uid = userInfo.getUserID();
                                        FirebaseUser user1 = auth.getCurrentUser();
                                        String myUID = user1.getUid();
                                        Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                        if (uid.equals(myUID)) {

                                             /* ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true"); */

                                            Intent intent = new Intent(CommentsInComments.this, ProfileFragment.class);
                                            startActivity(intent);

                                        } else {

                                            DocumentReference profileInfo = profileUsers.collection("Users").document(uid);

                                            profileInfo.addSnapshotListener(CommentsInComments.this,new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                                                    Intent openUserProfile = new Intent(CommentsInComments.this, UserProfileActivity.class);
                                                    openUserProfile.putExtra("userID", uid);
                                                    startActivity(openUserProfile);
                                                }

                                            });
                                        }
                                    }
                                }
                            }
                        });
                    }
                });

                final FirebaseFirestore commentsUsers = FirebaseFirestore.getInstance();
                commentsUsers.collection("CommentsInComments").document(key).collection("reply-id").document(post_key_comments).addSnapshotListener(CommentsInComments.this,new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot querySnapshot, FirebaseFirestoreException e) {


                        if (querySnapshot.exists()) {
                            if (auth.getCurrentUser().getUid().equals(querySnapshot.getString("uid"))) {
                                viewHolder.downArrow.setVisibility(View.VISIBLE);

                                viewHolder.downArrow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String[] items = {"Delete comment", "Cancel"};

                                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(viewHolder.mView.getContext());
                                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (items[which].equals("Delete comment")) {
                                                    commentsUsers.collection("CommentsInComments").document(key).collection("reply-id").document(post_key_comments).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("deleteReply", e.getLocalizedMessage());
                                                        }
                                                    });
                                                } else if (items[which].equals("Cancel")) {

                                                }
                                            }

                                        });
                                        dialog.create();
                                        dialog.show();
                                    }
                                });

                            }
                        }
                    }
                });

            }

        };
        populate.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = populate.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.

                if (lastVisiblePosition == 0 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    commentsInComments.scrollToPosition(positionStart);
                }
            }
        });


        commentsInComments.setAdapter(populate);
        populate.notifyDataSetChanged();
        populate.startListening();


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendCommentInComments) {

        }
    }


    private void hideSoftKeyboard(CommentsInComments commentsInComments) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) commentsInComments.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                commentsInComments.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!writeCommentInComment.getText().toString().trim().isEmpty() && writeCommentInComment.getText().toString().trim().length() >= 1) {
            sendComment.setRotation(0);
            sendCommentInComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textComment = writeCommentInComment.getText().toString().trim();

                    CollectionReference post_comment = FirebaseFirestore.getInstance().collection("CommentsInComments").document(key).collection("reply-id");
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("textComment", textComment);
                    commentsMap.put("profileImage", profileImage);
                    commentsMap.put("username", username);
                    commentsMap.put("uid", auth.getCurrentUser().getUid());
                    commentsMap.put("timestamp",FieldValue.serverTimestamp());
                    post_comment.add(commentsMap);

                    FirebaseFirestore getIduserpost = FirebaseFirestore.getInstance();
                    getIduserpost.collection("Comments").document(keyPost).collection("comment-id").document(key).addSnapshotListener(CommentsInComments.this,new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                            if (dataSnapshot.exists()) {
                                String userpostUID = dataSnapshot.getString("uid");


                                Map<String, Object> notifMap = new HashMap<>();
                                notifMap.put("action", "coment");
                                notifMap.put("uid", auth.getCurrentUser().getUid());
                                notifMap.put("seen", false);
                                notifMap.put("whatIS", "reply");
                                notifMap.put("timestamp", FieldValue.serverTimestamp());
                                notifMap.put("post_key", key);

                                if (!userpostUID.equals(auth.getCurrentUser().getUid())) {
                                    CollectionReference notifSet = FirebaseFirestore.getInstance().collection("Notification").document(userpostUID).collection("notif-id");
                                    notifSet.add(notifMap);
                                }
                            }

                            if (e != null) {
                                Log.e("likeERROR", e.getLocalizedMessage());
                            }
                        }
                    });
                    writeCommentInComment.setText("");
                    hideSoftKeyboard(CommentsInComments.this);
                }


            });


        } else if (writeCommentInComment.getText().toString().trim().length() < 1) {
            sendComment.setRotation(300);
            sendCommentInComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(CommentsInComments.this, "Can't send empty comment", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(writeCommentInComment, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(writeCommentInComment);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojiconsCommentsinComments, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }


    public static class CommentsInCommentsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView profileImageImg;
        EmojiconTextView commentsText;
        ImageView downArrow;
        TextView usernameTxt;
        FirebaseDatabase database;
        FirebaseAuth mAuth;

        public CommentsInCommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            profileImageImg = (ImageView) mView.findViewById(R.id.profileCommentInComment);
            commentsText = (EmojiconTextView) mView.findViewById(R.id.textCommentInComments);
            downArrow = (ImageView) mView.findViewById(R.id.down_arrow_comments_in_Comments);
            usernameTxt = (TextView) mView.findViewById(R.id.username_comment_profile_in_comment);

            database = FirebaseDatabase.getInstance();


            mAuth = FirebaseAuth.getInstance();

        }

        public void setTextComment(String textComment) {
            commentsText.setText(textComment);
            commentsText.setTextColor(Color.parseColor("#000000"));
            Log.i("comment", textComment);

        }

        public void setProfileImage(Context ctx, String profileImage) {

            Picasso.with(ctx).load(profileImage).into(profileImageImg);
            // Log.i("prp", profileImage);

        }

        public void setUsername(String username) {

            if (username != null) {
                usernameTxt.setText(username);
            } else {
                usernameTxt.setText("");
            }

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backToComments = new Intent(CommentsInComments.this, CommentsActivity.class);
        backToComments.putExtra("keyComment", keyPost);
        startActivity(backToComments);
    }
}
