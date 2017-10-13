package com.floreaacosmin.app.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.floreaacosmin.app.utils.AppShimmerHelper;
import com.floreaacosmin.notifier.R;
import com.romainpiel.shimmer.ShimmerTextView;

class AppDrawerHeader {

	public void setupHeaderView(Context context, ListView drawerLv) {
        // Instantiate the Drawer ListView Header Layout
		FrameLayout drawerHeaderView = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.drawer_header_view, drawerLv, false);
        // The Header View must not be active
     	drawerHeaderView.setActivated(false);
     	
     	// Instantiate the ShimmerTextView object
		ShimmerTextView	shimmerTextView = drawerHeaderView.findViewById(R.id.drawer_header_view_shimmer_textview);
        // Apply the shimmer effect
     	new AppShimmerHelper().setShimmerEffect(shimmerTextView);
     	
        // The Header View has no data and is set as not selectable
     	drawerLv.addHeaderView(drawerHeaderView, null, false);
	}
}