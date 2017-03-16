package com.malikbisic.sportapp;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Nane on 16.3.2017.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
