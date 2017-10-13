package com.floreaacosmin.app.database;

import android.provider.BaseColumns;

/* Database table column names. Provide constant values for each data column name, so that consuming code 
 * can easily discover and refer. By implementing the BaseColumns interface, your inner class can inherit 
 * a primary key field called _ID that some Android classes such as cursor adapters will expect it to have. 
 * If it is not available (another name than _id maybe) an error could be thrown by the database library. */	
public final class AppDBTableColumns implements BaseColumns {
	// Empty constructor to avoid accidental instantiation
	private AppDBTableColumns() {}
	
	// Values used throughout other classes as well	
	public static final String NOTIFICATION_INTERNAL_ID = "_id";
	public static final String NOTIFICATION_ID = "notification_id";
	public static final String NOTIFICATION_NAME = "notification_name";
	public static final String NOTIFICATION_CONTENT = "notification_content";
	public static final String NOTIFICATION_DATE = "notification_date";
	public static final String NOTIFICATION_AUTHOR = "notification_author";
	public static final String NOTIFICATION_IMAGEURL = "notification_imageurl";
	public static final String NOTIFICATION_READ = "notification_read";
}
