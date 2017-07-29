package com.malikbisic.sportapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference setCommentRef, getCommentRef;
    ImageButton sendComment;
    EditText writeComment;
    RecyclerView comments;

    Intent myIntent;

    String key;
    String profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        myIntent = getIntent();
        key = myIntent.getStringExtra("keyComment");
        profileImage = myIntent.getStringExtra("profileComment");

        getCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(key);
        Log.i("key", key);


        sendComment = (ImageButton) findViewById(R.id.sendComment);
        writeComment = (EditText) findViewById(R.id.writeComment);
        comments = (RecyclerView) findViewById(R.id.rec_view_comments);

        comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        comments.setLayoutManager(linearLayoutManager);

        sendComment.setOnClickListener(this);
        FirebaseRecyclerAdapter<Comments, CommentsActivity.CommentsViewHolder> populateComment = new FirebaseRecyclerAdapter<Comments, CommentsActivity.CommentsViewHolder>(
                Comments.class,
                R.layout.comments_wall,
                CommentsViewHolder.class,
                getCommentRef) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comments model, int position) {

                viewHolder.setTextComment(model.getTextComment());
                viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
            }
        };
        comments.setAdapter(populateComment);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendComment){
            String textComment = writeComment.getText().toString().trim();
            setCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(key).push();
           DatabaseReference post_comment = setCommentRef ;
            post_comment.child("textComment").setValue(textComment);
            post_comment.child("profileImage").setValue(profileImage);

            writeComment.setText("");
            hideSoftKeyboard(CommentsActivity.this);

        }
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ImageView profileImageImg;
        TextView commentsText;
        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            profileImageImg = (ImageView) mView.findViewById(R.id.profileComment);
            commentsText = (TextView) mView.findViewById(R.id.textComment);
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
    }
}
