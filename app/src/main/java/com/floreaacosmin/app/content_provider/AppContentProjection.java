package com.floreaacosmin.app.content_provider;

import com.floreaacosmin.app.database.AppDBTableColumns;

public class AppContentProjection {
	private AppContentProjection() {}
	
	public static final String[] NOTIFICATIONS_PROJECTION = {
			AppDBTableColumns.NOTIFICATION_INTERNAL_ID,
			AppDBTableColumns.NOTIFICATION_ID,
			AppDBTableColumns.NOTIFICATION_NAME,
			AppDBTableColumns.NOTIFICATION_CONTENT,
			AppDBTableColumns.NOTIFICATION_DATE,
			AppDBTableColumns.NOTIFICATION_AUTHOR,
			AppDBTableColumns.NOTIFICATION_IMAGEURL,
			AppDBTableColumns.NOTIFICATION_READ
	};

	public static final String[] AUTHORS_PROJECTION = {
			AppDBTableColumns.NOTIFICATION_AUTHOR
	};
}