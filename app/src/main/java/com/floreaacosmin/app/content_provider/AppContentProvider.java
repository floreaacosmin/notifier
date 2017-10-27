package com.floreaacosmin.app.content_provider;

import java.util.Arrays;

import com.floreaacosmin.app.database.AppDBContract;
import com.floreaacosmin.app.database.AppDBHelper;
import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.toolbox.SelectionBuilder;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/* AppContentProvider implements update(), insert(), delete() and query() methods which map 
 * to AppDBHelper interface */
public class AppContentProvider extends ContentProvider {

	private final String LOG_TAG = LogUtils.makeLogTag(AppContentProvider.class);

    private final String UNKNOWN_URI = "Unknown Uri: ";

	private AppDBHelper appDBHelper;
	private UriMatcher uriMatcher;
	private AppContentHelper appContentHelperInstance;
	
	@Override
	public boolean onCreate() {
		// Instantiate the AppContentHelper class
		appContentHelperInstance = new AppContentHelper();
		
		/* To determine what kinds of URI addresses are passed to the content provider, we can leverage a 
		 * helper class called UriMatcher to define specific URI patterns the content provider will support.
		 * Instantiate the UriMatcher Object. */
		uriMatcher = appContentHelperInstance.buildUriMatcher();
		
		appDBHelper = new AppDBHelper(getContext());
		// Return true only if the DB is created
		return (appDBHelper != null);
	}

	/* Called to determine the content type that will be returned for a given Uri. This might tell the 
	 * consuming application how to handle that data. */
	@Override
	public String getType(@NonNull Uri uri) {
        final int uriMatch = uriMatcher.match(uri);
        		
		switch (uriMatch) {
			case AppProviderURIContract.ALL_NOTIFICATIONS:
				// List
				return AppProviderURIContract.CONTENT_NOTIFICATIONS_LIST_MIME_TYPE;

			case AppProviderURIContract.NOTIFICATION_ID:
				// Single Item
				return AppProviderURIContract.CONTENT_NOTIFICATIONS_ITEM_MIME_TYPE;

			default:
				throw new IllegalArgumentException (UNKNOWN_URI + uri);
		}
	}	
	
	/* Parse the Uri parameter and call the correct database method. If the Uri contains an ID parameter
	 * then the integer is parsed out (using LastPathSegment) and used in the database query. */
	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		
		// Instantiate a Database object as a readable database
		final SQLiteDatabase appDatabase = appDBHelper.getReadableDatabase();
	    /* The UriMatcher class is used to determine if the query is for a single entry or all 
	     * entries. If it's a single entry, we add a where clause to filter by just the unique artId. */
		final int match = uriMatcher.match(uri);
		
		LogUtils.LOGD(LOG_TAG, "Query triggered - the received uri was: " + uri + ", match: " + match +
			", projection: " + Arrays.toString(projection) + ", selection: " + selection + 
			", arguments: " + Arrays.toString(selectionArgs));
		
        // All cases are handled with ExpandedSelectionBuilder
        final SelectionBuilder selectionBuilder = appContentHelperInstance.buildExpandedSelection(uri, match);

		/* We call the query() method of the SQLiteQueryBuilder class. It takes many of the same
		 * parameters as were passed to our query() method so we just pass them along. Then we return
		 * the newly created cursor. */
	    @SuppressWarnings("UnnecessaryLocalVariable")
		Cursor cursor;

		switch (match) {
            case AppProviderURIContract.ALL_NOTIFICATIONS:
            case AppProviderURIContract.NOTIFICATION_ID:
                cursor = selectionBuilder.where(selection, selectionArgs).query(appDatabase, projection,
                        appContentHelperInstance.getSortOrder(match, sortOrder));
                break;

			case AppProviderURIContract.DISTINCT_AUTHORS:
                cursor = appDatabase.rawQuery("SELECT DISTINCT n.notification_author FROM notifications_table n", null);
                break;

            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri);
		}
        // No need to notify, because this is a query, the data is not updated
        return cursor;
	}
	
	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values) {
		
        LogUtils.LOGD(LOG_TAG, "Insert operation with Uri: " + uri + ", values: " + values.toString());
		
		// Get Database object as a writable database
		final SQLiteDatabase appDatabase = appDBHelper.getWritableDatabase();
		final int match = uriMatcher.match(uri);
		long rowInsertedId;
	   
	    switch (match) {
	    	case AppProviderURIContract.ALL_NOTIFICATIONS:
	    		rowInsertedId = appDatabase.insert(AppDBContract.NOTIFICATIONS_TABLE, null, values);
	    		break;
	    	// If none of the above matches than return an exception
	    	default:
	    		throw new IllegalArgumentException(UNKNOWN_URI + uri);
	    }
	    Uri itemUri = ContentUris.withAppendedId(uri, rowInsertedId);
	    // Notify all listeners that the change has been performed only if the item was added successfully
		if (rowInsertedId > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
	    return itemUri;
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		
        LogUtils.LOGD(LOG_TAG, "Update operation with Uri: " + uri + ", values=" + values.toString());
		
		// Get Database object as a writable database
        final SQLiteDatabase appDatabase = appDBHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
		String updateId;
		
		switch (match) {
	    	case AppProviderURIContract.ALL_NOTIFICATIONS:
	    		rowsUpdated = appDatabase.update(AppDBContract.NOTIFICATIONS_TABLE,
	    			values, selection, selectionArgs);
	    		break;
	    	case AppProviderURIContract.NOTIFICATION_ID:
	    		updateId = uri.getLastPathSegment();
	    		if (TextUtils.isEmpty(selection)) {
	    			rowsUpdated = appDatabase.update(AppDBContract.NOTIFICATIONS_TABLE,
	    			values,	AppDBTableColumns.NOTIFICATION_INTERNAL_ID + "=" + updateId, null);
	    		} else {
	    			rowsUpdated = appDatabase.update(AppDBContract.NOTIFICATIONS_TABLE,
	    			values,	AppDBTableColumns.NOTIFICATION_INTERNAL_ID + "=" + updateId +
	    			" and " + selection, selectionArgs);
	    		}
	    		break;
	    	default:
	    		throw new IllegalArgumentException(UNKNOWN_URI + uri);
		}
		// Notify listeners of the performed change
		if (rowsUpdated > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
	    return rowsUpdated;
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		// Get Database object as a writable database
		final SQLiteDatabase appDatabase = appDBHelper.getWritableDatabase();
		final int match = uriMatcher.match(uri);
		int rowsDeleted;
		String deleteId;
		
		switch (match) {
			case AppProviderURIContract.ALL_NOTIFICATIONS:
		 		rowsDeleted = appDatabase.delete(AppDBContract.NOTIFICATIONS_TABLE,
		 			selection, selectionArgs);
		 		break;
		 	case AppProviderURIContract.NOTIFICATION_ID:
		 		deleteId = uri.getLastPathSegment();
		 	    if (TextUtils.isEmpty(selection)) {
		 	    	rowsDeleted = appDatabase.delete(AppDBContract.NOTIFICATIONS_TABLE,
		 	    		AppDBTableColumns.NOTIFICATION_INTERNAL_ID + "=" + deleteId, null);
		 	    } else {
		 	        rowsDeleted = appDatabase.delete(AppDBContract.NOTIFICATIONS_TABLE,
		 	        	AppDBTableColumns.NOTIFICATION_INTERNAL_ID + "=" + deleteId +
		 	        	" and " + selection, selectionArgs);
		 	    }
		 	    break;
		 	default:
		 	   throw new IllegalArgumentException(UNKNOWN_URI + uri);
		 }
		// Notify listeners of the performed change
		if (rowsDeleted > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
	    return rowsDeleted;
	}	
	
	@Override
	public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] allValues) {
		// Get Database object as a writable database
		final SQLiteDatabase appDatabase = appDBHelper.getWritableDatabase();		
		final int match = uriMatcher.match(uri);
		int rowsBulkInserted;
		
	    switch (match) {
    		case AppProviderURIContract.ALL_NOTIFICATIONS:
    			rowsBulkInserted = appContentHelperInstance.insertBulkItems(appDatabase,
						allValues);
    			break;
    			// If none of the above matches than return an exception
    		default:
    			throw new IllegalArgumentException(UNKNOWN_URI + uri);
	    }		
	    
	    // Notify all listeners that the change has been performed only if the item was added successfully
		if (rowsBulkInserted > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return rowsBulkInserted;
	}
}