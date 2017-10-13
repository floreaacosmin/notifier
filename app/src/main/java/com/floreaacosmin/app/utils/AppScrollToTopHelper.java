package com.floreaacosmin.app.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

import com.floreaacosmin.app.toolbox.VelocityListView;
import com.floreaacosmin.app.toolbox.VelocityListView.OnVelocityListViewListener;
import com.floreaacosmin.notifier.R;

public class AppScrollToTopHelper {

    private final int BIT_VISIBILITY = 0x01;
    private final int BIT_ANIMATION = 0x02;

    private final int SCROLL_TO_TOP_HIDDEN = 0;
    @SuppressWarnings("FieldCanBeLocal")
    private final int SCROLL_TO_TOP_HIDING = BIT_ANIMATION;
    @SuppressWarnings("FieldCanBeLocal")
    private final int SCROLL_TO_TOP_SHOWN = BIT_VISIBILITY;
    @SuppressWarnings("FieldCanBeLocal")
    private final int SCROLL_TO_TOP_SHOWING = BIT_ANIMATION | BIT_VISIBILITY;

    private VelocityListView velocityListView;
    private TextView scrollToTopView;
    private ViewPropertyAnimator viewAnimator;
    private int velocityAbsoluteThreshold;
    private int scrollToTopState = SCROLL_TO_TOP_HIDDEN;	
	
    
    public void setupVelocity(Fragment fragment, VelocityListView velocityLV) {
    	// Store a local reference to the received Velocity ListView
    	velocityListView = velocityLV;

        final int VELOCITY_ABSOLUTE_THRESHOLD = 5500;

        velocityAbsoluteThreshold = (int) (VELOCITY_ABSOLUTE_THRESHOLD * 
        	fragment.getActivity().getResources().getDisplayMetrics().density + 0.5f);
        
        scrollToTopView = fragment.getView().findViewById(R.id.app_items_view_list_view_scroll_to_top_button);
        scrollToTopView.setOnClickListener(new ViewOnClickListener());
        
        viewAnimator = scrollToTopView.animate();
        
        velocityListView.setOnVelocityListener(new OnVelocityLVListener());        
    }
    
    private class OnVelocityLVListener implements OnVelocityListViewListener {
    	@Override
        public void onVelocityChanged(int velocity) {
            if (velocity > 0) {
                if (Math.abs(velocity) > velocityAbsoluteThreshold) {
                    if ((scrollToTopState & BIT_VISIBILITY) == 0) {
                        viewAnimator.translationY(0).setListener(new OnShownViewListener());
                        scrollToTopState = SCROLL_TO_TOP_SHOWING;
                    }
                }
            } else {
                if ((scrollToTopState & BIT_VISIBILITY) == BIT_VISIBILITY) {
                    viewAnimator.translationY(-scrollToTopView.getHeight()).setListener(new OnHiddenViewListener());
                    scrollToTopState = SCROLL_TO_TOP_HIDING;
                }
            }
        }
    }
    
    private class ViewOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			velocityListView.requestPositionToScreen(0, true);
		}
    }
    
    private class OnShownViewListener implements AnimatorListener {
		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
            scrollToTopState = SCROLL_TO_TOP_SHOWN;			
		}

		@Override
		public void onAnimationCancel(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}
    }
    
    private class OnHiddenViewListener implements AnimatorListener {
		@Override
		public void onAnimationStart(Animator animation) {
			scrollToTopState = SCROLL_TO_TOP_HIDDEN;			
		}

		@Override
		public void onAnimationEnd(Animator animation) {
		}

		@Override
		public void onAnimationCancel(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}
    }    
}