package com.malikbisic.sportapp.activity;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.malikbisic.sportapp.R;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by korisnik on 25/08/2017.
 */

public class BadgeServices extends FirebaseMessagingService {

    private int notificationId = 0;
    DatabaseReference notificationReference;
    FirebaseAuth mAuth;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        launcherCounter();

        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notification");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();
        DatabaseReference getNumberNotification = notificationReference.child(myUserId);


        String notification_title = remoteMessage.getNotification().getTitle();
        String notifification_body = remoteMessage.getNotification().getBody();
        String clickAction = remoteMessage.getNotification().getClickAction();
        String from_user_id = remoteMessage.getData().get("from_user_id");
        String postKey = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(BadgeServices.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notification_title)
                        .setContentText(notifification_body);

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("post_key", postKey);

// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }


    public void launcherCounter() {
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notification");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();
        DatabaseReference getNumberNotification = notificationReference.child(myUserId);

        Query query = getNumberNotification.orderByChild("seen").equalTo(false);

        query.addValueEventListener(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.N_MR1)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int number = (int) dataSnapshot.getChildrenCount();

                if (number == 0) {

                    ShortcutBadger.removeCount(getApplicationContext());


                } else {
                    startService(
                            new Intent(BadgeServices.this, BadgeServices.class));
                    ShortcutBadger.applyCount(getApplicationContext(), number);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Intent intent2 = new Intent(Intent.ACTION_MAIN);
        intent2.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent2, PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;

    }

}
