package com.floreaacosmin.app.drawer;

import com.floreaacosmin.app.activity.AppMainActivity;
import com.floreaacosmin.app.activity.AppDataResultReceiver;
import com.floreaacosmin.app.utils.AppInputUtils;
import com.floreaacosmin.notifier.R;

import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;

/* In the Navigation Drawer design guide it is stated that the contents of the action bar when the 
 * drawer is visible should be modified, such as to change the title and remove action items that 
 * are contextual to the main content. The following code is overriding DrawerLayout.DrawerListener 
 * callback methods with an instance of the ActionBarDrawerToggle class. */
@SuppressWarnings("deprecation")
public class AppActionBarDrawerToggle extends ActionBarDrawerToggle {
	
	private final Activity activity;
	private float previousSlideOffset;
	private boolean shouldGoInvisible;
	
	public boolean getShouldGoInvisible() {
		return shouldGoInvisible;
	}
	
	public void hideMenuItems(Menu menu) {
	    for(int i = 0; i < menu.size(); i++){
	        menu.getItem(i).setVisible(!getShouldGoInvisible());
	    }
	}
	
	public AppActionBarDrawerToggle(Activity a, DrawerLayout drawerLayout) {
		super(a, drawerLayout, R.drawable.icon_navigation_drawer, R.string.drawer_open, R.string.drawer_close);
		
		// Save received context in a local variable
		activity = a;
		previousSlideOffset = 0f;
	}
	
	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		super.onDrawerSlide(drawerView, slideOffset);
		
		// Hide the keyboard
		AppInputUtils.getInstance().dismissKeyboard(activity);
		// Dismiss the toast messagevif any
		((AppDataResultReceiver) activity).clearAppToastMessage();
        // Cancel the "new articles" message if present
		((AppDataResultReceiver) activity).clearAppToastMessage();
		// Cancel the exit message if present
		((AppMainActivity) activity).cancelExitMessage();
		
		if (slideOffset > previousSlideOffset && !shouldGoInvisible) {
            shouldGoInvisible = true;
            // Creates call to onPrepareOptionsMenu()
            activity.invalidateOptionsMenu();
        } else if (previousSlideOffset > slideOffset && slideOffset < 0.5f && shouldGoInvisible) {
            shouldGoInvisible = false;
            // Creates call to onPrepareOptionsMenu()
            activity.invalidateOptionsMenu();
        }
		previousSlideOffset = slideOffset;
	}
}