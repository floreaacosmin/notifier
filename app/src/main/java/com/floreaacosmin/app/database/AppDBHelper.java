package com.floreaacosmin.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Create the application’s underlying SQLite database
public class AppDBHelper extends SQLiteOpenHelper {
		
	public AppDBHelper(Context context) {	
		super(context, AppDBContract.DATABASE_NAME, null,
			AppDBContract.DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		AppDBTableCreation.onCreate(database);
	}

	/* Method is called during an upgrade of the database (increase the version). 
	 * In a new application version if the structure of the tables have been changed, 
	 * the database version needs to be incremented also so that the existing database 
	 * (if any) will get deleted avoiding any issues related to the data structure. */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		AppDBTableCreation.onUpgrade(database, oldVersion, newVersion);
	}
}
