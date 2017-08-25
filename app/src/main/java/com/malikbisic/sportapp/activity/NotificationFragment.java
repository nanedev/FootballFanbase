package com.malikbisic.sportapp.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.Date;
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
    static boolean isNotificationClicked = false;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

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
            protected void populateViewHolder(final NotificationViewHolder viewHolder, Notification model, int position) {
                final String post_key_notification = getRef(position).getKey();
                viewHolder.setUid(getContext(), model.getUid());
                viewHolder.setAction(model.getAction(), model.getWhatIS());

                viewHolder.itemview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final DatabaseReference getKey = notificationRef.child(post_key_notification);
                        getKey.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String key = String.valueOf(dataSnapshot.child("post_key").getValue());
                                String action = String.valueOf(dataSnapshot.child("action").getValue());
                                String whatIS = String.valueOf(dataSnapshot.child("whatIS").getValue());
                                if (whatIS.equals("post")) {
                                    Intent openSinglePost = new Intent(getContext(), SinglePostViewNotificationActivity.class);
                                    openSinglePost.putExtra("post_key", key);
                                    isNotificationClicked = true;
                                    startActivity(openSinglePost);
                                } else if (whatIS.equals("comment")){
                                    Intent openCom = new Intent(getContext(), CommentsActivity.class);
                                    openCom.putExtra("keyComment2", key);
                                    isNotificationClicked = true;
                                    openCom.putExtra("profileComment", MainPage.profielImage);
                                    openCom.putExtra("username", MainPage.usernameInfo);
                                    startActivity(openCom);
                                } else if (whatIS.equals("reply")) {
                                    Intent intent = new Intent(getContext(),CommentsInComments.class);
                                    intent.putExtra("keyComment3", key);
                                    isNotificationClicked = true;
                                    intent.putExtra("keyPost", CommentsActivity.key);
                                    intent.putExtra("profileComment", MainPage.profielImage);
                                    intent.putExtra("username", MainPage.usernameInfo);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(post_key_notification).child("uid").exists()) {

                            viewHolder.downArrowNotification.setVisibility(View.VISIBLE);

                            viewHolder.downArrowNotification.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String[] items = {"Delete notification", "Cancel"};

                                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(viewHolder.itemView.getContext());
                                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (items[which].equals("Delete notification")) {
                                                notificationRef.child(post_key_notification).removeValue();
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        notificationRecView.setAdapter(populateAdapter);


    }


    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        View itemview;
        TextView username;
        CircleImageView profileImage;
        TextView actionTxt;
        String usernameTxt;
        String actionString;
        ImageView downArrowNotification;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            itemview = itemView;

            username = (TextView) itemview.findViewById(R.id.notification_username);
            profileImage = (CircleImageView) itemview.findViewById(R.id.notification_profil_image);
            actionTxt = (TextView) itemview.findViewById(R.id.notification_action);
            downArrowNotification = (ImageView) itemView.findViewById(R.id.down_arrow_notification);
        }

        public void setUid(final Context ctx, String uid) {
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

        public void setAction(String action, String whatIS) {
            actionTxt.setText(action + " your " + whatIS + "!");
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.notification_menu, menu);
        MenuItem post = menu.findItem(R.id.postText);
        post.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        if (item.getItemId() == R.id.notification_clear_id) {
            notificationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String[] items = {"Delete all notification", "Cancel"};


                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (items[which].equals("Delete all notification")) {
                                    notificationRef.removeValue();

                                } else if (items[which].equals("Cancel")) {

                                }
                            }

                        });
                        dialog.create();
                        dialog.show();
                    }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        return super.onOptionsItemSelected(item);
    }
}
