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


    public void getClubName() {
        userReference = FirebaseDatabase.getInstance().getReference().child("UsersChat");
        
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clubName = new ArrayList<UserChatGroup>();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final List<UserChat> userChats = new ArrayList<UserChat>();

                    final String clubNameString = snapshot.getKey().toString(); //String.valueOf(snapshot.child("favoriteClub").getValue());
                    Log.i("parentsChild", clubNameString);

                    DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(clubNameString);
                    chatReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                final String username = value.get("username").toString();
                                String profileImage = value.get("profileImage").toString();
                                String flag = value.get("flag").toString();

                                userChats.add(new UserChat(username, flag, profileImage));
                                clubName.add(new UserChatGroup(clubNameString, userChats));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_users,container,false);

        userRecylerView = (RecyclerView) view.findViewById(R.id.chatUserRecView);
        getClubName();

        return view;
    }
}
