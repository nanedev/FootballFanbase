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

import java.util.ArrayList;
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
    String profileImage;
    String username;
    String flag;
    String clubNameLogo;
    boolean isOnline;
    int numberOnline;
    String online;


    public void getClubName() {
        userReference = FirebaseDatabase.getInstance().getReference().child("UsersChat");
        
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clubName = new ArrayList<UserChatGroup>();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()){


                    final String clubNameString = snapshot.getKey().toString();

                    DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(clubNameString);
                    chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final List<UserChat> userChats = new ArrayList<UserChat>();
                            numberOnline = 0;
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                username = String.valueOf(snapshot1.child("username").getValue());
                                profileImage = String.valueOf(snapshot1.child("profileImage").getValue());
                                flag = String.valueOf(snapshot1.child("flag").getValue());
                                clubNameLogo = String.valueOf(snapshot1.child("favoriteClubLogo").getValue());
                                isOnline = (boolean) snapshot1.child("online").getValue();

                                if (isOnline){
                                    numberOnline++;
                                }

                                online = String.valueOf(numberOnline);



                                userChats.add(new UserChat(username, flag, profileImage));
                            }
                                clubName.add(new UserChatGroup(clubNameString, userChats, clubNameLogo, online));


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


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference setOfline = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(MainPage.myClubName).child(MainPage.uid);
        setOfline.child("online").setValue(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DatabaseReference setOfline = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(MainPage.myClubName).child(MainPage.uid);
        setOfline.child("online").setValue(false);
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
