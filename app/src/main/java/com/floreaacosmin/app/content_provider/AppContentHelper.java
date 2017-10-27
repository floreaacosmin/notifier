package com.floreaacosmin.app.content_provider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.floreaacosmin.app.database.AppDBContract;
import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.toolbox.SelectionBuilder;

class AppContentHelper {
	
	private final String LOG_TAG = LogUtils.makeLogTag(AppContentHelper.class);
	
	/* Define UriMatcher method, the function helps parse the requests to determine what data 
	 * to return. Catches all variations supported by the ContentProvider. */
	public final UriMatcher buildUriMatcher() {
		
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AppProviderURIContract.AUTHORITY;
		
		// Request to return the full list of items
		uriMatcher.addURI(authority, AppProviderURIContract.BASE_PATH_NOTIFICATIONS,
			AppProviderURIContract.ALL_NOTIFICATIONS);
		// Request to return a single item, # is a placeholder for a numeric parameter (_id of the row in DB)
		uriMatcher.addURI(authority, AppProviderURIContract.BASE_PATH_NOTIFICATIONS + "/#",
			AppProviderURIContract.NOTIFICATION_ID);
		// Request to query after distinct authors
		uriMatcher.addURI(authority, AppProviderURIContract.BASE_PATH_DISTINCT_AUTHORS,
				AppProviderURIContract.DISTINCT_AUTHORS);

		return uriMatcher;
	}
	
	/* Method used to bulk insert items in the Content Provider, bulk inserting is faster because 
	 * it uses transactions. It is generally used for large data sets. */
	public int insertBulkItems(SQLiteDatabase dataBase, ContentValues[] allValues) {
	    int rowsBulkInserted = 0;
	    long insertedRowId;
	    ContentValues values;
	    
	    try {
	        dataBase.beginTransaction();
	        
	        for (ContentValues initialValues : allValues) {
	            values = initialValues == null ? new ContentValues() : new ContentValues(initialValues);
	            insertedRowId = dataBase.insert(AppDBContract.NOTIFICATIONS_TABLE, null, values);
	            if (insertedRowId > 0)
	                rowsBulkInserted++;
	        }
	        
	        dataBase.setTransactionSuccessful();
	    } catch (SQLException sqlException) {
	    	LogUtils.LOGD(LOG_TAG, "There was a problem with the bulk insert: ", sqlException);
	    } finally {
	        dataBase.endTransaction();
	    }
	     
	    return rowsBulkInserted;
	}	
	
    /* Build an advanced SelectionBuilder to match the requested Uri. This will be used 
     * by the query method, since it performs table joins useful for cursor data but also 
     * for regular operations such as insert, update and delete. */
    public SelectionBuilder buildExpandedSelection(Uri uri, int match) {
    	
        final SelectionBuilder selectionBuilder = new SelectionBuilder();
        
        switch (match) {
            case AppProviderURIContract.ALL_NOTIFICATIONS:
            case AppProviderURIContract.DISTINCT_AUTHORS: {
				// No filters in this case
                return selectionBuilder.table(AppDBContract.NOTIFICATIONS_TABLE);
            }

            case AppProviderURIContract.NOTIFICATION_ID: {
				// Add the ID to the original query (the ID is the last part of the Uri)
                final String itemId = uri.getLastPathSegment();
                return selectionBuilder.table(AppDBContract.NOTIFICATIONS_TABLE)
                	.where(AppDBTableColumns.NOTIFICATION_INTERNAL_ID + "=?", itemId);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
    
    public String getSortOrder(int match, String sortOrder) {
		final String DESCENDING_ORDER = " DESC";
		String finalSortOrder;

	    // Set the sort order for the different categories based on the table type
	    if (TextUtils.isEmpty(sortOrder)) {
	        // If no sort order is specified use the default
	        switch(match) {
	        	case AppProviderURIContract.ALL_NOTIFICATIONS:
                case AppProviderURIContract.NOTIFICATION_ID:
	        		finalSortOrder = AppDBTableColumns.NOTIFICATION_DATE + DESCENDING_ORDER;
	        		break;

	        	default:
	        		throw new UnknownError("Unknown sort order for the Uri match type: " + match);
	        }
	    } else {
	    	finalSortOrder = sortOrder;
	    }
	    
	    return finalSortOrder;
	}    
}