package com.malikbisic.sportapp.application;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.activity.MainPage;
import com.parse.Parse;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


/**
 * Created by Nane on 16.3.2017.
 */

public class Application extends android.app.Application{


    DatabaseReference mUsersReference;
    FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();



        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("1l5MQohP0oGkgU2LhfK89mRV6ojjQYFXc7CQwvw1")
                .clientKey("KFCpsprLkYcsIjlWXqOri5y1RqB16Ji63P5cLvYg")
                .server("https://parseapi.back4app.com/")
                .build()
        );

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        mUsersReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null){

                    String clubName = String.valueOf(dataSnapshot.child("favoriteClub").getValue());

                    final DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(clubName).child(mAuth.getCurrentUser().getUid());
                    mUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUsers.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                            mUsers.child("online").setValue(true);




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
