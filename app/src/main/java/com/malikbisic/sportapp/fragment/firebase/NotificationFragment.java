package com.malikbisic.sportapp.fragment.firebase;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.utils.GetTimeAgo;
import com.malikbisic.sportapp.activity.firebase.SinglePostViewNotificationActivity;
import com.malikbisic.sportapp.activity.firebase.CommentsActivity;
import com.malikbisic.sportapp.activity.firebase.CommentsInComments;
import com.malikbisic.sportapp.activity.firebase.MainPage;
import com.malikbisic.sportapp.model.firebase.Notification;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    FirebaseFirestore notificationRef;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    RecyclerView notificationRecView;
    public static boolean isNotificationClicked = false;
    Query query;
    String uid;

    ArrayList<String> listNotfikey;

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
        listNotfikey = new ArrayList<>();

        FirebaseUser user = auth.getCurrentUser();
        uid = user.getUid();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -30);
        Date d = c.getTime();

        Log.i("30 days", String.valueOf(d));
        long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS);
        notificationRef = FirebaseFirestore.getInstance();
        query = notificationRef.collection("Notification").document(uid).collection("notif-id").orderBy("timestamp", Query.Direction.ASCENDING).startAt(d);
        deleteExpiredNotification();

        return view;
    }

    public void deleteExpiredNotification(){

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -30);
        Date d = c.getTime();
        Query notif = FirebaseFirestore.getInstance().collection("Notification").document(uid).collection("notif-id").orderBy("timestamp", Query.Direction.ASCENDING).endBefore(d);
        notif.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult()){
                    String id = snapshot.getId();

                    DocumentReference doc = FirebaseFirestore.getInstance().collection("Notification").document(uid).collection("notif-id").document(id);
                    doc.delete();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        auth.addAuthStateListener(authStateListener);

        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .build();

        FirestoreRecyclerAdapter<Notification, NotificationViewHolder> populateAdapter = new FirestoreRecyclerAdapter<Notification, NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final NotificationViewHolder viewHolder, int position, Notification model) {

                final String post_key_notification = getSnapshots().getSnapshot(position).getId();
                viewHolder.setUid(getContext(), model.getUid());
                viewHolder.setAction(model.getAction(), model.getWhatIS());
                viewHolder.setTimeAgo(model.getTimestamp(), getContext());
                viewHolder.isSeen(model.isSeen(), getActivity());

                listNotfikey.add(post_key_notification);

                viewHolder.itemview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final DocumentReference getKey = notificationRef.collection("Notification").document(auth.getCurrentUser().getUid()).collection("notif-id").document(post_key_notification);
                        getKey.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                                String key = String.valueOf(dataSnapshot.getString("post_key"));
                                String action = String.valueOf(dataSnapshot.getString("action"));
                                String whatIS = String.valueOf(dataSnapshot.getString("whatIS"));
                                if (whatIS.equals("post")) {
                                    Intent openSinglePost = new Intent(getContext(), SinglePostViewNotificationActivity.class);
                                    openSinglePost.putExtra("post_key", key);
                                    isNotificationClicked = true;

                                    DocumentReference docDelete = FirebaseFirestore.getInstance().collection("Notification")
                                            .document(uid).collection("notif-id").document(post_key_notification);
                                    docDelete.update("seen", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {

                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("errorSetSeenNotif", e.getLocalizedMessage());
                                        }
                                    });

                                    startActivity(openSinglePost);
                                } else if (whatIS.equals("comment")) {
                                    Intent openCom = new Intent(getContext(), CommentsActivity.class);
                                    openCom.putExtra("keyComment2", key);
                                    isNotificationClicked = true;
                                    Log.i("KeyNOTIF", key);
                                    openCom.putExtra("profileComment", MainPage.profielImage);
                                    openCom.putExtra("username", MainPage.usernameInfo);

                                    DocumentReference docDelete = FirebaseFirestore.getInstance().collection("Notification")
                                            .document(uid).collection("notif-id").document(post_key_notification);
                                    docDelete.update("seen", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {

                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("errorSetSeenNotif", e.getLocalizedMessage());
                                        }
                                    });

                                    startActivity(openCom);
                                } else if (whatIS.equals("reply")) {
                                    Intent intent = new Intent(getContext(), CommentsInComments.class);
                                    intent.putExtra("keyComment3", key);
                                    isNotificationClicked = true;
                                    intent.putExtra("keyPost", CommentsActivity.key);
                                    intent.putExtra("profileComment", MainPage.profielImage);
                                    intent.putExtra("username", MainPage.usernameInfo);

                                    DocumentReference docDelete = FirebaseFirestore.getInstance().collection("Notification")
                                            .document(uid).collection("notif-id").document(post_key_notification);
                                    docDelete.update("seen", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {

                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("errorSetSeenNotif", e.getLocalizedMessage());
                                        }
                                    });

                                    startActivity(intent);
                                }
                            }

                        });

                    }
                });

                notificationRef.collection("Notification").document(auth.getCurrentUser().getUid()).collection("notif-id").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                            if (snapshot.exists()) {

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
                                                    notificationRef.collection("Notification").document(auth.getCurrentUser().getUid()).collection("notif-id").document(post_key_notification).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_LONG).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("errorDeleteNotif", e.getLocalizedMessage());
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

            @Override
            public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, parent, false);
                return new NotificationViewHolder(view);
            }
        };

        notificationRecView.setAdapter(populateAdapter);
        populateAdapter.notifyDataSetChanged();
        populateAdapter.startListening();


    }


    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        View itemview;
        TextView username;
        CircleImageView profileImage;
        TextView actionTxt;
        String usernameTxt;
        String actionString;
        ImageView downArrowNotification;
        TextView timeAgoTextView;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            itemview = itemView;

            username = (TextView) itemview.findViewById(R.id.notification_username);
            profileImage = (CircleImageView) itemview.findViewById(R.id.notification_profil_image);
            actionTxt = (TextView) itemview.findViewById(R.id.notification_action);
            downArrowNotification = (ImageView) itemView.findViewById(R.id.down_arrow_notification);
            timeAgoTextView = (TextView) itemView.findViewById(R.id.setTimeAgo);
        }

        public void setUid(final Context ctx, String uid) {
            FirebaseFirestore userFirestore = FirebaseFirestore.getInstance();
            DocumentReference usersRef = userFirestore.collection("Users").document(uid);
            usersRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                    UsersModel model = dataSnapshot.toObject(UsersModel.class);
                    usernameTxt = model.getUsername(); //String.valueOf(value.get("username"));
                    String profileIMG = model.getProfileImage();

                    username.setText(usernameTxt);

                    Picasso.with(ctx).load(profileIMG).into(profileImage);


                }

            });
        }

        public void setAction(String action, String whatIS) {
            actionTxt.setText(action + " your " + whatIS + "!");
        }

        public void setTimeAgo(Date timestamp, Context ctx) {
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            //Date time = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault()).parse(str_date);
            long lastTime = timestamp.getTime();
            String lastStringTime = getTimeAgo.getTimeAgo(lastTime, ctx);
            timeAgoTextView.setText(lastStringTime);
        }

        public void isSeen(boolean isSeen, Activity activity) {
            if (isSeen) {
                itemview.setBackgroundColor(activity.getResources().getColor(R.color.white));
            } else {
                itemview.setBackgroundColor(activity.getResources().getColor(R.color.notifnotSeen));
            }
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

            final String[] items = {"Mark all as read", "Cancel"};
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity(),R.style.AppTheme_Dark_Dialog);
            dialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (items[which].equals("Mark all as read")) {


                        CollectionReference notif = FirebaseFirestore.getInstance().collection("Notification").document(uid).collection("notif-id");
                        notif.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    String docID = snapshot.getId();

                                    DocumentReference docDelete = FirebaseFirestore.getInstance().collection("Notification")
                                            .document(uid).collection("notif-id").document(docID);
                                    docDelete.update("seen", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {

                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("errorSetSeenAllNotif", e.getLocalizedMessage());
                                        }
                                    });
                                }

                            }
                        });

                    } else if (items[which].equals("Cancel")) {

                    }
                }

            });
            dialog.create();
            dialog.show();


        }

        return super.onOptionsItemSelected(item);
    }


}
