package com.floreaacosmin.app.data_processor;

public class AppDataServiceContract {
	private AppDataServiceContract() {}
	
	// Variables used for incoming and outgoing message data
    public static final String INTENT_COMMAND = "command";
    public static final String INTENT_GET_NOTIFICATIONS = "get_notifications";
    public static final String OPERATION_RESULT_STATUS  = "operation_result_status";
    public static final String PROGRESS_BAR_VISIBILITY  = "progress_bar_visibility";
    public static final String NEW_NOTIFICATION_RECEIVED = "new_notification";

    // Status variables
    public static final int STATUS_ERROR = 10;
    public static final int STATUS_PROGRESS_TOGGLE = 100;
    public static final int STATUS_NEW_NOTIFICATION = 200;
    public static final int STATUS_NEW_NOTIFICATIONS = 250;
    public static final int NOTIFICATION_DETAIL_REFRESH = 300;

    // Variables used in the update all data function
	public static final String LAST_RUN_TIME = "last_run_time";
	public static final String UPDATE_RUNNING = "update_running";
}