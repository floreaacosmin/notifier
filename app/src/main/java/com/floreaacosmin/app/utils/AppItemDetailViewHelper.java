package com.floreaacosmin.app.utils;

import android.app.Fragment;
import android.database.Cursor;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.floreaacosmin.app.application.AppBaseApplication;
import com.floreaacosmin.app.cursor_adapter.AppAdapterHelper;
import com.floreaacosmin.app.content_provider.AppProviderURIContract;
import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.ui.AppActionBarHelper;
import com.floreaacosmin.notifier.R;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;

public class AppItemDetailViewHelper {

	private final String LOG_TAG = LogUtils.makeLogTag(AppItemDetailViewHelper.class);

	private Fragment fragment;
	private View headerView;
	private TextView itemAuthor;
	private TextView itemName;
	private TextView itemDate;
	private WebView itemBody;
	private NetworkImageView itemImage;
	private ImageView headerImageBack;
	private ImageView headerImageFront;
	private DetailViewOnScrollChangedListener detailViewOnScrollChangedListener;
	private ParallaxScrollView parallaxScrollView;

	public void setupDetailView(Fragment f, ParallaxScrollView pScrollView) {
		// Save the received references
		fragment = f;
		parallaxScrollView = pScrollView;
		
		/* A notification is sent in order to refresh the content of the ListView 
		 * and update the article read/ unread status. */
		(fragment.getActivity()).getContentResolver().notifyChange
				(AppProviderURIContract.CONTENT_NOTIFICATIONS_URI, null);

		// Instantiate the needed UI objects
		headerView = fragment.getView().findViewById(R.id.app_item_detail_view_header);
		itemAuthor = fragment.getView().findViewById(R.id.app_item_detail_view_author_name);
		itemName = fragment.getView().findViewById(R.id.app_item_detail_view_title);
		itemDate = fragment.getView().findViewById(R.id.app_item_detail_view_item_date);
		itemBody = fragment.getView().findViewById(R.id.app_item_detail_view_article_content);
		itemImage = fragment.getView().findViewById(R.id.author_row_layout_image);
		headerImageBack = fragment.getView().findViewById(R.id.app_items_view_header_hill_back);
		headerImageFront = fragment.getView().findViewById(R.id.app_items_view_header_hill_front);

		// Disable the sound effects for the WebView
		itemBody.setSoundEffectsEnabled(false);
		// Disable JavaScript as it is not needed
		itemBody.getSettings().setJavaScriptEnabled(false);
		// Keep the screen on
		itemBody.setKeepScreenOn(true);

		detailViewOnScrollChangedListener = new DetailViewOnScrollChangedListener();

		// Register the ScrollListener for the ScrollView
		parallaxScrollView.getViewTreeObserver().addOnScrollChangedListener(detailViewOnScrollChangedListener);
	}

	public void removeGlobalListener() {
		parallaxScrollView.getViewTreeObserver().removeOnScrollChangedListener(detailViewOnScrollChangedListener);
	}

	private class DetailViewOnScrollChangedListener implements OnScrollChangedListener {
		@Override
		public void onScrollChanged() {

			// Get the scroll position
			float currentScrollPosition = parallaxScrollView.getScrollY();

			// Translate the header items
			headerImageBack.setTranslationY(currentScrollPosition);
			headerImageFront.setTranslationY(currentScrollPosition / 2);
			// Translate the item title
			itemName.setTranslationY(currentScrollPosition / 2);

			// Get the translation ratio value
			float headerTranslationRatio = AppUIFunctions.getInstance().clamp(headerView.getTranslationY());

			// Hide or show the ActionBar Title depending on the view scroll direction
			AppActionBarHelper.getInstance().setActionBarTitleAlpha
					(fragment.getActivity(), (headerTranslationRatio == 1.0) ? 0.0f : 1.0f);
		}
	}

	// Method used to fill the UI with the data from the Content Provider using the Cursor object 
	public void fillData(Cursor cursor) {
		/* This method is surrounded by a try/ catch block because sometimes when executing 
		 * the application very fast (using utilities like Monkey for example) is causes an 
		 * "NullPointerException" on the fragment object which normally cannot be reproduced. */
		try {
			int NOTIFICATION_AUTHOR_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_AUTHOR);
			int NOTIFICATION_NAME_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_NAME);
			int NOTIFICATION_CONTENT_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_CONTENT);
			int NOTIFICATION_DATE_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_DATE);
			int NOTIFICATION_IMAGEURL_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_IMAGEURL);

			itemAuthor.setText(cursor.getString(NOTIFICATION_AUTHOR_INDEX));
			itemName.setText(cursor.getString(NOTIFICATION_NAME_INDEX));
			itemDate.setText(cursor.getString(NOTIFICATION_DATE_INDEX));
			itemImage.setImageUrl(cursor.getString(NOTIFICATION_IMAGEURL_INDEX),
					((AppBaseApplication) fragment.getActivity().getApplicationContext()).getImageLoader());

			// Load the HTML content in the WebView
			AppAdapterHelper.getInstance().loadHtmlContent(itemBody, cursor.getString(NOTIFICATION_CONTENT_INDEX));

		} catch (Exception e) {
			LogUtils.LOGD(LOG_TAG, "The caught exception was: ", e);
		}
	}

	public void refreshWebView() {
		if (itemBody != null && itemBody.isShown()) {
			itemBody.reload();

			LogUtils.LOGD(LOG_TAG, "The WebView was refreshed.");
		}
	}
}