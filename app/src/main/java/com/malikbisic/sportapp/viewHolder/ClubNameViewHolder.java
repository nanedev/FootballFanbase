package com.malikbisic.sportapp.viewHolder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.FragmentChatUsers;
import com.malikbisic.sportapp.adapter.ClubNameChatAdapter;
import com.malikbisic.sportapp.model.UserChat;
import com.malikbisic.sportapp.model.UserChatGroup;
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
    TextView onlineTexview;
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
        if (club instanceof UserChatGroup) {
            clubName2.setText(club.getTitle());

            Picasso.with(itemView.getContext()).load(((UserChatGroup) club).getClubLogo()).into(clubLogoImg);
           // onlineTexview.setText(((UserChatGroup) club).getOnline());
        }

    }

    public void setNumberOnline(final String title){
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UsersChat");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    final String clubNameString = snapshot.getKey().toString();

                    final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(title);
                    chatReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final List<UserChat> userChats = new ArrayList<UserChat>();
                            numberOnline = 0;
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {


                              String  isOnline =  snapshot1.child("online").getValue().toString();

                                if (isOnline.equals("true")) {
                                    numberOnline++;
                                }

                                online = String.valueOf(numberOnline);
                            }
                                onlineTexview.setText(online);



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
