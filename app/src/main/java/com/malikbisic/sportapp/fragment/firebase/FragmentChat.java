package com.malikbisic.sportapp.fragment.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.adapter.firebase.ChatListAdapter;
import com.malikbisic.sportapp.model.firebase.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nane on 3.9.2017.
 */

public class FragmentChat extends Fragment {

    RecyclerView usersChatList;
    List<Messages> messagesList = new ArrayList<>();
    List<Messages> userList = new ArrayList<>();
    ChatListAdapter mAdapter;
    FirebaseAuth mAuth;


    int prevCount = 0;
    int currentCount;

    String prevMessage;
    String currentMessage;
    boolean firstCall = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Intent closeAPP = new Intent(getContext(), StopAppServices.class);
        getActivity().startService(closeAPP);

        mAuth = FirebaseAuth.getInstance();
        usersChatList = (RecyclerView) view.findViewById(R.id.recView_usersChat_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mAdapter = new ChatListAdapter(messagesList, getContext());
        usersChatList.setLayoutManager(manager);
        usersChatList.setAdapter(mAdapter);
        loadMessagesList();

        updateMessagesList();

        firstCall = true;
        return view;
    }

    private void loadMessagesList() {
        messagesList.clear();
        CollectionReference messageListQUERY = FirebaseFirestore.getInstance().collection("Messages").document(mAuth.getCurrentUser().getUid()).collection("chat-user");
        messageListQUERY.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                messagesList.clear();
                for (final DocumentSnapshot snap : task.getResult()) {

                    final String toUserID = snap.getId();
                    com.google.firebase.firestore.Query messageListRef = FirebaseFirestore.getInstance().collection("Messages").document(mAuth.getCurrentUser().getUid()).collection("chat-user")
                            .document(toUserID).collection("message");
                    messageListRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (DocumentChange snapshot : task.getResult().getDocumentChanges()) {
                                if (snapshot.getType() == DocumentChange.Type.ADDED) {


                                    Messages messages = snapshot.getDocument().toObject(Messages.class).withId(toUserID);
                                    messagesList.add(messages);

                                    currentMessage = snapshot.getDocument().getId();
                                }

                                currentCount = mAdapter.getItemCount();


                            }
                            mAdapter.notifyDataSetChanged();

                            prevCount = mAdapter.getItemCount();
                        }
                    });

                }
            }
        });


    }

    private void updateMessagesList() {
        CollectionReference messageListQUERY = FirebaseFirestore.getInstance().collection("Messages").document(mAuth.getCurrentUser().getUid()).collection("chat-user");
        messageListQUERY.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {

                for (DocumentSnapshot snap : querySnapshot.getDocuments()) {
                    final String toUserID = snap.getId();
                    com.google.firebase.firestore.Query messageListRef = FirebaseFirestore.getInstance().collection("Messages").document(mAuth.getCurrentUser().getUid()).collection("chat-user")
                            .document(toUserID).collection("message");
                    messageListRef.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {




                                    if (firstCall) {

                                        messagesList.clear();
                                        loadMessagesList();
                                    }


                        }
                    });

                }
            }
        });


    }
}
