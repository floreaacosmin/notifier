package com.floreaacosmin.app.utils;

import android.graphics.RectF;
import android.view.View;
import android.widget.ListView;

public class AppUIFunctions {

    private static AppUIFunctions instance;

    private AppUIFunctions(){}

    public static synchronized AppUIFunctions getInstance() {
        if (instance == null) {
            instance = new AppUIFunctions();
        }
        return instance;
    }
	
    // Get the 4 float coordinates for a rectangular view     
    @SuppressWarnings("UnusedReturnValue")
    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    // Clamping is the process of limiting a position to an area	
    public float clamp(float value) {
        return Math.max(Math.min(value, 1.0f), 0.0f);
    }
    
    /* Return Header height, take care of the ListView header height in the
     * calculation if the first visible position is >= 1 */    
    public int getScrollY(ListView lv, View lvheader) {
        View listViewChild = lv.getChildAt(0);
        if (listViewChild == null) {
            return 0;
        }

        int firstVisiblePosition = lv.getFirstVisiblePosition();
        int top = listViewChild.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = lvheader.getHeight();
        }

        return -top + firstVisiblePosition * listViewChild.getHeight() + headerHeight;
    }    
    
	/* On ListView scroll, you have to move & scale depending on the header translation ratio. The main
	 * principle is to make a “diff” between the transparent (i.e. invisible) ActionBar icon view and 
	 * the header logo view. This “diff” is calculated using the Rect on screen of the 2 views and results 
	 * in scaleX, scaleY, translationX and translationY values. Finally, those values are used to translate
	 * and scale the header logo view until it matches the transparent ActionBar icon view. */    
    public void interpolate(View view1, View view2, float interpolation, RectF r1, RectF r2, View headerv) {
        getOnScreenRect(r1, view1);
        getOnScreenRect(r2, view2);

        float scaleX = 1.0F + interpolation * (r2.width() / r1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (r2.height() / r1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (r2.left + r2.right - r1.left - r1.right));
        float translationY = 0.5F * (interpolation * (r2.top + r2.bottom - r1.top - r1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - headerv.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }
}