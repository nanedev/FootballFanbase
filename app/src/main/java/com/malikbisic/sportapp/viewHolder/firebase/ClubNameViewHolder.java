package com.malikbisic.sportapp.viewHolder.firebase;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.firebase.UserChat;
import com.malikbisic.sportapp.model.firebase.UserChatGroup;
import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 03/09/2017.
 */

public class ClubNameViewHolder extends GroupViewHolder {

    View view;
    TextView clubName2;
    CircleImageView clubLogoImg;
    Activity activity;
    public TextView onlineTexview;
    FirebaseDatabase mDatabase;
    int numberOnline;
    String online;
    boolean isOnline;

    public ClubNameViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        clubName2 = (TextView) view.findViewById(R.id.clubName_Users);
        clubLogoImg = (CircleImageView) view.findViewById(R.id.parent_club_logo);
        onlineTexview = (TextView) view.findViewById(R.id.parent_online_text);
    }

    public void setClubTitle(ExpandableGroup club) {
        if (club.getTitle() != null) {
            if (club instanceof UserChatGroup) {
                clubName2.setText(club.getTitle());

                Picasso.with(itemView.getContext()).load(((UserChatGroup) club).getClubLogo()).into(clubLogoImg);
                // onlineTexview.setText(((UserChatGroup) club).getOnline());

                Log.i(club.getTitle(), String.valueOf(((UserChatGroup) club).getNumberOnline()));


            }
        }
    }

    public void setNumberOnline(final String title,Activity activity) {
        final Query userReference = FirebaseFirestore.getInstance().collection("UsersChat").document(title).collection("user-id").whereEqualTo("online","true");

        userReference.addSnapshotListener(activity,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    numberOnline = documentSnapshots.size();

                    if (numberOnline == 0) {
                        onlineTexview.setText("");
                    } else {
                        onlineTexview.setText(String.valueOf(numberOnline));
                        view.setVisibility(View.VISIBLE);
                    }


                }
            }
        });

/*
        userReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {
                for (final DocumentSnapshot snapshot : dataSnapshot.getDocuments()) {


                    final String clubNameString = snapshot.getId();

                    final DocumentReference chatReference = FirebaseFirestore.getInstance().collection("UsersChat").document(title);
                    chatReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e1) {
                            final List<UserChat> userChats = new ArrayList<UserChat>();
                            numberOnline = 0;
                            String isOnline = "";

                            if (dataSnapshot.contains("online"))

                                isOnline = dataSnapshot.getString("online");

                            if (isOnline.equals("true")) {
                                numberOnline++;
                            }

                            online = String.valueOf(numberOnline);

                            onlineTexview.setText(online);


                            if (numberOnline == 0) {
                                view.setVisibility(View.GONE);
                            } else {
                                view.setVisibility(View.VISIBLE);
                            }
                        }
                    });


                }

            }
        });*/
    }
}
