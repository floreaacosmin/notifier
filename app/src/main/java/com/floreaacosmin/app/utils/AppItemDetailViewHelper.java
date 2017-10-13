package com.floreaacosmin.app.utils;

import android.app.Fragment;
import android.database.Cursor;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
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
	private TextView articleAuthor;
	private TextView articleName;
	private TextView articleDate;
	private WebView articleBody;
	private NetworkImageView authorImage;
	private ImageView headerCityBackDay;
	private ImageView headerCityFrontDay;
	private DetailViewOnScrollChangedListener detailViewOnScrollChangedListener;
	private ParallaxScrollView parallaxScrollView;

	private int NOTIFICATION_AUTHOR_INDEX;
	private int NOTIFICATION_NAME_INDEX;
    private int NOTIFICATION_CONTENT_INDEX;
    private int NOTIFICATION_DATE_INDEX;

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
		articleAuthor = fragment.getView().findViewById(R.id.app_item_detail_view_author_name);
		articleName = fragment.getView().findViewById(R.id.app_item_detail_view_title);
		articleDate = fragment.getView().findViewById(R.id.app_item_detail_view_item_date);
		articleBody = fragment.getView().findViewById(R.id.app_item_detail_view_article_content);
		authorImage = fragment.getView().findViewById(R.id.authors_row_layout_image);
		headerCityBackDay = fragment.getView().findViewById(R.id.app_items_view_header_hill_back);
		headerCityFrontDay = fragment.getView().findViewById(R.id.app_items_view_header_hill_front);

		// Disable the sound effects for the WebView
		articleBody.setSoundEffectsEnabled(false);
		// Disable JavaScript as it is not needed
		articleBody.getSettings().setJavaScriptEnabled(false);
		// Keep the screen on
		articleBody.setKeepScreenOn(true);

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
			headerCityBackDay.setTranslationY(currentScrollPosition);
			headerCityFrontDay.setTranslationY(currentScrollPosition / 2);
			// Translate the item title
			articleName.setTranslationY(currentScrollPosition / 2);

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
			NOTIFICATION_AUTHOR_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_AUTHOR);
			NOTIFICATION_NAME_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_NAME);
			NOTIFICATION_CONTENT_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_CONTENT);
			NOTIFICATION_DATE_INDEX = cursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_DATE);

			String articleAuthorValue = cursor.getString(NOTIFICATION_AUTHOR_INDEX);
			String articleNameValue = cursor.getString(NOTIFICATION_NAME_INDEX);

			articleAuthor.setText(articleAuthorValue);
			articleName.setText(articleNameValue);
/*			authorImage.setImageUrl(cursor.getString(AUTHOR_IMAGE_URL_INDEX),
					((AppBaseApplication) fragment.getActivity().getApplication()).getImageLoader());*/

			// Load the HTML content in the WebView
			AppAdapterHelper.getInstance().loadHtmlContent(articleBody, cursor.getString(NOTIFICATION_CONTENT_INDEX));
			// AppAdapterHelper.getInstance().loadArticleDate(articleDate, cursor, ARTICLE_DATE_UNIX_INDEX, ARTICLE_DATE_INDEX);

		} catch (Exception e) {
			LogUtils.LOGD(LOG_TAG, "The caught exception was: ", e);
		}
	}

	public void refreshWebView() {
		if (articleBody != null && articleBody.isShown()) {
			articleBody.reload();

			LogUtils.LOGD(LOG_TAG, "The WebView was refreshed.");
		}
	}
}