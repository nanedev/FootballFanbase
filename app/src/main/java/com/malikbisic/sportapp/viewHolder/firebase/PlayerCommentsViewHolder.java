package com.malikbisic.sportapp.viewHolder.firebase;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.malikbisic.sportapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by malikbisic on 17/01/2018.
 */

public class PlayerCommentsViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profileImage;
    TextView usernameProfile;
    TextView textComments;

    public PlayerCommentsViewHolder(View itemView) {
        super(itemView);
        profileImage = (CircleImageView) itemView.findViewById(R.id.profileComment_playerCom);
        usernameProfile = (TextView) itemView.findViewById(R.id.username_comment_profile_playerCom);
        textComments = (TextView) itemView.findViewById(R.id.textComment_playerCom);
    }

    public void setTextComments(String text){
        textComments.setText(text);
    }

    public void profileSet(String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String user = task.getResult().getString("username");
                String profileImg = task.getResult().getString("profileImage");

                Glide.with(profileImage.getContext()).load(profileImg).into(profileImage);
                usernameProfile.setText(user);
            }
        });
    }
}
