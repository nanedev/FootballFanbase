package com.malikbisic.sportapp;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.Parse;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


/**
 * Created by Nane on 16.3.2017.
 */

public class Application extends android.app.Application {


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



    }

}
