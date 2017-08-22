package com.malikbisic.sportapp.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.Notification;
import com.malikbisic.sportapp.model.UsersModel;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    DatabaseReference notificationRef;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    RecyclerView notificationRecView;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        auth = FirebaseAuth.getInstance();
        notificationRecView = (RecyclerView) view.findViewById(R.id.notification_recView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        notificationRecView.setLayoutManager(layoutManager);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification").child(uid);
        final DatabaseReference setSeen = notificationRef;


            setSeen.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        snapshot.child("seen").getRef().setValue(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        auth.addAuthStateListener(authStateListener);

        FirebaseRecyclerAdapter<Notification, NotificationViewHolder> populateAdapter = new FirebaseRecyclerAdapter<Notification, NotificationViewHolder>(
                Notification.class,
                R.layout.notification_row,
                NotificationViewHolder.class,
                notificationRef
        ) {
            @Override
            protected void populateViewHolder(NotificationViewHolder viewHolder, Notification model, int position) {

                viewHolder.setUid(getContext(), model.getUid());
                viewHolder.setAction(model.getAction());
            }
        };

        notificationRecView.setAdapter(populateAdapter);


    }


    public static class NotificationViewHolder extends RecyclerView.ViewHolder{

        View itemview;
        TextView username;
        CircleImageView profileImage;
        TextView actionTxt;
        String usernameTxt;
        String actionString;
        public NotificationViewHolder(View itemView) {
            super(itemView);
            itemview = itemView;

            username = (TextView) itemview.findViewById(R.id.notification_username);
            profileImage = (CircleImageView) itemview.findViewById(R.id.notification_profil_image);
            actionTxt = (TextView) itemview.findViewById(R.id.notification_action);
        }

        public void setUid(final Context ctx, String uid){
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();

                    UsersModel model = dataSnapshot.getValue(UsersModel.class);
                    usernameTxt = model.getUsername(); //String.valueOf(value.get("username"));
                    String profileIMG = model.getProfileImage();

                    username.setText(usernameTxt);

                    Picasso.with(ctx).load(profileIMG).into(profileImage);




                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setAction (String action){
            actionTxt.setText(action);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().finish();
    }
}
