package com.floreaacosmin.app.activity;

import com.floreaacosmin.app.content_provider.AppProviderContentContract;
import com.floreaacosmin.app.data_processor.AppDataResultInterface;
import com.floreaacosmin.app.data_processor.AppDataServiceContract;
import com.floreaacosmin.app.ui.AppToastMessageView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

@SuppressLint("Registered")
public class AppDataResultReceiver extends AppBaseActivity implements AppDataResultInterface.Receiver {

	private AppToastMessageView appToastMessage;

	public void clearAppToastMessage() {
		if (appToastMessage != null) {
			appToastMessage.cancelMessage();
		}
	}
	
	@Override
	public void onReceiveResult(int resultCode, Bundle passedData) {
		switch (resultCode) {
			case AppDataServiceContract.STATUS_ERROR:
				appToastMessage = new AppToastMessageView(this, passedData.getString(
										AppDataServiceContract.OPERATION_RESULT_STATUS),
										Gravity.BOTTOM);
				appToastMessage.showMessage();
				break;
			
			case AppDataServiceContract.STATUS_PROGRESS_TOGGLE:
				/* The progress bar is not visible in all the views so first it is important 
				 * to check if it is not null. */
				if (getProgressBar() != null) {
					
					// Show or hide the progress bar
					boolean progressBarVisibility = passedData.getBoolean(AppDataServiceContract.PROGRESS_BAR_VISIBILITY);
					if (progressBarVisibility) {
						getProgressBar().setVisibility(View.VISIBLE);
					} else {
						getProgressBar().setVisibility(View.GONE);
					}
				}
				break;

			case AppDataServiceContract.STATUS_NEW_NOTIFICATION:
				// Show a toast with the received message
				appToastMessage = new AppToastMessageView(this,
						passedData.getString(AppDataServiceContract.NEW_NOTIFICATION_RECEIVED),
								Gravity.TOP, Toast.LENGTH_LONG);
                appToastMessage.showMessage();
				break;

			case AppDataServiceContract.STATUS_NEW_NOTIFICATIONS:
				// Show a message to inform how many new items were added
				appToastMessage = new AppToastMessageView(this,
						passedData.getInt(AppDataServiceContract.OPERATION_RESULT_STATUS) + 
						AppProviderContentContract.NEW_NOTIFICATIONS_ADDED, Gravity.TOP);
				appToastMessage.showMessage();
				// Update the drawer menu with new items after downloading new content
				getAppDrawerHelper().updateDrawerMenuItems();
                break;
				
			case AppDataServiceContract.NOTIFICATION_DETAIL_REFRESH:
				// Refresh the WebView inside the article detail view
				getAppItemDetailViewHelper().refreshWebView();
				break;
		}
	}

    // Register a receiver for IntentService broadcasts
    private synchronized void registerAppDataResultReceiver() {
        AppDataResultInterface.getInstance().setReceiver(this);
    }

    // Unregister the receiver in order to avoid memory leaks
    private synchronized void unregisterAppDataResultReceiver() {
        AppDataResultInterface.getInstance().setReceiver(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    	/* Instantiate the data controller class and register the receiver
		 * in order to communicate with the Intent Service. */
        registerAppDataResultReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the intent service receiver in order to avoid memory leaks
        unregisterAppDataResultReceiver();
    }
}