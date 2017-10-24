package com.floreaacosmin.app.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.floreaacosmin.app.activity.AppMainActivity;
import com.floreaacosmin.app.data_processor.AppDataHelper;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.notifier.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    private final String LOG_TAG = LogUtils.makeLogTag(AppFirebaseMessagingService.class);

    public AppFirebaseMessagingService() {
        /* Subscribe to the default topic in order to received notifications, messages can be
        sent to the application only from the Firebase console(and not from the API). */
        final String DEFAULT_TOPIC = "all_topics";
        FirebaseMessaging.getInstance().subscribeToTopic(DEFAULT_TOPIC);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if the message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            // Log received messages
            LogUtils.LOGD(LOG_TAG, "From: " + remoteMessage.getFrom() + " received this message data payload: " +
                remoteMessage.getData());

            final String contentData = remoteMessage.getData().get("content");




            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.app_icon)
                            .setContentTitle("My notification")
                            .setContentText("Hello World!")
                            .setAutoCancel(true);

            Intent resultIntent = new Intent(this, AppMainActivity.class);
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

            // Sets an ID for the notification
            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());





            // Send an intent to display the message
            AppDataHelper.getInstance().sendResultToReceiver(contentData);
            // Send an intent to refresh the content data
            AppDataHelper.getInstance().sentUpdateAllDataIntent(getApplicationContext());
        }
    }

        /* Also if you intend on generating your own notifications as a result of a
         * received FCM message, here is where that should be initiated.
         * See google sendNotification method example. */
}
