package com.malikbisic.sportapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.ClubNameChatAdapter;
import com.malikbisic.sportapp.model.UserChat;
import com.malikbisic.sportapp.model.UserChatGroup;
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Nane on 31.8.2017.
 */

public class FragmentChatUsers extends Fragment {

    RecyclerView userRecylerView;
    ClubNameChatAdapter adapter;
    List<UserChatGroup> clubName;
    DatabaseReference userReference;
    static String profileImage;
    static String username;
    static String flag;
    String clubNameLogo;
    static String userUID;
    static String date;
    boolean isOnline;
    static int numberOnline;
    public static String online;


    public void getClubName() {
        userReference = FirebaseDatabase.getInstance().getReference().child("UsersChat");

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clubName = new ArrayList<UserChatGroup>();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (!snapshot.getKey().toString().equals("null")) {
                        final String clubNameString = snapshot.getKey().toString();

                        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(clubNameString);
                        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final List<UserChat> userChats = new ArrayList<UserChat>();
                                numberOnline = 0;
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                    clubNameLogo = String.valueOf(snapshot1.child("favoriteClubLogo").getValue());
                                    isOnline = Boolean.parseBoolean(String.valueOf(snapshot1.child("online").getValue()));
                                    userUID = String.valueOf(snapshot1.child("userID").getValue());
                                    date = String.valueOf(snapshot1.child("date").getValue());

                                    DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                                    userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            username = String.valueOf(dataSnapshot.child("username").getValue());
                                            profileImage = String.valueOf(dataSnapshot.child("profileImage").getValue());
                                            flag = String.valueOf(dataSnapshot.child("flag").getValue());

                                            userChats.add(new UserChat(username, flag, profileImage, userUID, date, isOnline));
                                            Collections.sort(userChats, new CheckOnline());

                                    }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                            if (isOnline) {
                                                numberOnline++;
                                            }

                                            online = String.valueOf(FragmentChatUsers.numberOnline);






                                    //  userChats.add(new UserChat(FragmentChatUsers.username, FragmentChatUsers.flag, FragmentChatUsers.profileImage, FragmentChatUsers.userUID, FragmentChatUsers.date));

                                }

                                clubName.add(new UserChatGroup(clubNameString, userChats, clubNameLogo, numberOnline));


                                Collections.sort(clubName, new OnlineNumber());


                                adapter = new ClubNameChatAdapter(clubName, getContext());
                                userRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                userRecylerView.setAdapter(adapter);

                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_users, container, false);

        userRecylerView = (RecyclerView) view.findViewById(R.id.chatUserRecView);
        getClubName();

        return view;
    }

    class OnlineNumber implements Comparator<UserChatGroup>{

        @Override
        public int compare(UserChatGroup e1, UserChatGroup e2) {
            if(e1.getNumberOnline() < e2.getNumberOnline()){
                return 1;
            } else {
                return -1;
            }
        }
    }

    class CheckOnline implements Comparator<UserChat>{

        @Override
        public int compare(UserChat e1, UserChat e2) {
            if(e1.isIsonline()){
                return 1;
            } else {
                return -1;
            }
        }
    }
}
