/*
 * Copyright 2013 Lars Werkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Modified by Cosmin Florea - November 2014 

package com.floreaacosmin.app.toolbox;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

@SuppressWarnings("unused")
public class QuickReturnListView extends ListView {

	@SuppressWarnings("FieldCanBeLocal")
    private int mItemCount;
	private int mItemOffsetY[];
	private boolean scrollIsComputed = false;
	private int mHeight;
	private View rowView;
	@SuppressWarnings("FieldCanBeLocal")
	private int nScrollY;
	@SuppressWarnings("FieldCanBeLocal")
	private int nItemY;
	@SuppressWarnings("FieldCanBeLocal")
	private int position;
	
	public QuickReturnListView(Context context) {
		super(context);
	}

	public QuickReturnListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		/* Recompute the scroll values each time the number of items in the list gets changed. 
		 * This way the number of views always stays correct and exceptions are avoided. */
		adapter.registerDataSetObserver(new AdapterObserver());
	}
	
	private class AdapterObserver extends DataSetObserver {
		@Override
		public void onChanged() {
		    computeScrollY();
		}
	}
	
	public int getListHeight() {
		return mHeight;
	}
	
	public void computeScrollY() {
		// Try to compute the scroll only if an adapter has been attached
		if (getAdapter() != null) {
			/* Only the first rowView is measured because in this case because all views have the same height. 
			 * If a custom cursor adapter is used, that has multiple rows layouts than the rowView measurement 
			 * calculation must be included in the for loop so it can be calculated continuously, but this 
			 * will cause serious performance issues. */
			if (rowView == null) {
				rowView = getAdapter().getView(0, null, this);
				rowView.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));	
			}
			
			mHeight = 0;
			mItemCount = getAdapter().getCount();
			
			if (mItemOffsetY == null || mItemOffsetY.length != mItemCount) {
				mItemOffsetY = new int[mItemCount];
			}
			for (int i = 0; i < mItemCount; ++i) {
				mItemOffsetY[i] = mHeight;
				mHeight += rowView.getMeasuredHeight();
			}
			scrollIsComputed = true;			
		} else {
			scrollIsComputed = false;
		}
	}

	public boolean scrollYIsComputed() {
		return scrollIsComputed;
	}

	public int getComputedScrollY() {
        
		position = getFirstVisiblePosition();
		nItemY = rowView.getTop();
		nScrollY = mItemOffsetY[position] - nItemY;
		
		return nScrollY;
	}
}