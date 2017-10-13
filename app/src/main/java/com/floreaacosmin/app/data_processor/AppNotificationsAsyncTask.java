package com.floreaacosmin.app.data_processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.app.fragment.AppItemsView;
import com.floreaacosmin.app.content_provider.AppContentProjection;
import com.floreaacosmin.app.content_provider.AppProviderURIContract;
import com.floreaacosmin.app.object_models.Notification;
import com.floreaacosmin.app.toolbox.LogUtils;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v4.util.LongSparseArray;

public class AppNotificationsAsyncTask extends AsyncTask<Void, Void, Void> {

	private final String LOG_TAG = LogUtils.makeLogTag(AppNotificationsAsyncTask.class);
	
	private final Context context;
	private final Notification[] requestResponse;

    public AppNotificationsAsyncTask(Context context, Notification[] requestResponse) {
        this.context = context;
        this.requestResponse = requestResponse;
    }

	private int numberOfNewItemsInserted;

	@Override
	protected Void doInBackground(Void... params) {	
		
		LogUtils.LOGD(LOG_TAG, "Data processing started.");

		// Get the Content Resolver instance
        ContentResolver contentResolver = context.getContentResolver();

        /* Get the array of items from the container object and store them in a HashMap. Build a
         * key value pair of the incoming entries. A sparse array (faster than a hash map)
		 * is a data structure consisting of a set of keys and values in which keys are mapped to
		 * values (indices can have gaps). At this point the HashMap is not filtered but needs to be. */

		LongSparseArray<Notification> allDownloadedNotifications = new LongSparseArray<>();
        for (Notification e : requestResponse) {
            allDownloadedNotifications.put(e.getId(), e);
        }

        int numberOfLocalItems = 0;
        int numberOfLocalItemsUpdated = 0;
        int numberOfLocalItemsDeleted = 0;
		numberOfNewItemsInserted = 0;

		// Log the number of received downloaded items
		LogUtils.LOGD(LOG_TAG, "Parsing complete, found " +	allDownloadedNotifications.size() + " entries in the downloaded data");

		// Initialize the content batch insert object
        ArrayList<ContentProviderOperation>	batchContentProviderOperation = new ArrayList<>();

		// Get a list of the local entries stored in the content provider
		LogUtils.LOGD(LOG_TAG, "Fetching local entries for merge");

		// Create a cursor projection
        String[] cursorProjection = AppContentProjection.NOTIFICATIONS_PROJECTION;

		LogUtils.LOGD(LOG_TAG, "The cursor projections is " + Arrays.toString(cursorProjection));

		// Get all the entries stored locally in the local cache database
		Cursor cursor = contentResolver.query(AppProviderURIContract.CONTENT_NOTIFICATIONS_URI, cursorProjection, null, null, null);
		LogUtils.LOGD(LOG_TAG, String.format(Locale.US, "Found %d local entries, computing merge solution", cursor.getCount()));

		int NOTIFICATION_INTERNAL_ID_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_INTERNAL_ID);
		int NOTIFICATION_ID_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_ID);
		int NOTIFICATION_NAME_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_NAME);
		int NOTIFICATION_CONTENT_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_CONTENT);
		int NOTIFICATION_DATE_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_DATE);
		int NOTIFICATION_AUTHOR_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_AUTHOR);
		int NOTIFICATION_IMAGEURL_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_IMAGEURL);
		int NOTIFICATION_READ_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_READ);

        int notificationInternalId;
		// Find the stale data from the content provider
		while (cursor.moveToNext()) {
			// Get the internal article Id from the cursor in order to create later the item Uri
			notificationInternalId = cursor.getInt(NOTIFICATION_INTERNAL_ID_INDEX);

			// Keep track of how many entries were stored locally
			numberOfLocalItems++;

			// Get the values from the content provider and store them
            Notification storedNotification = new Notification(
                    cursor.getLong(NOTIFICATION_ID_INDEX),
                    cursor.getString(NOTIFICATION_NAME_INDEX),
                    cursor.getString(NOTIFICATION_CONTENT_INDEX),
                    cursor.getString(NOTIFICATION_DATE_INDEX),
                    cursor.getString(NOTIFICATION_AUTHOR_INDEX),
					cursor.getString(NOTIFICATION_IMAGEURL_INDEX),
					cursor.getInt(NOTIFICATION_READ_INDEX)
            );

            Notification downloadedNotification = allDownloadedNotifications.get(storedNotification.getId());

            Uri contentNotificationItemURI;

			if (downloadedNotification != null) {
				/* Entry exists already in the local database. It will be removed from the entry 
				 * map to prevent later insertion. */
				allDownloadedNotifications.remove(storedNotification.getId());
				/* Although it already exists in the local database, it will be checked to see if the entry 
				 * needs to be updated. This check is done by comparing the current downloaded item with the 
				 * item stored locally with the same artId. */
				contentNotificationItemURI = Uri.parse(AppProviderURIContract.CONTENT_NOTIFICATIONS_URI + "/" + notificationInternalId);

				/* In the case of items only the Id is compared as this is unique and besides this all the
				 * other content values are static. */
				if ((downloadedNotification.getId() != null &&
					!downloadedNotification.getId().equals(storedNotification.getId()))) {

					// If an update is needed, the existing local record is updated using the new downloaded values
					LogUtils.LOGD(LOG_TAG, "Scheduling item update: " +	contentNotificationItemURI);
					
					batchContentProviderOperation.add(ContentProviderOperation.newUpdate(contentNotificationItemURI)
                            .withValue(AppDBTableColumns.NOTIFICATION_ID, downloadedNotification.getId())
                            .withValue(AppDBTableColumns.NOTIFICATION_NAME, downloadedNotification.getName())
                            .withValue(AppDBTableColumns.NOTIFICATION_CONTENT, downloadedNotification.getContent())
                            .withValue(AppDBTableColumns.NOTIFICATION_DATE, downloadedNotification.getNotificationDate())
							.withValue(AppDBTableColumns.NOTIFICATION_AUTHOR, downloadedNotification.getAuthor())
							.withValue(AppDBTableColumns.NOTIFICATION_IMAGEURL, downloadedNotification.getImageUrl())
							.withValue(AppDBTableColumns.NOTIFICATION_READ, downloadedNotification.getIsRead())
						    .build());
					// Keep track of how many local items were updated
					numberOfLocalItemsUpdated++;
				} else {
					LogUtils.LOGD(LOG_TAG, "No update action needed: " + contentNotificationItemURI);
				}
			} else {
				/* The entry does not exist in the new downloaded data, meaning it is and obsolete entry 
				 * so it must be removed from the database as entries not present anymore on server side
				 * are not kept in cache. */
				contentNotificationItemURI = Uri.parse(AppProviderURIContract.CONTENT_NOTIFICATIONS_URI + "/" + notificationInternalId);

				LogUtils.LOGD(LOG_TAG, "Scheduling item delete: " + contentNotificationItemURI);
				
				batchContentProviderOperation.add(ContentProviderOperation.newDelete(contentNotificationItemURI).build());
				// Keep track of how many local entries were deleted
				numberOfLocalItemsDeleted++;
			}
		}
		// Close the cursor in order to release the resources
		cursor.close();

		/* At this step only completely new items are left in the items map, 
		 * and are added in the local database through a batch insert. */

        for (int i = 0; i < allDownloadedNotifications.size(); i++) {
            long key = allDownloadedNotifications.keyAt(i);
            // Get the stored object by the key
            Notification e = allDownloadedNotifications.get(key);

			LogUtils.LOGD(LOG_TAG, "Scheduling item insert: entry_id = " + e.getId());
			
			batchContentProviderOperation.add(ContentProviderOperation.newInsert
				(AppProviderURIContract.CONTENT_NOTIFICATIONS_URI)
                    .withValue(AppDBTableColumns.NOTIFICATION_ID, e.getId())
                    .withValue(AppDBTableColumns.NOTIFICATION_NAME, e.getName())
                    .withValue(AppDBTableColumns.NOTIFICATION_CONTENT, e.getContent())
                    .withValue(AppDBTableColumns.NOTIFICATION_DATE, e.getNotificationDate())
					.withValue(AppDBTableColumns.NOTIFICATION_AUTHOR, e.getAuthor())
					.withValue(AppDBTableColumns.NOTIFICATION_IMAGEURL, e.getImageUrl())
                    .withValue(AppDBTableColumns.NOTIFICATION_READ, e.getIsRead())
				    .build());
			// Keep track of how many new items were inserted
			numberOfNewItemsInserted++;
		}
		LogUtils.LOGD(LOG_TAG, "Merge solution ready, applying batch items update");
		try {
            contentResolver.applyBatch(AppProviderURIContract.AUTHORITY, batchContentProviderOperation);
			/* Set the last update time. This function runs here because the last update time must be
			* set only if the data request was successful and the parsing completed successfully. */
			AppDataHelper.getInstance().setLastUpdateTime(context);
		} catch (RemoteException e) {
			LogUtils.LOGD(LOG_TAG, "RemoteException: ", e);
		} catch (OperationApplicationException e) {
			LogUtils.LOGD(LOG_TAG, "OperationApplicationException: ", e);
		}

		// Log what has been done after the method completes
		LogUtils.LOGD(LOG_TAG, "There were " + numberOfLocalItems +
				" item(s) stored locally, " + numberOfLocalItemsUpdated +
				" local items were updated, " + numberOfLocalItemsDeleted +
				" local items were obsolete and deleted, and " + numberOfNewItemsInserted +
				" new item(s) were inserted (only if the table constraints were met). ");
		
		LogUtils.LOGD(LOG_TAG, "Items data processing finished.");
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		/* Send a notification message through the receiver if there were new items added.
		 * The check for the items added number is done inside the function. */
		AppDataHelper.getInstance().sendResultToReceiver
			(null, numberOfNewItemsInserted);
		// Refresh the list in order to reflect the changes made in the content provider 
		AppItemsView.refreshArticlesCursorLoader();
		// Send an notification through the receiver in order to hide the progress bar
		AppDataHelper.getInstance().sendProgressToReceiver(false);
	}
}