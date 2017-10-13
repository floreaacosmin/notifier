package com.floreaacosmin.app.volley;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.floreaacosmin.app.data_processor.AppDataHelper;
import com.floreaacosmin.app.toolbox.LogUtils;

/* Volley invokes the onErrorResponse callback method of the listener passing an instance
 * of the VolleyError object when there is an error while performing the request. */
class ErrorResponseListener  implements Response.ErrorListener {

        private final String LOG_TAG = LogUtils.makeLogTag(ErrorResponseListener.class);

        @Override
        public void onErrorResponse(VolleyError error) {

            LogUtils.LOGD(LOG_TAG, "Getting response error: " + error.toString());

            AppDataHelper.getInstance().sendResultToReceiver(error, 0);
            AppDataHelper.getInstance().sendProgressToReceiver(false);
        }
    }
