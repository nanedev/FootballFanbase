package com.malikbisic.sportapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> populateComment = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(
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

    //nees

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendComment){
            String textComment = writeComment.getText().toString().trim();

           DatabaseReference post_comment = setCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(key).push();
            post_comment.child("textComment").setValue(textComment);
            post_comment.child("profileImage").setValue(profileImage);
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
            if (textComment != null) {
                commentsText.setText(textComment);
                Log.i("comment", textComment);
            }
        }

        public void setProfileImage(Context ctx, String profileImage) {
            if (profileImage != null) {
                Picasso.with(ctx).load(profileImage).into(profileImageImg);
                Log.i("prp", profileImage);
            }
        }
    }
}
