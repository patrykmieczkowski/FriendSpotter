package com.mieczkowskidev.friendspotter.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Patryk Mieczkowski on 2015-12-28
 */
public class MyGcmListenerService extends GcmListenerService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    public final static String TAG = MyGcmListenerService.class.getSimpleName();

    public NotificationManager mNotificationManager;


    @Override
    public void onMessageReceived(String from, Bundle data) {

        String title = data.getString("title");
        String text = data.getString("text");

        Log.e(TAG, "notification title " + title + ", text: " + text);

//        addMessageToDatabase(title, message);
        createNotification(title, text);
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String text) {
        final Context context = getBaseContext();

//        Intent intent = new Intent(context, NotificationActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setNumber(3)
                .setAutoCancel(true);

//        mBuilder.setContentText("You have " + mBuilder.mNumber + " notifications...");

        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(3, mBuilder.build());

    }

    public void clearNotifications() {
        mNotificationManager.cancelAll();
    }

//    private static Magnet mMagnet;
//    private static Handler handler = new Handler(Looper.getMainLooper());
//    private static ImageView iconView;

}
