package com.floreaacosmin.app.volley;

import android.content.Context;

import com.android.volley.Request;
import com.floreaacosmin.app.application.AppBaseApplication;
import com.floreaacosmin.app.object_models.Notification;
import com.floreaacosmin.app.toolbox.GsonRequest;
import com.floreaacosmin.app.toolbox.LogUtils;

public class AppVolleyRequestHelper {

    private static AppVolleyRequestHelper instance;

    private AppVolleyRequestHelper(){}

    public static synchronized AppVolleyRequestHelper getInstance() {
        if (instance == null) {
            instance = new AppVolleyRequestHelper();
        }
        return instance;
    }

	private final String LOG_TAG = LogUtils.makeLogTag(AppVolleyRequestHelper.class);

	public void addGsonNotificationsRequest(Context context) {

		LogUtils.LOGD(LOG_TAG, "Adding Volley items request using url: " + AppVolleyContract.API_URL);

		GsonRequest<Notification[]> gsonNotificationsRequest = new GsonRequest<>(
				Request.Method.GET,
                AppVolleyContract.API_URL,
				Notification[].class,
				null,
				new NotificationsResponseListener(context),
				new ErrorResponseListener(),
                null);

		((AppBaseApplication) context.getApplicationContext()).addToRequestQueue
			(gsonNotificationsRequest, AppVolleyContract.GSON_NOTIFICATIONS_REQUEST_TAG);
    }
}