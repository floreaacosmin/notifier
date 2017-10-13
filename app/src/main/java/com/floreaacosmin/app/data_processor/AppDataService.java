package com.floreaacosmin.app.data_processor;

import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.volley.AppVolleyRequestHelper;

import android.app.IntentService;
import android.content.Intent;

/* IntentService is a simple type of service that can be used to handle asynchronous work off 
 * the main thread by way of Intent requests. Each intent is added to the IntentService’s queue 
 * and handled sequentially. The Android Service has a longer life than an Android Activity and 
 * if it is desired to complete a task even if the app UI is no longer available a service should 
 * be used. */
public class AppDataService extends IntentService {
	
	private final String LOG_TAG = LogUtils.makeLogTag(AppDataService.class);
	
	public AppDataService() {
		// Used for naming the worker thread 
		super(AppDataService.class.getName());
	}

	/* This method is where the processing occurs. Any data necessary for each processing request can 
	 * be packaged in the intent extras. */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		// Get the intent extra sent from the activity
		String intentCommand = intent.getStringExtra(AppDataServiceContract.INTENT_COMMAND);
		
		LogUtils.LOGD(LOG_TAG, "Intent service started with the command: " + intentCommand);
		
		// Send progress into the instantiated result receiver
		AppDataHelper.getInstance().sendProgressToReceiver(true);
		
		if (intentCommand.equals(AppDataServiceContract.INTENT_GET_NOTIFICATIONS)) {
			AppVolleyRequestHelper.getInstance().addGsonNotificationsRequest(this);
		}	
		
		LogUtils.LOGD(LOG_TAG, "Intent service stopping with the command: " + intentCommand);
		
		this.stopSelf();
	}
}