package com.malikbisic.sportapp.application;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.yarolegovich.discretescrollview.DiscreteScrollView;


/**
 * Created by Nane on 16.3.2017.
 */

public class Application extends android.app.Application{


    static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }


        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);



    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final String myUID = mAuth.getCurrentUser().getUid();

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.i("uid", myUID);
            Log.i("close", "app");

            DocumentReference usersRef = db.collection("Users").document(myUID);
            usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String myClub = task.getResult().getString("favoriteClub");
                    Log.i("club", myClub);
                    db.collection("UsersChat").document(myClub).collection("user-id").document(myUID).update("online", FieldValue.serverTimestamp()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("online", "update");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("errorUpdateOnline", e.getLocalizedMessage());
                        }
                    });

                }
            });        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
