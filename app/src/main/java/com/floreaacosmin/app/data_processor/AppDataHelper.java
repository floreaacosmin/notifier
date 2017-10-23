package com.floreaacosmin.app.data_processor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.volley.VolleyErrorHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class AppDataHelper {

	private static AppDataHelper instance;

	private AppDataHelper(){}

	public static synchronized AppDataHelper getInstance() {
		if (instance == null) {
			instance = new AppDataHelper();
		}
		return instance;
	}

	private final String LOG_TAG = LogUtils.makeLogTag(AppDataHelper.class);
	
	private SharedPreferences applicationPreferences;
	private long lastSavedUpdateUnixTime;
	
	/* Synchronized methods enable a simple strategy for preventing thread interference and memory 
	 * consistency errors: if an object is visible to more than one thread, all reads or writes to 
	 * that object's variables are done through synchronized methods. */
	public synchronized void sentUpdateAllDataIntent(Context context) {
		boolean isContentEmpty = true;
		
		try {
			isContentEmpty = new IsContentEmptyAsync(context).execute().get();
		} catch (InterruptedException e) {
			LogUtils.LOGD(LOG_TAG, "InterruptedException: ", e);
		} catch (ExecutionException e) {
			LogUtils.LOGD(LOG_TAG, "ExecutionException: ", e);
		}

		// Set this value in seconds (example: one hour = 3600)
		final int minimumRefreshInterval = 1;
		// Set the default never run value by decreasing the current time by 24 hours
		long neverUpdatedUnixTime = (Calendar.getInstance().getTimeInMillis() / 1000L) - (2 * minimumRefreshInterval);
		// Get the last saved run time from preferences
		lastSavedUpdateUnixTime = getApplicationPreferences(context).getLong
			(AppDataServiceContract.LAST_RUN_TIME, neverUpdatedUnixTime);
		// Get update function running state from preferences
		boolean updateFunctionRunning = getApplicationPreferences(context).getBoolean
			(AppDataServiceContract.UPDATE_RUNNING, false);
		
		/* Compare the current time with the last time when the update was run, and if it is 
		 * greater than an hour update else do nothing. There is no need to take into consideration 
		 * the case when the data is deleted by the user because if the data is deleted the last run 
		 * date becomes unavailable and so the logic is applied. But there can be case in which the 
		 * function is run but there is no Internet connection, and so the function must be run again 
		 * until data is downloaded.*/
    	if (((((Calendar.getInstance().getTimeInMillis() / 1000L) - lastSavedUpdateUnixTime) > minimumRefreshInterval) &&
    		!updateFunctionRunning) || isContentEmpty) {
    		
    		// The update function is running
    		getApplicationPreferences(context).edit().putBoolean(AppDataServiceContract.UPDATE_RUNNING, true).apply();
    		
			LogUtils.LOGD(LOG_TAG, "Running the data update process because the last update was run at: " + 
    			convertUnixTimeToDate() + ", the update function is running: " +
    			String.valueOf(updateFunctionRunning) + ", the content is empty: " + String.valueOf(isContentEmpty));
    		
    		// Send the intent to download all the data
			context.startService((new Intent(context, AppDataService.class)).putExtra(
					AppDataServiceContract.INTENT_COMMAND,
					AppDataServiceContract.INTENT_GET_NOTIFICATIONS
					)
			);

    		
        	getApplicationPreferences(context).edit().putBoolean(AppDataServiceContract.UPDATE_RUNNING, 
            	false).apply();
        	
    	} else {
			LogUtils.LOGD(LOG_TAG, "Not running the data update process because the last update was run at: " + 
    			convertUnixTimeToDate() + ", the update function is running: " +
    			String.valueOf(updateFunctionRunning) + ", the content is empty: " + String.valueOf(isContentEmpty));
    	}
	}

	private String convertUnixTimeToDate() {
		// Multiply by 1000 in order to convert seconds to milliseconds
		Date date = new Date(lastSavedUpdateUnixTime*1000L);
		// The format of the date
		SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss", Locale.getDefault());

        return sdf.format(date);
	}

	public void sendResultToReceiver(Object error, int newArticlesAdded) {
		// Send a status through the result receiver in order to display a message to the user			
    	Bundle bundle = new Bundle();
    	if (error != null) {
        	bundle.putString(AppDataServiceContract.OPERATION_RESULT_STATUS, 
            	VolleyErrorHelper.getMessage(error));
            AppDataResultInterface.getInstance().send(AppDataServiceContract.STATUS_ERROR, bundle);
    	}
    	if (newArticlesAdded > 0) {
    		bundle.putInt(AppDataServiceContract.OPERATION_RESULT_STATUS, newArticlesAdded);
    		AppDataResultInterface.getInstance().send(AppDataServiceContract.STATUS_NEW_NOTIFICATIONS, bundle);
    	}
	}

	public void sendResultToReceiver(String message) {
		// Send a status through the result receiver in order to display a message to the user
		Bundle bundle = new Bundle();

		bundle.putString(AppDataServiceContract.NEW_NOTIFICATION_RECEIVED, message);
		AppDataResultInterface.getInstance().send(AppDataServiceContract.STATUS_NEW_NOTIFICATION, bundle);
	}

	public void sendProgressToReceiver(boolean isRunning) {
		// Send progress into the instantiated result receiver
		Bundle bundle = new Bundle();
		bundle.putBoolean(AppDataServiceContract.PROGRESS_BAR_VISIBILITY, isRunning);
		AppDataResultInterface.getInstance().send(
		        AppDataServiceContract.STATUS_PROGRESS_TOGGLE,
                bundle);
	}
	
	private SharedPreferences getApplicationPreferences(Context context) {
		// Instantiate the application preferences only once
		if (applicationPreferences == null) {
			applicationPreferences = PreferenceManager.getDefaultSharedPreferences(context);			
		}
		return applicationPreferences;
	}

    // Set the time the last data update was performed
    public void setLastUpdateTime(Context context) {
		long lastUpdateUnixTime = Calendar.getInstance().getTimeInMillis() / 1000L;
    	getApplicationPreferences(context).edit().putLong(AppDataServiceContract.LAST_RUN_TIME, 
    	lastUpdateUnixTime).apply();
	}
}