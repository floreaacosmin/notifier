package com.floreaacosmin.app.utils;

import android.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.floreaacosmin.notifier.R;

class AppEmptyViewUtils {

	public AppEmptyViewUtils(Fragment fragment, ListView listView) {
		setListViewEmptyView(fragment, listView);
	}

	private void setListViewEmptyView(Fragment fragment, ListView listView) {

		/* Instantiate the ListView empty view. A ViewStub is used as it lets you inflate a view hierarchy
		 * only when needed (it is a just in time inflation). */
		LinearLayout listViewEmptyView = fragment.getView().findViewById(R.id.app_items_view_empty_view);
		ImageView emptyViewCity = fragment.getView().findViewById(R.id.app_empty_view_icon);

		Animation rotateAnimation = AnimationUtils.loadAnimation(fragment.getActivity(), R.anim.rotate_indefinitely_animation);
		// Start the ImageView animation
		emptyViewCity.startAnimation(rotateAnimation);
		/* Handle the ListView empty case. The AdapterView and the empty view are mutually exclusive in
		 * term of visibility: if the empty view is visible (View.VISIBLE) the AdapterView has the View.GONE.
		 * AdapterView determines its emptiness by looking at the following conditions: the underlying Adapter
		 * is null, a call to isEmpty() on the underlying Adapter returns true. */
		listView.setEmptyView(listViewEmptyView);
	}
}