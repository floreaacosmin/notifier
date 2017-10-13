package com.floreaacosmin.app.volley;

import com.android.volley.Response;
import com.floreaacosmin.app.data_processor.AppDataHelper;
import com.floreaacosmin.app.data_processor.AppNotificationsAsyncTask;
import com.floreaacosmin.app.object_models.Notification;
import com.floreaacosmin.app.toolbox.LogUtils;
import android.content.Context;

class NotificationsResponseListener implements Response.Listener<Notification[]> {

    private final Context context;

    public NotificationsResponseListener(Context c){
        context = c;
    }

    private final String LOG_TAG = LogUtils.makeLogTag(NotificationsResponseListener.class);

    @Override
    public void onResponse(Notification[] response) {
        try {

            LogUtils.LOGD(LOG_TAG, "Getting notifications response.");

    		   /* Set the update status as successful and run the parsing
    		    * function if articles are returned. */
            if (response.length > 0) {
                // Run the parsing and data insertion in an AsyncTask
                new AppNotificationsAsyncTask(context, response).execute();
            } else {
                LogUtils.LOGD(LOG_TAG, "Not running the parsing method because the number of items was: " +
                        response.length);
                // Send an notification through the receiver in order to hide the progress bar
                AppDataHelper.getInstance().sendProgressToReceiver(false);
            }
        } catch (Exception e) {
            LogUtils.LOGD(LOG_TAG, "Exception: ", e);
        }
    }
}
