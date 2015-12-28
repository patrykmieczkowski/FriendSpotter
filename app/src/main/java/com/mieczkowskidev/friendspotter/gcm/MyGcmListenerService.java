package com.mieczkowskidev.friendspotter.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.mieczkowskidev.friendspotter.R;

/**
 * Created by Patryk Mieczkowski on 2015-12-28
 */
public class MyGcmListenerService extends GcmListenerService {

    public static final int MESSAGE_NOTIFICATION_ID = 4561;

    public final static String TAG = MyGcmListenerService.class.getSimpleName();

    public NotificationManager mNotificationManager;


    @Override
    public void onMessageReceived(String from, Bundle data) {

        String title = data.getString("title");
        String text = data.getString("text");

        Log.e(TAG, "notification title " + title + ", text: " + text);

        createNotification(title, text);
    }

    private void createNotification(String title, String text) {
        final Context context = getBaseContext();

        Log.d(TAG, "createNotification() called with: " + "title = [" + title + "], text = [" + text + "]");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.test_app_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);

        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());

    }

    public void clearNotifications() {
        mNotificationManager.cancelAll();
    }

}
