package com.floreaacosmin.app.firebase;

import android.content.Context;

import com.floreaacosmin.app.data_processor.AppDataHelper;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    private final String LOG_TAG = LogUtils.makeLogTag(AppFirebaseMessagingService.class);

    private final String DEFAULT_TOPIC = "all_topics";

    public AppFirebaseMessagingService() {
        FirebaseMessaging.getInstance().subscribeToTopic(DEFAULT_TOPIC);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        // Log received messages
        LogUtils.LOGD(LOG_TAG, "From: " + remoteMessage.getFrom());

        // Check if the message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            LogUtils.LOGD(LOG_TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if the message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            LogUtils.LOGD(LOG_TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            final String message = remoteMessage.getNotification().getBody();

            // Send an intent to display the message
            final Context context = getApplicationContext();
            AppDataHelper.getInstance().sendResultToReceiver(message);
            AppDataHelper.getInstance().sentUpdateAllDataIntent(context);
        }

    }

        /* Also if you intend on generating your own notifications as a result of a
         * received FCM message, here is where that should be initiated.
         * See google sendNotification method example. */
}
