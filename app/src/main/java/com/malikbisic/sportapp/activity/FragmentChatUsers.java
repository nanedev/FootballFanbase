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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryListenOptions;
import com.google.firebase.firestore.QuerySnapshot;
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
    FirebaseFirestore mReference;
    CollectionReference userReference;
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
        mReference = FirebaseFirestore.getInstance();
        userReference = mReference.collection("UsersChat");

        userReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot dataSnapshot, FirebaseFirestoreException e) {
                clubName = new ArrayList<UserChatGroup>();

                if (e != null){
                    Log.e("errorClub", e.getLocalizedMessage());
                }

                for (final DocumentSnapshot snapshot : dataSnapshot.getDocuments()) {

                    if (snapshot.exists()) {
                        final String clubNameString = snapshot.getId();
                        Log.i("clubName", clubNameString);

                        final DocumentReference chatReference = mReference.collection("UsersChat").document(clubNameString);
                        chatReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                                final List<UserChat> userChats = new ArrayList<UserChat>();
                                numberOnline = 0;
                                if (e != null){
                                    Log.e("errorUsersChat", e.getLocalizedMessage());
                                }

                                    clubNameLogo = String.valueOf(dataSnapshot.getString("favoriteClubLogo"));
                                    isOnline = Boolean.parseBoolean(String.valueOf(dataSnapshot.getString("online")));
                                    userUID = String.valueOf(dataSnapshot.getString("userID"));
                                    date = String.valueOf(dataSnapshot.getString("date"));

                                    DocumentReference userInfo = mReference.collection("Users").document(userUID);
                                    userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                            if (e != null){
                                                Log.e("erroruserInfo", e.getLocalizedMessage());
                                            }
                                            username = String.valueOf(dataSnapshot.getString("username"));
                                            profileImage = String.valueOf(dataSnapshot.getString("profileImage"));
                                            flag = String.valueOf(dataSnapshot.getString("flag"));

                                            userChats.add(new UserChat(username, flag, profileImage, userUID, date, isOnline));
                                            Collections.sort(userChats, new CheckOnline());

                                        }
                                    });


                                            if (isOnline) {
                                                numberOnline++;
                                            }

                                            online = String.valueOf(FragmentChatUsers.numberOnline);






                                    //  userChats.add(new UserChat(FragmentChatUsers.username, FragmentChatUsers.flag, FragmentChatUsers.profileImage, FragmentChatUsers.userUID, FragmentChatUsers.date));


                                clubName.add(new UserChatGroup(clubNameString, userChats, clubNameLogo, numberOnline));


                                Collections.sort(clubName, new OnlineNumber());


                                adapter = new ClubNameChatAdapter(clubName, getContext());
                                userRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                userRecylerView.setAdapter(adapter);

                                adapter.notifyDataSetChanged();

                            }
                        });


                    }
                }
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
