package com.floreaacosmin.app.content_provider;

import android.content.ContentResolver;
import android.net.Uri;

/* A contract class is a container for constants that define names, variables for URIs. The contract 
 * class allows you to use the same constants across all the other classes in the same package. This 
 * permits changing a column name in one place and have it propagate throughout your code. */

public final class AppProviderURIContract {
	private AppProviderURIContract() {}

	/* Content provider classes generally provide some public constants that can be used by apps to
	 * identify the data they want to query. Inside the content provider, some help is available for
	 * determining what kind of URI is being passed in. */
	
	// The authority of the App Content Provider
	public static final String AUTHORITY = "com.floreaacosmin.app";
	private static final String CONTENT_PREFIX = "content://";
	public static final String BASE_PATH_NOTIFICATIONS = "notifications";

	// The content URI for the top-level Content Provider Authority
	public static final Uri CONTENT_NOTIFICATIONS_URI = Uri.parse(CONTENT_PREFIX + AUTHORITY + "/" + BASE_PATH_NOTIFICATIONS);
	
	// MIME types used for getting a list, or a single item, they are different MIME types 
	private static final String NOTIFICATION = "notification";
	
	// Get list of items
	public static final String CONTENT_NOTIFICATIONS_LIST_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_PATH_NOTIFICATIONS;

	// Get single item
	public static final String CONTENT_NOTIFICATIONS_ITEM_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + NOTIFICATION;

	// Helper constants for use with the UriMatcher
	public static final int ALL_NOTIFICATIONS = 10;
	public static final int NOTIFICATION_ID = 11;
}