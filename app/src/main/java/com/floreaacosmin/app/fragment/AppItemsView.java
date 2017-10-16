package com.floreaacosmin.app.fragment;

import java.util.Arrays;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.floreaacosmin.app.activity.AppBaseActivity;
import com.floreaacosmin.app.cursor_adapter.AppArticlesCursorAdapter;
import com.floreaacosmin.app.content_provider.AppContentProjection;
import com.floreaacosmin.app.content_provider.AppCursorLoaderContract;
import com.floreaacosmin.app.content_provider.AppProviderContentContract;
import com.floreaacosmin.app.content_provider.AppProviderURIContract;
import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.toolbox.VelocityListView;
import com.floreaacosmin.app.ui.AppItemsSearchView;
import com.floreaacosmin.app.utils.AppItemsListViewHelper;
import com.floreaacosmin.notifier.R;

public class AppItemsView extends Fragment implements LoaderCallbacks<Cursor> {
    
    public AppItemsView(){}

    private final String LOG_TAG = LogUtils.makeLogTag(AppItemsView.class);
    
	private View rootView;
	private AppArticlesCursorAdapter appArticlesCursorAdapter;
	private static Loader<Cursor> appArticlesCursorLoader;
    private AppItemsSearchView appItemsSearchView;
	private String searchFilter;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Must be set to true so invalidateOptionsMenu() can be called
		setHasOptionsMenu(true);	
	}
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		final int fragmentIcon = R.drawable.icon_transparent;
        rootView = inflater.inflate(R.layout.app_items_view, container, false);
        
		// Set the corresponding icon for the view
		this.getActivity().getActionBar().setIcon(fragmentIcon);
	
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Enabled in order to save the state across different changed (including screen configuration)
		setRetainInstance(true);

        /* Setup the search view inside the custom view of the ActionBar. This method must run in
		 * onResume in order to execute each time the activity is resumed. */
        appItemsSearchView = new AppItemsSearchView(this);

		// Instantiate ListView Object
		VelocityListView velocityListView = rootView.findViewById(R.id.app_items_view_list_view);

		// Method including all the parameterizations for the ListView 
		new AppItemsListViewHelper(this, velocityListView, appItemsSearchView.getSearchIcon());

		// Set the data adapter for this ListView
		appArticlesCursorAdapter = new AppArticlesCursorAdapter(this.getActivity());
		velocityListView.setAdapter(appArticlesCursorAdapter);	
		
		/* The LoaderManager class is associated with an Activity or Fragment. Used to manage one 
		 * or more Loader instances. This helps an application manage longer-running operations in
		 * conjunction with the Activity or Fragment life cycle; the most common use of this is with
		 * a CursorLoader. */
		appArticlesCursorLoader = getLoaderManager().initLoader
			(AppCursorLoaderContract.GLOBAL_LOADER, this.getArguments(), this);
	}

	@Override
	public void onResume() {
		super.onResume();
	
		/* This is called in order to make the search icon appear after 
		 * the screen (the fragment view) has been resumed. */
		this.getActivity().invalidateOptionsMenu();
		
		// Refresh the cursor loader to reflect changes made in the authors selection fragment if any
		getLoaderManager().getLoader(AppCursorLoaderContract.GLOBAL_LOADER).onContentChanged();
	}

	// Called when invalidateOptionsMenu() is triggered
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

        appItemsSearchView.hideSearchView(
				((AppBaseActivity) getActivity()).getAppDrawerHelper().getAppActionBarDrawerToggle().getShouldGoInvisible());
	}

	@Override
	public void onPause() {
		super.onPause();
		/* Close the SearchView if the fragment is paused (application goes into the background 
		 * or the fragment is changed. */
        appItemsSearchView.hideSearchView(true);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		CursorLoader cursorLoader;
		String[] cursorProjection;
		String[] searchCursorSelectionArgs;
		String searchFilterWild;
		String searchCursorSelection;
		String caseCursorSelection = "";
		String finalCursorSelection;

		if (id ==  AppCursorLoaderContract.GLOBAL_LOADER) {
		    /* Add wild characters to searchFilter in order to pass from the Cursor Loader to 
		    the underlying SQL query */
		    searchFilterWild = "%" + searchFilter + "%";
			
		    // A Cursor Projection defines the columns that will be returned for each row
			cursorProjection = AppContentProjection.NOTIFICATIONS_PROJECTION;

			LogUtils.LOGD(LOG_TAG, "The cursor projection is: " + Arrays.toString(cursorProjection));
			
		    // Set filter depending on what was typed in the ActionBar SearchView
		    if (searchFilter != null)
		    {
		    	/* If it needs to be filtered after multiple columns than the extra column needs to 
		    	 * be added with "OR" between. */
		    	searchCursorSelection = AppDBTableColumns.NOTIFICATION_NAME + " LIKE ?";
		    	// The array has as many entries as the number of columns that needs to be filtered
		    	searchCursorSelectionArgs = new String[]{searchFilterWild};
		    } else {
		    	searchCursorSelection = null;
		    	searchCursorSelectionArgs = null;
			}
		    // If the passed bundle is not null than get the case filter from it 
		    if (args != null) {
		    	caseCursorSelection = args.getString(AppProviderContentContract.NOTIFICATIONS_CASE_FILTER_KEY);
		    }
		    
		    /* In the beginning of the logic the final cursor filter must be set to null in order 
		     * to avoid previous cases when it has been used already. */
		    finalCursorSelection = null;
		    // Only the case filter is passed
		    if (caseCursorSelection != null) {
		    	finalCursorSelection = caseCursorSelection;
		    }
		    // Only the search filter is passed
		    if (finalCursorSelection == null) {
		    	if (searchCursorSelection != null) {
		    		finalCursorSelection = searchCursorSelection;	
		    	} else {
					finalCursorSelection = null;
				}
		    // Both case and search filters are passed and are used in the selection clause	
		    } else {
		    	if (searchCursorSelection != null) {
		    		finalCursorSelection = finalCursorSelection + " AND (" + searchCursorSelection + ")";	
		    	}
		    }
		    
			/* CursorLoader loads data backed by a ContentProvider. The selection arguments string array 
			 * is always passed to the cursor loader constructor as it does not influence (it is considered 
			 * extra as there are cases where it is not used at all. */
		    cursorLoader = new CursorLoader(this.getActivity(), 
		    	AppProviderURIContract.CONTENT_NOTIFICATIONS_URI, cursorProjection, finalCursorSelection,
		    	searchCursorSelectionArgs, null);
		    
		    return cursorLoader; 
		} else {
			return null;	
		}
	}

	// Called when a previously created loader has finished its load.	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == AppCursorLoaderContract.GLOBAL_LOADER) {
			if (appArticlesCursorAdapter != null && data != null) {
			    /* Swap the new cursor in. The framework will take care of 
			     * closing the old cursor once we return. */
				appArticlesCursorAdapter.swapCursor(data);	
			}	
		}
	}

	// Called when a previously created loader is being reset.	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == AppCursorLoaderContract.GLOBAL_LOADER) {
			if (appArticlesCursorAdapter != null) {
			    /* This is called when the last Cursor provided to onLoadFinished() 
			     * above is about to be closed.  We need to make sure we are no 
			     * longer using it. */
				appArticlesCursorAdapter.swapCursor(null);	
			}	
		}	
	}
	
	public void setSearchFilter(String cf) {
		searchFilter = cf;
	}
	
	public void restartLoader() {
		getLoaderManager().restartLoader(AppCursorLoaderContract.GLOBAL_LOADER, this.getArguments(), this);
	}
		
	public static void refreshArticlesCursorLoader() {
		if (appArticlesCursorLoader != null) {
			appArticlesCursorLoader.onContentChanged();
		}
	}
}