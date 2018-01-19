package com.malikbisic.sportapp.fragment.firebase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.api.ClubNameChatAdapter;
import com.malikbisic.sportapp.model.firebase.UserChat;
import com.malikbisic.sportapp.model.firebase.UserChatGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Nane on 31.8.2017.
 */

public class FragmentChatUsers extends Fragment {

    RecyclerView userRecylerView;
    ClubNameChatAdapter adapter;
    List<UserChatGroup> clubName = new ArrayList<UserChatGroup>();
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


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_users, container, false);
        userRecylerView = (RecyclerView) view.findViewById(R.id.chatUserRecView);



        Collections.sort(clubName, new OnlineNumber());


        adapter = new ClubNameChatAdapter(clubName, getContext());
        userRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userRecylerView.setAdapter(adapter);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query userReference = db.collection("UsersChat");

        userReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (final DocumentSnapshot snapshot : task.getResult()) {
                    Log.i("club userchat", "otvoreno");

                    if (snapshot.exists()) {
                        final String clubNameString = snapshot.getId();
                        Log.i("clubName", clubNameString);

                        final DocumentReference chatReference = db.collection("UsersChat").document(clubNameString);
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

                                DocumentReference userInfo = db.collection("Users").document(userUID);
                                userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                        if (e != null){
                                            Log.e("erroruserInfo", e.getLocalizedMessage());
                                        }
                                        username = String.valueOf(dataSnapshot.getString("username333s"));
                                        profileImage = String.valueOf(dataSnapshot.getString("profileImage"));
                                        flag = String.valueOf(dataSnapshot.getString("flag"));

                                        // userChats.add(new UserChat(username, flag, profileImage, userUID, date, isOnline));
                                        Collections.sort(userChats, new CheckOnline());

                                    }
                                });


                                if (isOnline) {
                                    numberOnline++;
                                }

                                online = String.valueOf(FragmentChatUsers.numberOnline);






                                userChats.add(new UserChat(FragmentChatUsers.username, FragmentChatUsers.flag, FragmentChatUsers.profileImage, FragmentChatUsers.userUID, FragmentChatUsers.date, isOnline));

                                clubName.add(new UserChatGroup(clubNameString, userChats, clubNameLogo, numberOnline));

                                adapter.notifyDataSetChanged();

                            }
                        });


                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("err", e.getLocalizedMessage());
            }
        });





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
