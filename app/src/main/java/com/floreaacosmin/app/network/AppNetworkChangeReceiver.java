package com.floreaacosmin.app.network;

import com.floreaacosmin.app.toolbox.LogUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* Broadcast receiver which gets called whenever network state changes. The state change is 
 * handled by showing a toast message with the status of the network state. */
public class AppNetworkChangeReceiver extends BroadcastReceiver {
	
	private final String LOG_TAG = LogUtils.makeLogTag(AppNetworkChangeReceiver.class);
	
	private AppNetworkUtils appNetworkUtilsInstance;
	
	public final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
		
    @Override
    public void onReceive(final Context context, final Intent intent) { 
    	LogUtils.LOGD(LOG_TAG, "Receiver triggered");
    	
    	if (appNetworkUtilsInstance == null) {
    		appNetworkUtilsInstance = new AppNetworkUtils();
    	}
    	
    	appNetworkUtilsInstance.retrieveDataIfNetworkAvailable(context);
    }
}