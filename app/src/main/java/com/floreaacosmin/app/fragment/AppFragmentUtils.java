package com.floreaacosmin.app.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;

import com.floreaacosmin.app.content_provider.AppProviderContentContract;
import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.drawer.AppDrawerContract;
import com.floreaacosmin.notifier.R;

public class AppFragmentUtils {

	public AppFragmentUtils(Activity a) {
		activity = a;
	}

	private final String LOG_TAG = LogUtils.makeLogTag(AppFragmentUtils.class);

	private final Activity activity;
	private Bundle extraDataBundle;
	private int fragmentPosition;

	// Display selected fragment view when clicking the Drawer List Item
	public void displaySelectedView(int position, Uri itemUri, String drawerItemName) {
        final String NOTIFICATIONS = " notifications";
        Fragment fragmentView;

		// Save the fragment position
		fragmentPosition = position;

		LogUtils.LOGD(LOG_TAG, "The selection fragment view was: " + position);

		switch (position) {

			case AppDrawerContract.POSITION_ALL_NOTIFICATIONS:
				fragmentView = new AppItemsView();
				extraDataBundle = new Bundle();
				extraDataBundle.putString(AppProviderContentContract.NOTIFICATIONS_CASE_TITLE_KEY,
						AppProviderContentContract.NOTIFICATIONS_ALL);
				break;

            default:
                fragmentView = new AppItemsView();
                extraDataBundle = new Bundle();
                extraDataBundle.putString(AppProviderContentContract.NOTIFICATIONS_CASE_TITLE_KEY,
                        drawerItemName + NOTIFICATIONS);
                extraDataBundle.putString(AppProviderContentContract.NOTIFICATIONS_CASE_FILTER_KEY,
                        AppDBTableColumns.NOTIFICATION_AUTHOR + " = '" + drawerItemName + "'");
                break;

            case AppDrawerContract.POSITION_NOTIFICATION_DETAIL:
				fragmentView = new AppItemDetailView();
				extraDataBundle = new Bundle();
				extraDataBundle.putParcelable(AppProviderContentContract.SELECTED_NOTIFICATION_ITEM_URI, itemUri);
                break;

            case AppDrawerContract.POSITION_ABOUT:
                fragmentView = new AppAboutView();
                break;
		}

		if (fragmentView != null && extraDataBundle != null ) {

                if (fragmentView.getArguments() == null) {
					fragmentView.setArguments(extraDataBundle);
				}

				// Creating a fragment transaction
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				// Replace whatever is in the fragment_container view with this fragment
				fragmentTransaction.replace(R.id.app_main_view_frame_container, fragmentView, getFragmentTag(fragmentView));
			    /* When you remove or replace a fragment and add the transaction to the back stack,
			     * the fragment that is removed is stopped (not destroyed). If the user navigates back to
			     * restore the fragment, it restarts. If you do not add the transaction to the back stack,
			     * then the fragment is destroyed when removed or replaced. To allow the user to navigate
			     * backward through the fragment transactions, you must call addToBackStack() before you
			     * commit theFragmentTransaction. With this logic duplicates are avoided. */
                LogUtils.LOGD(LOG_TAG, "The fragment bundle when added to stack was: " + fragmentView.getArguments());
                fragmentTransaction.addToBackStack(getFragmentTag(fragmentView));

				// Commit the transaction
				fragmentTransaction.commit();

                /* Execute pending transactions in the fragment manager, so recursive entries
				 * to executePendingTransactions are avoided. */
				if (getFragmentManager().executePendingTransactions()) {
					LogUtils.LOGD(LOG_TAG, "There were pending fragment transactions");
				}

		} else {
			// Log the error appeared in creating the fragment
			LogUtils.LOGD(LOG_TAG, "Error in creating the fragment.");
		}
	}

	/* Use the fragment position in the fragment name tag for cases when the fragment class is 
     * the same but the passed bundle is different. */
	private String getFragmentTag(Fragment f) {
		return f.getClass().getName() + "_" + fragmentPosition;
	}

	// Getting reference to the FragmentManager
	private FragmentManager getFragmentManager() {
		return activity.getFragmentManager();
	}

	public String getDisplayedFragmentName() {
		int lastFragmentPosition;
		String fragmentName;

		// Get last fragment name from the BackStack (which is also displayed)
		lastFragmentPosition = getFragmentManager().getBackStackEntryCount() - 1;
		fragmentName = getFragmentManager().getBackStackEntryAt(lastFragmentPosition).getName();

		return fragmentName;
	}
}