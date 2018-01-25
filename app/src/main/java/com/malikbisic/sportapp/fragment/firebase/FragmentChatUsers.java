package com.malikbisic.sportapp.fragment.firebase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.malikbisic.sportapp.adapter.api.SelectLeagueAdapter;
import com.malikbisic.sportapp.model.api.LeagueModel;
import com.malikbisic.sportapp.model.firebase.UserChat;
import com.malikbisic.sportapp.model.firebase.UserChatGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

/**
 * Created by Nane on 31.8.2017.
 */

public class FragmentChatUsers extends Fragment implements SearchView.OnQueryTextListener{

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
    List<UserChat> userChats;
    String clubNameString;
    FirebaseAuth mauth;
    SearchView searchView;

    public FragmentChatUsers() {
    }

    public void getClubName() {



        userReference = FirebaseFirestore.getInstance().collection("UsersChat");


        userReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                clubName = new ArrayList<UserChatGroup>();
                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()){

                    if (documentSnapshot.exists()){

                        final String  clubNameString = documentSnapshot.getId();
                        final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("UsersChat").document(clubNameString).collection("user-id");

                        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                final List<UserChat> userChats = new ArrayList<UserChat>();
                                numberOnline = 0;
                                for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()){


                                    username = String.valueOf(snapshot.getString("username"));
                                    profileImage = String.valueOf(snapshot.getString("profileImage"));
                                    flag = String.valueOf(snapshot.getString("flag"));
                                    clubNameLogo = String.valueOf(snapshot.getString("favoriteClubLogo"));
                                    isOnline = Boolean.parseBoolean(snapshot.getString("online"));
                                    userUID = String.valueOf(snapshot.getString("userID"));
                                    date = String.valueOf(snapshot.getString("date"));
                                    userChats.add(new UserChat(username, flag, profileImage, userUID,date,isOnline));
                                    Collections.sort(userChats, new CheckOnline());
                                }
                                if (isOnline){
                                    numberOnline++;
                                }
                                clubName.add(new UserChatGroup(clubNameString, userChats, clubNameLogo,numberOnline));
                                Collections.sort(clubName, new OnlineNumber());
                                adapter = new ClubNameChatAdapter(clubName, getContext(),getActivity());
                                userRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                userRecylerView.setAdapter(adapter);

                                adapter.notifyDataSetChanged();
                            }
                        });

                    }


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_users, container, false);

        userRecylerView = (RecyclerView) view.findViewById(R.id.chatUserRecView);
        searchView = (android.widget.SearchView) view.findViewById(R.id.search_for_club_name);
        getClubName();
        setupSearchView();
       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                newText = newText.toLowerCase();
                ArrayList<UserChatGroup> newList = new ArrayList<>();
                for (UserChatGroup group : clubName) {
                    String name = group.getTitle().toLowerCase();
                    if (name.contains(newText)) {

                        newList.add(group);


                    }
                }

                userRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new ClubNameChatAdapter(newList, getActivity().getApplicationContext(), getActivity());
                userRecylerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                return true;


            }
        }); */

        return view;
    }

    private void setupSearchView() {

        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        searchView.clearFocus();
        searchView.setQueryHint("Search club");

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    class OnlineNumber implements Comparator<UserChatGroup> {

        @Override
        public int compare(UserChatGroup e1, UserChatGroup e2) {
            if (e1.getNumberOnline() < e2.getNumberOnline()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    class CheckOnline implements Comparator<UserChat> {

        @Override
        public int compare(UserChat e1, UserChat e2) {
            if (e1.isIsonline()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}