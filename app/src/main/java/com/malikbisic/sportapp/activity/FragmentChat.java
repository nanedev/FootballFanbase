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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.ChatListAdapter;
import com.malikbisic.sportapp.model.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nane on 3.9.2017.
 */

public class FragmentChat extends Fragment {

    RecyclerView usersChatList;
    List<Messages> messagesList = new ArrayList<>();
    ChatListAdapter mAdapter;
    FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);

        mAuth = FirebaseAuth.getInstance();
        usersChatList = (RecyclerView) view.findViewById(R.id.recView_usersChat_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mAdapter = new ChatListAdapter(messagesList, getContext());
        usersChatList.setLayoutManager(manager);
        usersChatList.setAdapter(mAdapter);


        loadMessagesList();
        return view;
    }

    private void loadMessagesList() {
        final DatabaseReference chat = FirebaseDatabase.getInstance().getReference().child("messages").child(mAuth.getCurrentUser().getUid());
        chat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    Log.i("key", key);

                    DatabaseReference usersInfo = chat.child(key);
                    Query query = usersInfo.limitToLast(1);
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
