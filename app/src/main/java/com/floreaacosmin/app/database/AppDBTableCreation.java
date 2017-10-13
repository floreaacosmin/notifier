package com.floreaacosmin.app.database;

import android.database.sqlite.SQLiteDatabase;

import com.floreaacosmin.app.toolbox.LogUtils;

// Define the Database Schema
class AppDBTableCreation {
	
	private static final String LOG_TAG = LogUtils.makeLogTag(AppDBTableCreation.class);
	
	// Table creation SQL statement
	private static final String createNotificationsTableStatement =
		"create table " + AppDBContract.NOTIFICATIONS_TABLE + "("
		+ AppDBTableColumns.NOTIFICATION_INTERNAL_ID + " integer primary key autoincrement, "
		+ AppDBTableColumns.NOTIFICATION_ID + " integer not null unique, "
		+ AppDBTableColumns.NOTIFICATION_NAME + " text not null, "
		+ AppDBTableColumns.NOTIFICATION_CONTENT + " text not null, "
		+ AppDBTableColumns.NOTIFICATION_DATE + " text not null, "
		+ AppDBTableColumns.NOTIFICATION_AUTHOR + " text not null, "
		+ AppDBTableColumns.NOTIFICATION_IMAGEURL + " text not null, "
		+ AppDBTableColumns.NOTIFICATION_READ + " integer default 0 "
		+ ");";

	//Method that will be called during the creation of the database in the Helper Class
	public static void onCreate(final SQLiteDatabase database) {
		// Create the table inside the database
		database.execSQL(createNotificationsTableStatement);
	}

	/* Method to be called during an upgrade of the database (version increase) in the Helper Class
	 * If the database version is incremented and the app is installed over an existing installation,
	 * this code will be triggered. The warning,is only shown in LogCat and it simply states that
	 * existing contents will be lost. */ 
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		LogUtils.LOGD(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + 
			newVersion + ", which will destroy all old data");
		
        /* This database is only a cache for online data, so its upgrade policy is to simply 
         * discard the data (delete the tables) and start over (create the tables again). */
		database.execSQL("DROP TABLE IF EXISTS " + AppDBContract.NOTIFICATIONS_TABLE);
		onCreate(database);
	}
}
