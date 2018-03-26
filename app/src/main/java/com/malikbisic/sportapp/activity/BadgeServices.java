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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by korisnik on 25/08/2017.
 */

public class BadgeServices extends FirebaseMessagingService {

    private int notificationId;

    FirebaseAuth mAuth;





    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        notificationId = new Random().nextInt(60000);
        String action = remoteMessage.getData().get("action");
        if (action != null) {
            if (action.equals("chat")) {
                Bitmap bitmap = getBitmapfromUrl(remoteMessage.getData().get("profileImage"));
                String username = remoteMessage.getData().get("username");
                String from_user_id = remoteMessage.getData().get("userId");
                String clickAction = remoteMessage.getNotification().getClickAction();


                Uri defaulSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification.Builder builder = new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setLargeIcon(getCircleBitmap(bitmap))

                        .setContentTitle("Football fanbase")
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(defaulSoundUri);


                Intent resultIntent = new Intent(clickAction);
                resultIntent.putExtra("userId", from_user_id);
                resultIntent.putExtra("username", username);

// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );


                builder.setContentIntent(resultPendingIntent);


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId /* ID of notification */, builder.build());


                mAuth = FirebaseAuth.getInstance();

                FirebaseUser user = mAuth.getCurrentUser();
                final String myUserId = user.getUid();
            } else {
                Bitmap bitmap = getBitmapfromUrl(remoteMessage.getData().get("profileImage"));
                String postKey = remoteMessage.getData().get("post_key");
                String from_user_id = remoteMessage.getData().get("from_user_id");
                String clickAction = remoteMessage.getNotification().getClickAction();


                Uri defaulSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification.Builder builder = new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setLargeIcon(getCircleBitmap(bitmap))

                        .setContentTitle("Football fanbase")
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(defaulSoundUri);


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


                builder.setContentIntent(resultPendingIntent);


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId /* ID of notification */, builder.build());


                mAuth = FirebaseAuth.getInstance();

                FirebaseUser user = mAuth.getCurrentUser();
                final String myUserId = user.getUid();
            }
        }
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream(); return BitmapFactory.decodeStream(input);
        } catch (Exception e)
        { e.printStackTrace();
            return null;
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

}
