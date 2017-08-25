package com.malikbisic.sportapp.activity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.malikbisic.sportapp.R;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by korisnik on 25/08/2017.
 */

public class BadgeServices extends IntentService {

    private int notificationId = 0;

    public BadgeServices() {
        super("BadgeServices");
    }

    private NotificationManager mNotificationManager;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int badgeCount = intent.getIntExtra("badgeCount", 0);

            mNotificationManager.cancel(notificationId);
            notificationId++;

            ShortcutBadger.applyCount(getApplicationContext(), badgeCount);

        }
    }
}
