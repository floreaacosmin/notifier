package com.floreaacosmin.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.floreaacosmin.app.activity.AppMainActivity;
import com.floreaacosmin.app.data_processor.AppDataHelper;
import com.floreaacosmin.app.data_processor.AppDataResultInterface;
import com.floreaacosmin.app.data_processor.AppDataServiceContract;
import com.floreaacosmin.app.drawer.AppDrawerContract;

class AppNetworkUtils {

    // Identify what kind of Internet connection is available on the device
    private boolean isDeviceConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        
        boolean isConnected;

        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        
        return isConnected;
    }
    
    private void refreshWebView(Context context) {
    	// Try to refresh the WebView only if the respective fragment is displayed
    	if (((AppMainActivity) context).getAppFragmentUtils()
                .getDisplayedFragmentName().contains(AppDrawerContract.NOTIFICATION_DETAIL_VIEW_TAG)) {

            // Function used to send a refresh request for the article detail view to the intent receiver
            AppDataResultInterface.getInstance().send(AppDataServiceContract.NOTIFICATION_DETAIL_REFRESH, null);
    	}
    }
    
    /* Initiate the data update functions if the network becomes available and refresh 
     * the article detail WebView if that fragment is visible. */
    public void retrieveDataIfNetworkAvailable(Context context) {
    	boolean isNetworkConnected = isDeviceConnected(context);

        if (isNetworkConnected) {
        	AppDataHelper.getInstance().sentUpdateAllDataIntent(context);
    		
        	refreshWebView(context);
        }
    }    
}