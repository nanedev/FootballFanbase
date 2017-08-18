package com.malikbisic.sportapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CommentsInComments extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference setCommentRef, getCommentRef;
    ImageButton sendComment;
    EditText writeComment;
    RecyclerView commentsInComments;
    FirebaseAuth auth;
    Intent myIntent;

    String key;
    String profileImage;
    String username;
    DatabaseReference profileUsers;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_in_comments);
        myIntent = getIntent();
        auth = FirebaseAuth.getInstance();
        sendComment = (ImageButton) findViewById(R.id.sendCommentInComments);
        key = myIntent.getStringExtra("keyComment");
        getCommentRef = FirebaseDatabase.getInstance().getReference().child("CommentsInComments").child(key);
        writeComment = (EditText) findViewById(R.id.writeCommentInComments);
        commentsInComments = (RecyclerView) findViewById(R.id.rec_view_comments);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };


        commentsInComments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        commentsInComments.setLayoutManager(linearLayoutManager);
        sendComment.setOnClickListener(this);

        FirebaseRecyclerAdapter<CommentsInCommentsModel, CommentsInComments.CommentsInCommentsViewHolder> populate = new FirebaseRecyclerAdapter<CommentsInCommentsModel, CommentsInCommentsViewHolder>(
                CommentsInCommentsModel.class,
                R.layout.comments_in_comments_row,
                CommentsInCommentsViewHolder.class,
                getCommentRef
        ) {


            @Override
            protected void populateViewHolder(CommentsInCommentsViewHolder viewHolder, CommentsInCommentsModel model, int position) {

                viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setTextComment(model.getTextComment());
            }


        };




    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendCommentInComments) {
            String textComment = writeComment.getText().toString().trim();
            setCommentRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference post_comment = setCommentRef.child("CommentsInComments").child(key).push();
            post_comment.child("textComment").setValue(textComment);
            post_comment.child("profileImage").setValue(profileImage);
            post_comment.child("username").setValue(username);
            post_comment.child("uid").setValue(auth.getCurrentUser().getUid());

            writeComment.setText("");
            hideSoftKeyboard(CommentsInComments.this);
        }
    }

    private void hideSoftKeyboard(CommentsInComments commentsInComments) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) commentsInComments.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                commentsInComments.getCurrentFocus().getWindowToken(), 0);
    }

    public static class CommentsInCommentsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView profileImageImg;
        TextView commentsText;
        ImageView downArrow;
        TextView usernameTxt;
        FirebaseDatabase database;
        FirebaseAuth mAuth;

        public CommentsInCommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            profileImageImg = (ImageView) mView.findViewById(R.id.profileCommentInComment);
            commentsText = (TextView) mView.findViewById(R.id.textCommentInComments);
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


}
