package com.floreaacosmin.app.drawer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.floreaacosmin.app.activity.AppMainActivity;
import com.floreaacosmin.app.data_processor.AppAuthorsAsyncTask;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.notifier.R;

public class AppDrawerHelper {

	public AppDrawerHelper(Activity activity){
		this.activity = activity;
	}

	private final String LOG_TAG = LogUtils.makeLogTag(AppDrawerHelper.class);
	
	private final Activity activity;
	private DrawerLayout drawerLayout;
	private ListView drawerListView;
	private ArrayList<AppDrawerItem> appDrawerMenuItems;
	private AppDrawerListAdapter appDrawerListAdapter;

	private AppActionBarDrawerToggle appActionBarDrawerToggle;
	
	public AppActionBarDrawerToggle getAppActionBarDrawerToggle() {
		return appActionBarDrawerToggle;
	}
	
	@SuppressWarnings("deprecation")
	public void setupDrawer() {
        
		// Instantiate the drawer menu items array
		appDrawerMenuItems = new AppDrawerItemsList();

        // Instantiate the ListView Drawer Adapter
        appDrawerListAdapter = new AppDrawerListAdapter(activity, appDrawerMenuItems);
        // Instantiate the ListView used in the Drawer Layout
        drawerListView = activity.findViewById(R.id.app_main_view_drawer_list);

        /* Update the Drawer with dynamic added content. This is placed here besides after
         * downloading new items, because when the activity is distroyed, the drawer menu
         * must be setup again. */
        updateDrawerMenuItems();

        // Add the drawer header view to the ListView
        new AppDrawerHeader().setupHeaderView(activity, drawerListView);
        
        // Initialize drawer position to 0
        drawerListView.setTag(0);
        
        // Handle click inside the Drawer ListView
        drawerListView.setOnItemClickListener(new DrawerListViewOnItemClickListener());
        
        // Set the list adapter to the navigation drawer ListView
        drawerListView.setAdapter(appDrawerListAdapter);
        // Instantiate the Drawer Layout 
        drawerLayout = activity.findViewById(R.id.app_main_view_drawer_layout);

        /* Rather than implementing the DrawerLayout.DrawerListener, if the activity includes the 
         * action bar, you can instead extend the ActionBarDrawerToggle class. The ActionBarDrawerToggle 
         * implements DrawerLayout.DrawerListener so you can still override those callbacks, but it also 
         * facilitates the proper interaction behavior between the action bar icon and the navigation 
         * drawer. */
        appActionBarDrawerToggle = new AppActionBarDrawerToggle(activity, drawerLayout);
        // Set the drawer toggle as the DrawerListener		
        drawerLayout.setDrawerListener(appActionBarDrawerToggle);
    }

    public void updateDrawerMenuItems() {
        /* Besides the static menu items, the drawer is populated with categories based on the
		 * existing retrieved authors from the local cached. */
        try {
            List<String> authors = new AppAuthorsAsyncTask(activity).execute().get();
            for (String author : authors) {
                appDrawerMenuItems.add(appDrawerMenuItems.size(), (new AppDrawerItem(author,
                        R.drawable.icon_about, R.color.transparent)));

                /* The adapter must notify the ListView when the underlying data has
                 * changed otherwise an exception is triggered. As this function is called
                 * from different locations and phases, as a safe measure the adapter is
                 * checked if it is instantiated first. */
                if (appDrawerListAdapter != null) {
                    appDrawerListAdapter.notifyDataSetChanged();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Drawer Items Click Listener
    private class DrawerListViewOnItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String drawerItemName = appDrawerMenuItems.get(position - 1).getName();

        	LogUtils.LOGD(LOG_TAG, "Selected position in drawer: " + position + ", with id: " + id +
                    " and name: " + drawerItemName);

            /* Set the selected item in the drawer list adapter, in order to highlight it. Also this
             * method contains in it the function to open the respective fragment. */
            selectItemInDrawer(position, drawerItemName);
        }
    }

    private void setActionBarTitle(int position) {
    	// Avoid the "ArrayIndexOutOfBoundsException" exception when it occurs
    	if (position != -1) {
        	activity.getActionBar().setTitle(appDrawerMenuItems.get(position).getName());	
    	}    	
    }

	public void toggleDrawer() {
		if (!drawerLayout.isDrawerOpen(drawerListView)) {
			drawerLayout.openDrawer(drawerListView);
		} else {
			drawerLayout.closeDrawer(drawerListView);
		}
	}

    public void selectItemInDrawer(int position, String drawerItemName) {
    	// An array begins at 0 while the ListView position begins with 1
    	int arrayPosition = position - 1;
    	
    	/* Get current position in the drawer in case of selecting an article in 
    	 * order to know where to come back. */
    	drawerListView.setTag(position - 1);
    	LogUtils.LOGD(LOG_TAG, "Save last position in drawer to: " + arrayPosition);        	
    	
        // Update selected item then
        drawerListView.setItemChecked(position, true);
        drawerListView.setSelection(position);

    	// Display view for selected Navigation Drawer Item
        ((AppMainActivity) activity).getAppFragmentUtils().displaySelectedView(position, null, drawerItemName);
    	
        appDrawerListAdapter.setSelectedItem(arrayPosition);
        // Set the ActionBar title with the Drawer menu item name
    	setActionBarTitle(arrayPosition);
        
    	// Close the drawer
    	drawerLayout.closeDrawer(drawerListView);    	
    }
    
    public void clearSelectedItemInDrawer() {
		final int POSITION_NONE = 1000000;

    	/* The logic is to set a value for the selected item which does not 
    	 * matches any real item in the drawer. */
    	appDrawerListAdapter.setSelectedItem(POSITION_NONE);	
    	// The data has changed and the drawer list must be refreshed
    	appDrawerListAdapter.notifyDataSetChanged();
    }
    
    // Get last selected position in drawer and set it
    public void setPreviousItemSelectedInDrawer() {
    	int previousPosition = Integer.valueOf(drawerListView.getTag().toString());
    	LogUtils.LOGD(LOG_TAG, "Retrieve last position in drawer as: " + previousPosition);    		

        appDrawerListAdapter.setSelectedItem(previousPosition);
    	appDrawerListAdapter.notifyDataSetChanged();
    }
}