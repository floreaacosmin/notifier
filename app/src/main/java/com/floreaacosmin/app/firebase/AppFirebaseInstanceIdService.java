package com.floreaacosmin.app.firebase;

import com.floreaacosmin.app.toolbox.LogUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class AppFirebaseInstanceIdService extends FirebaseInstanceIdService {

    /* On initial startup of your app, the FCM SDK generates a registration token for the
     * client app instance. If you want to target single devices, or create device groups,
     * you'll need to access this token.*/

    private final String LOG_TAG = LogUtils.makeLogTag(AppFirebaseInstanceIdService.class);

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        LogUtils.LOGD(LOG_TAG, "Refreshed token: " + refreshedToken);

        /* If you want to send messages to this application instance or
         * manage this apps subscriptions on the server side, send the
         * Instance ID token to your app server. */
        sendRegistrationToServer(refreshedToken);
    }

    /* Persist token to third-party servers.
     * Modify this method to associate the user's FCM InstanceID token with any
     * server-side account maintained by your application, where token is the new
     * token. */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
