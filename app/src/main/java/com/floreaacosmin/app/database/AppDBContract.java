package com.floreaacosmin.app.database;

// Class containing values used in the database creation used by the Content Provider
public final class AppDBContract {
	private AppDBContract() {}
	
	public static final String DATABASE_NAME = "appDBncache.db";
	public static final int DATABASE_VERSION = 1;
	
	public static final String NOTIFICATIONS_TABLE = "notifications_table";
}
