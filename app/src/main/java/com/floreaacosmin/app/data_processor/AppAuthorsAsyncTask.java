package com.floreaacosmin.app.data_processor;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.floreaacosmin.app.content_provider.AppContentProjection;
import com.floreaacosmin.app.content_provider.AppProviderURIContract;
import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.app.toolbox.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppAuthorsAsyncTask extends AsyncTask<Void, Void, List<String>> {

	private final String LOG_TAG = LogUtils.makeLogTag(AppAuthorsAsyncTask.class);

	private final Context context;

    public AppAuthorsAsyncTask(Context context) {
        this.context = context;
    }

	@Override
	protected List<String> doInBackground(Void... params) {
		
		LogUtils.LOGD(LOG_TAG, "Data query started.");

		// Get the Content Resolver instance
        ContentResolver contentResolver = context.getContentResolver();

		// Create a cursor projection
        String[] cursorProjection = AppContentProjection.AUTHORS_PROJECTION;
		LogUtils.LOGD(LOG_TAG, "The cursor projections is " + Arrays.toString(cursorProjection));

		// Get all the distinct entries stored in the local cache database
		Cursor cursor = contentResolver.query(AppProviderURIContract.CONTENT_DISTINCT_AUTHORS_URI, cursorProjection, null, null, null);

		int NOTIFICATION_AUTHOR_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_AUTHOR);

		List<String> authors = new ArrayList<>();
		while (cursor.moveToNext()) {
			authors.add(cursor.getString(NOTIFICATION_AUTHOR_INDEX));
		}

		LogUtils.LOGD(LOG_TAG, "The unique authors, stored in the array: " + authors.toString());

		// Close the cursor in order to release the resources
		cursor.close();

		return authors;
	}
}