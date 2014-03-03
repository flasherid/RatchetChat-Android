package com.scotttherobot.ratchet;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by scott on 2/27/14.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final String BROADCAST_ACTION = "com.scotttherobot.ratchet.updatethread";
    Intent broadcastIntent;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        broadcastIntent = new Intent(BROADCAST_ACTION);

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i("GCMINTENT", "Received: " + extras.toString());
                try {
                    String message = extras.getString("message");
                    String title = extras.getString("title");
                    String threadid = extras.getString("threadid");
                    String threadname = extras.getString("threadname");
                    sendNotification(title, message, threadid, threadname);
                } catch (Exception e) {
                    Log.e("GCMINTENT", "Couldn't get message", e);
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String msg, String threadid, String threadname) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // See if the device is awake. If it's not:
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(getApplicationContext().getPackageName().toString())) {
            isActivityFound = true;
        }

        Intent threadIntent = new Intent(this, MessageThreadActivity.class);
        threadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        threadIntent.putExtra("threadid", Integer.parseInt(threadid));
        threadIntent.putExtra("threadname", threadname);

        broadcastIntent.putExtra("threadid", threadid);
        broadcastIntent.putExtra("threadname", threadname);

        // If the app is not in the foreground, post a notification into the bar.
        if (!isActivityFound) {
            Log.v("GCM", "The app is IS NOT running");

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, threadIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.kim)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg))
                            .setContentText(msg);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else {
            Log.v("GCM", "The app IS running");
            // ELSE just make a sound and update the UI.
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, threadIntent, 0);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

            sendBroadcast(broadcastIntent);
        }
    }
}
