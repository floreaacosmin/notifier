package com.floreaacosmin.app.content_provider;

// Constants used in the WebView objects
public final class AppProviderContentContract {
	private AppProviderContentContract() {}
	
	public static final String SELECTED_NOTIFICATION_ITEM_URI = "selected_notification_item_uri";
	public static final String MIME_TYPE = "text/html; charset=UTF-8";
	public static final String NOTIFICATIONS_CASE_TITLE_KEY = "1000000";
	public static final String NOTIFICATIONS_CASE_FILTER_KEY = "1000001";
	public static final String NOTIFICATIONS_ALL = "all notifications";
	public static final String NEW_NOTIFICATIONS_ADDED = " new notifications added";
}