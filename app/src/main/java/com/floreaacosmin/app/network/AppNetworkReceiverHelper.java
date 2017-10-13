package com.floreaacosmin.app.network;

import android.content.Context;
import android.content.IntentFilter;

public class AppNetworkReceiverHelper {

	public AppNetworkReceiverHelper() {
		setupReceiver();
	}

	private AppNetworkChangeReceiver appNetworkChangeReceiver;
	private IntentFilter networkChangeReceiverFilter;	
	
	private void setupReceiver() {
		
		// Instantiate the Network Change Receiver
		appNetworkChangeReceiver = new AppNetworkChangeReceiver();
		// Instantiate the Network Change Receiver Filter and add its parameters
		networkChangeReceiverFilter = new IntentFilter();
		networkChangeReceiverFilter.addAction(appNetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE);	
	}
	
	public void registerReceiver(Context c) {
		c.registerReceiver(appNetworkChangeReceiver, networkChangeReceiverFilter);
	}
	
	public void unregisterReceiver(Context c) {
		c.unregisterReceiver(appNetworkChangeReceiver);
	}
}