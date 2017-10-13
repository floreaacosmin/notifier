package com.floreaacosmin.app.utils;

import com.floreaacosmin.app.data_processor.AppDataHelper;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.notifier.R;

import android.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.ListView;

class AppSwipeRefreshUtils {

	private final String LOG_TAG = LogUtils.makeLogTag(AppSwipeRefreshUtils.class);
	
	private SwipeRefreshLayout swipeRefreshLayout;
	private Fragment fragment;
	private ListView listView;
	
	public void setupSwipeLayout(Fragment f, ListView lv) {
		// Save the received instances
		fragment = f;
		listView = lv;
		
		// Instantiate the SwapRefreshLayout
		swipeRefreshLayout = fragment.getView().findViewById
			(R.id.app_items_view_list_view_swipe_container);
		// Set the refresh colors
		swipeRefreshLayout.setColorSchemeResources(R.color.sky_blue, R.color.sky_blue, R.color.sky_blue, R.color.sky_blue);
		swipeRefreshLayout.setOnRefreshListener(new SwipeOnRefreshListener());
	}
	
	public void applySwapEnableLogic() {
		
        if (listView != null && listView.getVisibility() == View.VISIBLE) {
            // Enabling or disabling the refresh layout
            swipeRefreshLayout.setEnabled(!canListViewScrollUp(listView));
        }
	}
	
	/* Utility method to check whether a ListView can scroll up from it's current 
	 * position. Handles platform version differences, providing backwards compatible 
	 * functionality where needed. */ 
    private boolean canListViewScrollUp(ListView listView) {
            // For ICS and above canScrollVertically() can be called to determine this
		//noinspection deprecation
		return ViewCompat.canScrollVertically(listView, -1);
    } 	
	
	private class SwipeOnRefreshListener implements OnRefreshListener {
		@Override
		public void onRefresh() {
			LogUtils.LOGD(LOG_TAG, "Refresh triggered");

			AppDataHelper.getInstance().sentUpdateAllDataIntent(fragment.getActivity());
			swipeRefreshLayout.setRefreshing(false);
		}
	}	
}