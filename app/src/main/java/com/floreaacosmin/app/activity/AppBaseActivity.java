package com.floreaacosmin.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;

import com.floreaacosmin.app.drawer.AppDrawerContract;
import com.floreaacosmin.app.drawer.AppDrawerHelper;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.ui.AppActionBarHelper;
import com.floreaacosmin.app.ui.AppToastMessageView;
import com.floreaacosmin.app.fragment.AppFragmentUtils;
import com.floreaacosmin.app.utils.AppItemDetailViewHelper;
import com.floreaacosmin.notifier.R;

import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

@SuppressLint("Registered")
public class AppBaseActivity extends Activity {

	private final String LOG_TAG = LogUtils.makeLogTag(AppBaseActivity.class);
	
	private AppToastMessageView exitToastMessage = null;
	private boolean doubleBackToExitPressedOnce = false;
	private Handler exitHandler;
	private Runnable exitRunnable;
	private AppFragmentUtils appFragmentUtils;
    private AppDrawerHelper appDrawerHelper;
    private ProgressBar progressBar;
    private AppItemDetailViewHelper appItemDetailViewHelper;

	public AppFragmentUtils getAppFragmentUtils() {
		if (appFragmentUtils == null) {
			// Instantiate the Fragment Utils class
			appFragmentUtils = new AppFragmentUtils(this);
		}
		return appFragmentUtils;
	}

	public AppDrawerHelper getAppDrawerHelper() {
        if (appDrawerHelper == null) {
            appDrawerHelper = new AppDrawerHelper(this);
        }
        return appDrawerHelper;
    }

    public AppItemDetailViewHelper getAppItemDetailViewHelper() {
        if (appItemDetailViewHelper == null) {
            appItemDetailViewHelper = new AppItemDetailViewHelper();
        }

        return appItemDetailViewHelper;
    }
	
	/* Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set opening transition animations
		overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
		
		// Customize the ActionBar
		AppActionBarHelper.getInstance().setupActionBar(this);
		
		// This method creates objects from the XML views
		setContentView(R.layout.app_main_view);

        // Instantiate and setup for the Navigation Drawer ListView
        getAppDrawerHelper();
	}

	// Reaction to the ActionBar menu selection
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Pass the event to ActionBarDrawerToggle, if it returns 
		 * true, then it has handled the app icon touch event. This 
		 * means the navigation drawer can be toggled on selecting 
		 * the ActionBar app icon. */
		return (getAppDrawerHelper().getAppActionBarDrawerToggle().onOptionsItemSelected(item)
				|| super.onOptionsItemSelected(item));
	}

	// Called when invalidateOptionsMenu() is triggered
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		/* If the Navigation Drawer is open, hide action items related to
		 * the content view and the ActionBar icons, else show them */
        getAppDrawerHelper().getAppActionBarDrawerToggle().hideMenuItems(menu);
		return super.onPrepareOptionsMenu(menu);
	}	

	/* When using the ActionBarDrawerToggle, it must be called during
	 * onPostCreate() and onConfigurationChanged() */
	@SuppressWarnings("deprecation")
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred
        getAppDrawerHelper().getAppActionBarDrawerToggle().syncState();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
        getAppDrawerHelper().getAppActionBarDrawerToggle().
			onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);

        // Instantiate the ProgressBar
        instantiateProgressBar();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Set closing transition animations
		overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
	}

	// Confirm exit from the activity by pressing the back button twice	
	@Override
	public void onBackPressed() {
		
		/* Execute pending transactions in the fragment manager, so recursive entries 
		 * to executePendingTransactions are avoided. */
    	if (this.getFragmentManager().executePendingTransactions()) {
    		LogUtils.LOGD(LOG_TAG, "There were pending fragment transactions");
    	}
		
		// Release memory occupied by runnable every time the back button is pressed
	    if (exitRunnable != null) {
	        exitHandler.removeCallbacks(exitRunnable);
        }
		
	    /* Get last fragment name from the BackStack (which is also displayed) and verify 
	     * if the fragment above is the item detail fragment and if true allow back navigation. */
		if (getAppFragmentUtils().getDisplayedFragmentName().contains(AppDrawerContract.NOTIFICATION_DETAIL_VIEW_TAG)) {
            getAppDrawerHelper().setPreviousItemSelectedInDrawer();
	    	super.onBackPressed();
	    // If not than there are two child cases
		} else {
			/* Check for first press and if this is the first press, display the exit 
			 * message and wait for the second press. */
			if (!doubleBackToExitPressedOnce) {
				this.doubleBackToExitPressedOnce = true;
				
				if (exitToastMessage == null) {	    
				    exitToastMessage = new AppToastMessageView(this, getResources().getString(R.string.leave_app),
                            Gravity.BOTTOM);
				}
				exitToastMessage.showMessage();
				
				// Start timer runnable to wait for the second press
			    exitHandler = new Handler();
			    if (exitRunnable == null) {
				    exitRunnable = new ExitRunnable();
			    }
			    exitHandler.postDelayed(exitRunnable, 2000);
			// If here it means the back button was pressed twice, than exit 
			} else {
				// Cancel the exit message if it exists
				cancelExitMessage();
				// Cancel AppToasts if any
				((AppDataResultReceiver) this).clearAppToastMessage();
		        // Remove the callback from the handler
		        exitHandler.removeCallbacks(exitRunnable);
		        // Call the close the application
				finish();
			}
		}
	}

    private void instantiateProgressBar() {
        progressBar = findViewById(R.id.app_data_progress_bar);
        progressBar.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(this).interpolator
                (new AccelerateInterpolator()).build());
    }

    ProgressBar getProgressBar() {
        return progressBar;
    }

	private class ExitRunnable implements Runnable {
		@Override
		public void run() {
			doubleBackToExitPressedOnce = false;
		}
	}

	public void cancelExitMessage() {
		if (exitToastMessage != null) {
			exitToastMessage.cancelMessage();
		}
	}

	// Open or close the drawer menu when the option button is used
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent e) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
            getAppDrawerHelper().toggleDrawer();
			return true;
		}
		return super.onKeyDown(keyCode, e);
	}
}	