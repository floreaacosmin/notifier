package com.floreaacosmin.app.activity;

import android.net.Uri;
import android.os.Bundle;

import com.floreaacosmin.app.drawer.AppDrawerContract;
import com.floreaacosmin.app.network.AppNetworkReceiverHelper;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.utils.ListItemSelectedCommunicator;

public class AppMainActivity extends AppDataResultReceiver implements ListItemSelectedCommunicator {
	
	private AppNetworkReceiverHelper appNetworkReceiverHelper;

	private final String LOG_TAG = LogUtils.makeLogTag(AppMainActivity.class);

	/* Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);

		// Instantiate the network receiver
		appNetworkReceiverHelper = new AppNetworkReceiverHelper();
		
	    if (savedInstanceState == null) {
            // On first time display show the first view from the drawer menu
			getAppDrawerHelper().selectItemInDrawer(AppDrawerContract.POSITION_ALL_NOTIFICATIONS, null);
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		/* Register the network receiver. The registering is done here so the update data 
		 * function runs every time the application is resumed. */
		appNetworkReceiverHelper.registerReceiver(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// Unregister the network receiver
		appNetworkReceiverHelper.unregisterReceiver(this);
	}
	
	@Override
	public void onListItemClicked(Uri itemUri) {

		LogUtils.LOGD(LOG_TAG, "onListItemClicked with itemUri: " + itemUri);

		if (itemUri != null) {
			getAppFragmentUtils().displaySelectedView(AppDrawerContract.POSITION_NOTIFICATION_DETAIL, itemUri, null);
		}
	}
}