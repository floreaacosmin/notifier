package com.floreaacosmin.app.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;

import com.floreaacosmin.app.toolbox.AlphaForegroundColorSpan;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.notifier.R;

public class AppActionBarHelper {

	private static AppActionBarHelper instance;

	private AppActionBarHelper() {
		
		if (Build.MANUFACTURER.contains(LG_MANUFACTURER) && Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
	        LogUtils.LOGD(LOG_TAG, "The device is an " + Build.MANUFACTURER + " running Android" +
	        	+ Build.VERSION.SDK_INT + ", avoiding the exception caused by the SpannableString set in the ActionBar");
	        
	        avoidException = true;
	    }
	}

	public static synchronized AppActionBarHelper getInstance() {
		if (instance == null) {
			instance = new AppActionBarHelper();
		}
		return instance;
	}

	@SuppressWarnings("FieldCanBeLocal")
	private final String LOG_TAG = LogUtils.makeLogTag(AppActionBarHelper.class);

	private int actionBarHeight;
	private final TypedValue typedValue = new TypedValue();
	private boolean avoidException = false;
	
	private static final String LG_MANUFACTURER = "LG";
	
	public void setupActionBar(Activity a){	
	    // Request ActionBar (disabled by default in the application theme)
		a.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        // Set ActionBar Icon
		a.getActionBar().setIcon(R.drawable.icon_transparent);
	}
	
	// Return the height of the ActionBar
    public int getActionBarHeight(Activity a) {
    	Theme theme = a.getTheme();

    	Resources resources = a.getResources();
    	
        if (actionBarHeight != 0) {
        	// Avoid calculating it again if it was already calculated before 
        	return actionBarHeight;
        } else {
            theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, resources.getDisplayMetrics());
            return actionBarHeight;
        }
    }	

	// Get ActionBar Icon View in order to make a translation    
    public View getActionBarIconView(Activity a) {
		return a.findViewById(android.R.id.home);
    }
    
	/* Set the alpha value on the AlphaForegroundColorSpan object. The same AlphaForegroundColorSpan 
	 * and SpannableString instances must be kept for performance reasons (avoiding GC) */	
	public void setActionBarTitleAlpha(Activity a, float alpha) {
		/* On fast opening and closing of the application for several times, the passed 
		 * activity object may become null causing a NullPointerException exception to 
		 * be thrown, so before doing anything this is checked. */
		if (a != null) {
			// Instantiate resources
			SpannableString spannableString = new SpannableString(a.getString(R.string.actionbar_title));
		    @SuppressWarnings("deprecation") int actionBarTitleColor = a.getResources().getColor(R.color.actionbar_title_color);
			AlphaForegroundColorSpan alphaForegroundColorSpan = new AlphaForegroundColorSpan(actionBarTitleColor);
	        alphaForegroundColorSpan.setAlpha(alpha);
	        spannableString.setSpan(alphaForegroundColorSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	        
	        if (avoidException) {
		        a.getActionBar().setTitle(spannableString.toString());
	        } else {
		        a.getActionBar().setTitle(spannableString);
			}
		}
    }
}