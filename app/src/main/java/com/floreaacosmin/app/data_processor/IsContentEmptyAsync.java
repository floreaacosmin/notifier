package com.floreaacosmin.app.data_processor;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.floreaacosmin.app.content_provider.AppContentProjection;
import com.floreaacosmin.app.content_provider.AppProviderURIContract;

class IsContentEmptyAsync extends AsyncTask<Void, Void, Boolean> {
	
	private final Context context;

	public IsContentEmptyAsync(Context c) {
		super();
		this.context = c;
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		boolean isContentEmpty;
		Cursor notificationsCursor;
		boolean globalContentExists;

		/* Query the Content Provider to find out if it is empty or not and based on this 
		 * information set the proper empty message. */
		notificationsCursor = context.getContentResolver().query(AppProviderURIContract.CONTENT_NOTIFICATIONS_URI,
			AppContentProjection.NOTIFICATIONS_PROJECTION, null, null, null);
		globalContentExists = notificationsCursor.moveToFirst();
		notificationsCursor.close();

		isContentEmpty = !(globalContentExists);
		
		return isContentEmpty;
	}		
}