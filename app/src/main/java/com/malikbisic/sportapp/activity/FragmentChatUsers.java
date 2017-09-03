package com.malikbisic.sportapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.ClubNameChatAdapter;
import com.malikbisic.sportapp.model.UserChat;
import com.malikbisic.sportapp.model.UserChatGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nane on 31.8.2017.
 */

public class FragmentChatUsers extends Fragment {

    RecyclerView userRecylerView;
    ClubNameChatAdapter adapter;
    List<UserChatGroup> clubName;
    DatabaseReference userReference;

    public void getClubName() {
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clubName = new ArrayList<UserChatGroup>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    List<UserChat> userChats = new ArrayList<UserChat>();
                    String username = String.valueOf(snapshot.child("username").getValue());
                    String profileImage = String.valueOf(snapshot.child("profileImage").getValue());
                    String flag = String.valueOf(snapshot.child("flag").getValue());
                    String clubNameString = String.valueOf(snapshot.child("favoriteClub").getValue());

                    userChats.add(new UserChat(username, flag, profileImage));
                    clubName.add(new UserChatGroup(clubNameString, userChats));
                }
                adapter = new ClubNameChatAdapter(clubName, getContext());
                userRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
                userRecylerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_users,container,false);

        userRecylerView = (RecyclerView) view.findViewById(R.id.chatUserRecView);
        getClubName();

        return view;
    }
}
