package com.floreaacosmin.app.utils;

import android.app.Fragment;
import android.content.ContentValues;
import android.graphics.RectF;
import android.net.Uri;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.floreaacosmin.app.content_provider.AppProviderCategoriesContract;
import com.floreaacosmin.app.content_provider.AppProviderContentContract;
import com.floreaacosmin.app.content_provider.AppProviderURIContract;
import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.app.toolbox.VelocityListView;
import com.floreaacosmin.app.ui.AppActionBarHelper;
import com.floreaacosmin.notifier.R;

public class AppItemsListViewHelper {

	private ImageView actionBarIcon;
	private int minHeaderTranslation;
	private ImageView headerLogo;
	private TextView headerLogoTitle;
	private TextView headerDetailText;
	private ImageView headerImageBack;
	private ImageView headerImageFront;
	private View visibleHeaderView;
	private View hiddenHeaderView;
	private AccelerateDecelerateInterpolator smoothInterpolator;
	private final RectF rectangle1 = new RectF();
	private final RectF rectangle2 = new RectF();
	private Fragment fragment;
	private ListView listView;
	private ListItemSelectedCommunicator listItemSelectedCommunicator;
	private AppSwipeRefreshUtils appSwipeRefreshUtilsInstance;

	public AppItemsListViewHelper(Fragment fragment, final ListView listView, CheckBox searchIcon) {
		setupListView(fragment, listView, searchIcon);
	}

	private void setupListView(Fragment fragment, final ListView listView, CheckBox searchIcon) {
		// Store content received from the Activity in local variables 
		this.fragment = fragment;
		this.listView = listView;

		/* If the activity has not implemented the interface, then the fragment throws a 
		 * ClassCastException. On success, the instantiated member holds a reference to 
		 * the activity's implementation of the interface, so that the fragment can share 
		 * events with the activity by calling methods defined by the interface */
		try {
			listItemSelectedCommunicator = (ListItemSelectedCommunicator) this.fragment.getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(this.fragment.getActivity().toString() + " must implement the interface");
		}
		
		// Instantiate the Visible Header View
        visibleHeaderView = this.fragment.getView().findViewById(R.id.app_items_view_header);
        // Instantiate the Visible Header View Logo
        headerLogo = this.fragment.getView().findViewById(R.id.app_items_view_header_logo);
        // Instantiate the Visible Header View Logo Title
        headerLogoTitle = this.fragment.getView().findViewById(R.id.app_items_view_header_logo_title);
        // Instantiate the Visible Header Case Detail Text
        headerDetailText = this.fragment.getView().findViewById(R.id.app_items_view_header_logo_subtitle);
        // Set the detail case text only if there is passed data in the bundle
        if (this.fragment.getArguments() != null) {
        	headerDetailText.setText(this.fragment.getArguments().getString(AppProviderContentContract.NOTIFICATIONS_CASE_TITLE_KEY));
        }
        
        // Instantiate the Visible Buildings Headers 
        headerImageBack = this.fragment.getView().findViewById(R.id.app_items_view_header_hill_back);
        headerImageFront = this.fragment.getView().findViewById(R.id.app_items_view_header_hill_front);
        
		smoothInterpolator = new AccelerateDecelerateInterpolator();
		int headerHeight = this.fragment.getResources().getDimensionPixelSize(R.dimen.items_header_height);
		minHeaderTranslation = -headerHeight + AppActionBarHelper.getInstance().getActionBarHeight(this.fragment.getActivity());

		// Instantiate the ActionBar Icon
		actionBarIcon = (ImageView) AppActionBarHelper.getInstance().getActionBarIconView(this.fragment.getActivity());
		// Instantiate the Hidden Header View
		hiddenHeaderView = this.fragment.getActivity().getLayoutInflater().inflate
			(R.layout.app_items_hidden_header_view, this.listView, false);
		// The Hidden Header View must not be active
		hiddenHeaderView.setActivated(false);
		// The Hidden Header View has no data and is set as not selectable
		this.listView.addHeaderView(hiddenHeaderView, null, false);
		
		// Set OnItemClickListener
		this.listView.setOnItemClickListener(new ListViewItemClickListener());

		// Set OnScrollListener
		this.listView.setOnScrollListener(new ListViewOnScrollListener(searchIcon));
			
		// Setup the velocity part needed for the ListView
		new AppScrollToTopHelper().setupVelocity(this.fragment, (VelocityListView) this.listView);
		
		// Setup the swipe refresh layout
		appSwipeRefreshUtilsInstance = new AppSwipeRefreshUtils();
		appSwipeRefreshUtilsInstance.setupSwipeLayout(this.fragment, this.listView);

		// Set the empty view for the ListView
		new AppEmptyViewUtils(this.fragment, this.listView);
	}

	private class ListViewItemClickListener implements ListView.OnItemClickListener {
		// Opens the second fragment if an entry is clicked
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			// Open the detail fragment only if the received id is valid
			if (id > 0) {
				// Update the item through the content provider in order to mark it read
				Uri itemUri = Uri.parse(AppProviderURIContract.CONTENT_NOTIFICATIONS_URI + "/" + id);
				ContentValues itemValues = new ContentValues();
				itemValues.put(AppDBTableColumns.NOTIFICATION_READ, AppProviderCategoriesContract.READ_VALUE);
				fragment.getActivity().getContentResolver().update(itemUri, itemValues, null, null);

				// Pass the selected item's Uri to the activity's implemented interface method
				listItemSelectedCommunicator.onListItemClicked(itemUri);
			}
		}
	}

	private class ListViewOnScrollListener implements AbsListView.OnScrollListener {

        private final CheckBox searchIcon;

        ListViewOnScrollListener(CheckBox searchIcon) {
            this.searchIcon = searchIcon;
        }

        @Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {	
		}
  
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            appSwipeRefreshUtilsInstance.applySwapEnableLogic();

            int scrollY = AppUIFunctions.getInstance().getScrollY(listView, hiddenHeaderView);
            // Sticky ActionBar
            visibleHeaderView.setTranslationY(Math.max(-scrollY, minHeaderTranslation));

            //Header Logo --> ActionBar Icon
            float translationRatio = AppUIFunctions.getInstance().
                    clamp(visibleHeaderView.getTranslationY() / minHeaderTranslation);
            AppUIFunctions.getInstance().interpolate(headerLogo, actionBarIcon,
                    smoothInterpolator.getInterpolation(translationRatio), rectangle1, rectangle2, visibleHeaderView);

            // ActionBar Title Alpha
            AppActionBarHelper.getInstance().setActionBarTitleAlpha
                    (fragment.getActivity(), AppUIFunctions.getInstance().clamp(5.0f * translationRatio - 4.0f));

            // Rotate the Search Icon on ListView Header translation (rotate the Search Icon by maximum 90 degrees)
            searchIcon.setRotation(AppUIFunctions.getInstance().clamp(5.0f * translationRatio - 4.0f)*-90);

            // Fast logic to get Header Logo Title visibility
            int headerLogoTitleVisiblity = (visibleHeaderView.getTranslationY() >= -40.0) ? View.VISIBLE : View.INVISIBLE;
            // Fast logic to get the list case detail text visibility
            int headerDetailTextVisiblity = (visibleHeaderView.getTranslationY() >= -300.0) ? View.VISIBLE : View.INVISIBLE;
            // Set Visibility
            headerLogoTitle.setVisibility(headerLogoTitleVisiblity);
            headerDetailText.setVisibility(headerDetailTextVisiblity);

			/* Translate the City Headers ImageViews from bottom (not visible) to top, using different
			 * ratios so it	creates a volume effect when appearing and disappearing */
            headerImageBack.setTranslationY(Math.max(scrollY, minHeaderTranslation));
            headerImageFront.setTranslationY(Math.max(scrollY, minHeaderTranslation) / 2);
        }
    }
}
