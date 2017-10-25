package com.floreaacosmin.app.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

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

        /* Check if the message contains a notification. In Firebase, messages are of two types: notifications
         * and data. Notifications are designed to be displayed - this is why we use them here. In data type's
         * case additional methods must be developed in order to determine if the application is in foreground
         * or backround, as the notification is displayed in the action bar in both cases by default. */
        if (remoteMessage.getNotification() != null) {

            RemoteMessage.Notification receivedNotification = remoteMessage.getNotification();
            String notificationMerged = receivedNotification.getTitle() + " : " +  receivedNotification.getBody();

            LogUtils.LOGD(LOG_TAG, "Notification received: " + notificationMerged);

            // Send an intent to display the message
            AppDataHelper.getInstance().sendResultToReceiver(notificationMerged);
            // Send an intent to refresh the content data
            AppDataHelper.getInstance().sentUpdateAllDataIntent(getApplicationContext());
        }

        if (remoteMessage.getData().size() > 0) {

            // Log received messages
            LogUtils.LOGD(LOG_TAG, "From: " + remoteMessage.getFrom() + " received this message data payload: " +
                remoteMessage.getData());

            final String itemContent = remoteMessage.getData().get("content");
            final String itemAuthor = remoteMessage.getData().get("author");
            // Set an ID for the notification in case you need to fetch it after
            final int notificationId = 100;
            /* If you target Android 8.0 (API level 26) and post a notification without specifying a
             * valid notifications channel, the notification fails to post and the system logs an error. */
            final String notificationChannelId = "notifier_channel_01";

            Intent resultIntent = new Intent(this, AppMainActivity.class);
            /* Because clicking the notification opens a new ("special") activity, there's no need to
            create an artificial back stack. */
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle(itemAuthor)
                    .setContentText(itemContent)
                    .setAutoCancel(true)
                    .setChannel(notificationChannelId)
                    .setContentIntent(resultPendingIntent);

            // Gets an instance of the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it
            notificationManager.notify(notificationId, notificationBuilder.build());

            // Send an intent to display the message
            AppDataHelper.getInstance().sendResultToReceiver(itemContent);
            // Send an intent to refresh the content data
            AppDataHelper.getInstance().sentUpdateAllDataIntent(getApplicationContext());
        }
    }

        /* Also if you intend on generating your own notifications as a result of a
         * received FCM message, here is where that should be initiated.
         * See google sendNotification method example. */
}
