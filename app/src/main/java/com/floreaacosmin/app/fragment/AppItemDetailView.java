package com.floreaacosmin.app.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.floreaacosmin.app.activity.AppBaseActivity;
import com.floreaacosmin.app.data_processor.AppAsyncQueryHandler;
import com.floreaacosmin.app.content_provider.AppContentProjection;
import com.floreaacosmin.app.content_provider.AppProviderContentContract;
import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.notifier.R;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;

public class AppItemDetailView extends Fragment {

	private View rootView;
	private Uri itemUri;
	private ParallaxScrollView parallaxScrollView;

	private final String LOG_TAG = LogUtils.makeLogTag(AppItemDetailView.class);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Must be set to true so invalidateOptionsMenu() can be called
		setHasOptionsMenu(true);

		LogUtils.LOGD(LOG_TAG, "Fragment created.");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.app_item_detail_view, container, false);

        return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Enabled in order to save the state across different changed (including screen configuration)
		setRetainInstance(true);

		parallaxScrollView = rootView.findViewById(R.id.app_item_detail_view_parallax_scrollview);
		
		// Check if there are any from the savedInstanceState bundle
		itemUri = (Uri) ((savedInstanceState == null) ? 
			null : savedInstanceState.getParcelable(AppProviderContentContract.SELECTED_NOTIFICATION_ITEM_URI));
	}

	@Override
	public void onResume() {
		super.onResume();
		/* No drawer item must be selected at this point (the position of this fragment is not visible in the 
		 * drawer), this is a detail fragment. */
		((AppBaseActivity) getActivity()).getAppDrawerHelper().clearSelectedItemInDrawer();
		
		// Method including all the parameterizations for the ScrollView
		((AppBaseActivity) this.getActivity()).getAppItemDetailViewHelper().setupDetailView(this, parallaxScrollView);

        // Get the itemUri object from the bundle passed in the fragment from the holder activity
        itemUri = this.getArguments().getParcelable(AppProviderContentContract.SELECTED_NOTIFICATION_ITEM_URI);
		LogUtils.LOGD(LOG_TAG, "The itemUri is: " + itemUri);

		String[] cursorProjection = AppContentProjection.NOTIFICATIONS_PROJECTION;
		// Run the cursor query asynchronously
		final String sortOrder = AppDBTableColumns.NOTIFICATION_INTERNAL_ID + " ASC";
		new AppAsyncQueryHandler(this.getActivity()).startQuery(AppAsyncQueryHandler.ITEM_DETAIL_QUERY, null, itemUri,
			cursorProjection, null, null, sortOrder);
	}

	@Override
	public void onPause() {
		super.onPause();

		((AppBaseActivity) this.getActivity()).getAppItemDetailViewHelper().removeGlobalListener();
	}
}